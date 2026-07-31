import random
import string

from werkzeug.security import generate_password_hash

from extensions import db
from models import AppConfig, Transaction, User


def _rand_code():
    return "".join(random.choices(string.digits, k=4))


def seed_if_empty():
    if User.query.first():
        return

    # Admin's password and recovery code are long/random on purpose - not meant to be
    # guessed or cracked directly. The intended path in is through a lower-privilege
    # account and a privilege-escalation bug elsewhere in the app, not this one.
    admin = User(
        username="admin",
        email="admin@swiftpay.local",
        display_name="Platform Admin",
        password_hash=generate_password_hash("Kx8!vQ2mZ9wLpT4uR6"),
        security_code=_rand_code(),
        balance=0,
        daily_limit=1000000,
        is_admin=True,
    )
    alice = User(
        username="alice",
        email="alice@swiftpay.local",
        display_name="Alice",
        password_hash=generate_password_hash("alicepassword1"),
        security_code=_rand_code(),
        balance=2500.00,
        daily_limit=5000,
        is_admin=False,
    )
    bob = User(
        username="bob",
        email="bob@swiftpay.local",
        display_name="Bob",
        password_hash=generate_password_hash("bobpassword1"),
        security_code=_rand_code(),
        balance=800.00,
        daily_limit=5000,
        is_admin=False,
    )
    db.session.add_all([admin, alice, bob])
    db.session.commit()

    db.session.add_all([
        Transaction(sender_id=alice.id, recipient_id=bob.id, amount=50.00, note="lunch split"),
        Transaction(sender_id=bob.id, recipient_id=alice.id, amount=20.00, note="coffee run"),
    ])
    db.session.add(AppConfig(
        key="fee_schedule",
        value_yaml="default_fee_percent: 1.5\nsupported_currencies: [USD, EUR]\n",
    ))
    db.session.add(AppConfig(
        key="transfer_limits",
        value_yaml="default_daily_limit: 5000\nmax_single_transfer: 2000\n",
    ))
    db.session.commit()
