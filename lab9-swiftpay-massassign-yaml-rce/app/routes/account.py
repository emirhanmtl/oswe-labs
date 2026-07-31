import psycopg2
from flask import Blueprint, current_app, jsonify, redirect, render_template, request, session, url_for

from extensions import db
from models import User
from routes.decorators import login_required

account_bp = Blueprint("account", __name__)


def _get_raw_conn():
    cfg = current_app.config["RAW_DB"]
    return psycopg2.connect(
        host=cfg["host"], dbname=cfg["dbname"], user=cfg["user"], password=cfg["password"]
    )


@account_bp.route("/account/recover", methods=["GET", "POST"])
def recover():
    """Account recovery for people who don't have their password handy: verify the
    email + 4-digit code we texted them at signup, then drop them straight into a
    session (no second password prompt - they already proved they own the phone).

    This talks to Postgres with its own psycopg2 connection instead of the ORM -
    it was lifted almost unchanged from the old billing-system codebase during the
    migration and nobody's gotten around to rewriting it yet.
    """
    error = None

    if request.method == "POST":
        email = request.form.get("email", "")
        code = request.form.get("security_code", "")

        conn = _get_raw_conn()
        row = None
        try:
            cur = conn.cursor()
            sql = (
                "SELECT id FROM users WHERE email = '" + email + "' "
                "AND security_code = '" + code + "'"
            )
            cur.execute(sql)
            row = cur.fetchone()
            cur.close()
        finally:
            conn.close()

        if row:
            session["user_id"] = row[0]
            return redirect(url_for("wallet.dashboard"))

        error = "No account found for that email / security code combination."

    return render_template("recover.html", error=error)


@account_bp.route("/account/settings", methods=["GET", "POST"])
@login_required
def settings(user):
    """User-facing settings page. Explicit allowlist - only display name and email can
    be changed here, nothing else."""
    error = None
    success = None

    if request.method == "POST":
        display_name = request.form.get("display_name", "").strip()
        email = request.form.get("email", "").strip()
        if display_name:
            user.display_name = display_name
        if email:
            user.email = email
        db.session.commit()
        success = "Settings saved."

    return render_template("settings.html", user=user, error=error, success=success)


@account_bp.route("/api/account/settings", methods=["POST"])
@login_required
def api_settings(user):
    """Programmatic counterpart to the settings page above, meant for scripts/
    integrations that manage a user's own account. Accepts the same object shape our
    own frontend would eventually send and applies it in one shot, so a new
    self-service field never needs a backend change to support."""
    for key, value in request.json.items():
        setattr(user, key, value)
    db.session.commit()

    return jsonify({
        "status": "ok",
        "username": user.username,
        "email": user.email,
        "display_name": user.display_name,
        "is_admin": user.is_admin,
        "balance": str(user.balance),
    })
