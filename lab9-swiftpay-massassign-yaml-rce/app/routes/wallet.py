from datetime import datetime, timedelta
from decimal import Decimal, InvalidOperation

from flask import Blueprint, render_template, request

from extensions import db
from models import Transaction, User
from routes.decorators import login_required

wallet_bp = Blueprint("wallet", __name__)


@wallet_bp.route("/dashboard")
@login_required
def dashboard(user):
    return render_template("dashboard.html", user=user)


@wallet_bp.route("/transfer", methods=["GET", "POST"])
@login_required
def transfer(user):
    error = None
    success = None

    if request.method == "POST":
        recipient_username = request.form.get("recipient", "").strip()
        raw_amount = request.form.get("amount", "")
        note = request.form.get("note", "")[:255]

        try:
            amount = Decimal(raw_amount)
        except InvalidOperation:
            amount = None

        recipient = User.query.filter_by(username=recipient_username).first()

        if amount is None or amount <= 0:
            error = "Enter a valid, positive amount."
        elif not recipient:
            error = "No such recipient."
        elif recipient.id == user.id:
            error = "You can't transfer money to yourself."
        elif amount > user.balance:
            error = "Insufficient balance."
        else:
            # Re-derive today's total sent straight from the database on every request
            # (never trust a client-supplied running total) so the daily cap can't be
            # raced or replayed past by firing several transfers back to back.
            since = datetime.utcnow() - timedelta(hours=24)
            sent_today = (
                db.session.query(db.func.coalesce(db.func.sum(Transaction.amount), 0))
                .filter(Transaction.sender_id == user.id, Transaction.created_at >= since)
                .scalar()
            )
            if Decimal(sent_today) + amount > user.daily_limit:
                error = "This transfer would exceed your daily transfer limit."
            else:
                user.balance = user.balance - amount
                recipient.balance = recipient.balance + amount
                db.session.add(Transaction(
                    sender_id=user.id,
                    recipient_id=recipient.id,
                    amount=amount,
                    note=note,
                ))
                db.session.commit()
                success = f"Sent {amount} to {recipient.username}."

    return render_template("transfer.html", user=user, error=error, success=success)


@wallet_bp.route("/statement")
@login_required
def statement(user):
    q = request.args.get("q", "").strip()

    query = Transaction.query.filter(
        (Transaction.sender_id == user.id) | (Transaction.recipient_id == user.id)
    )

    if q:
        # Free-text filter over the note field. The %wildcard% pattern is built with an
        # f-string, but the resulting pattern is still handed to ilike() as a single bound
        # parameter - SQLAlchemy binds it, it never gets spliced into the query text -
        # so this looks similar to the recovery flow's string-built query but isn't the
        # same shape at all.
        query = query.filter(Transaction.note.ilike(f"%{q}%"))

    txns = query.order_by(Transaction.created_at.desc()).all()
    return render_template("statement.html", user=user, txns=txns, q=q)
