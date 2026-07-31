from datetime import datetime

from extensions import db


class User(db.Model):
    __tablename__ = "users"

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), unique=True, nullable=False)
    email = db.Column(db.String(255), unique=True, nullable=False)
    display_name = db.Column(db.String(120))
    password_hash = db.Column(db.String(255), nullable=False)

    # 4-digit code texted to the user at signup, used only by the legacy account-recovery
    # flow (see routes/account.py) as a stand-in for "prove you own the phone number."
    security_code = db.Column(db.String(4), nullable=False)

    balance = db.Column(db.Numeric(12, 2), nullable=False, default=0)
    daily_limit = db.Column(db.Numeric(12, 2), nullable=False, default=5000)
    is_admin = db.Column(db.Boolean, nullable=False, default=False)

    created_at = db.Column(db.DateTime, default=datetime.utcnow)


class Transaction(db.Model):
    __tablename__ = "transactions"

    id = db.Column(db.Integer, primary_key=True)
    sender_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    recipient_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    amount = db.Column(db.Numeric(12, 2), nullable=False)
    note = db.Column(db.String(255))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)


class AppConfig(db.Model):
    """Platform-wide settings (fee schedule, supported currencies, transfer limits)
    managed from the admin panel and importable/exportable as a YAML blob so a config
    can be prepared on a staging instance and carried over to production."""

    __tablename__ = "app_config"

    id = db.Column(db.Integer, primary_key=True)
    key = db.Column(db.String(64), unique=True, nullable=False)
    value_yaml = db.Column(db.Text, nullable=False, default="")
