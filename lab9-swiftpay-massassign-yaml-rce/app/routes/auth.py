import random
import string

from flask import Blueprint, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash

from extensions import db
from models import User

auth_bp = Blueprint("auth", __name__)


@auth_bp.route("/")
def index():
    if session.get("user_id"):
        return redirect(url_for("wallet.dashboard"))
    return redirect(url_for("auth.login"))


@auth_bp.route("/register", methods=["GET", "POST"])
def register():
    error = None
    success = None

    if request.method == "POST":
        username = request.form.get("username", "").strip()
        email = request.form.get("email", "").strip()
        password = request.form.get("password", "")

        if len(username) < 3 or len(password) < 8:
            error = "Username must be 3+ chars and password 8+ chars."
        elif not email or "@" not in email:
            error = "Enter a valid email address."
        elif User.query.filter((User.username == username) | (User.email == email)).first():
            error = "That username or email is already registered."
        else:
            user = User(
                username=username,
                email=email,
                display_name=username,
                password_hash=generate_password_hash(password),
                security_code="".join(random.choices(string.digits, k=4)),
                balance=1000.00,
                daily_limit=5000,
                is_admin=False,
            )
            db.session.add(user)
            db.session.commit()
            success = "Account created with a starting balance of 1000.00. You can log in now."

    return render_template("register.html", error=error, success=success)


@auth_bp.route("/login", methods=["GET", "POST"])
def login():
    error = None

    if request.method == "POST":
        username = request.form.get("username", "")
        password = request.form.get("password", "")

        # Ordinary login always goes through the ORM with a bound-parameter lookup.
        user = User.query.filter_by(username=username).first()
        if user and check_password_hash(user.password_hash, password):
            session["user_id"] = user.id
            return redirect(url_for("wallet.dashboard"))

        error = "Invalid username or password."

    return render_template("login.html", error=error)


@auth_bp.route("/logout")
def logout():
    session.clear()
    return redirect(url_for("auth.login"))
