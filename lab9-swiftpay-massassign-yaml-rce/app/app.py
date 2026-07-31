import os
import secrets
import time

from flask import Flask, session
from sqlalchemy.exc import OperationalError

from extensions import db
from models import User
from seed import seed_if_empty


def create_app():
    app = Flask(__name__)

    # No hardcoded fallback here - if SECRET_KEY isn't set we just generate a random one
    # per container start. That's fine for session integrity; it's not the bug in this lab.
    app.secret_key = os.environ.get("SECRET_KEY") or secrets.token_hex(32)

    db_host = os.environ.get("DB_HOST", "db")
    db_name = os.environ.get("DB_NAME", "swiftpay")
    db_user = os.environ.get("DB_USER", "swiftpay")
    db_password = os.environ.get("DB_PASSWORD", "swiftpay_pw")

    app.config["SQLALCHEMY_DATABASE_URI"] = (
        f"postgresql+psycopg2://{db_user}:{db_password}@{db_host}/{db_name}"
    )
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

    # The account-recovery flow (routes/account.py) talks to Postgres directly with its
    # own psycopg2 connection instead of going through the ORM - carried over from the
    # legacy billing-system migration. It needs the raw connection parameters too.
    app.config["RAW_DB"] = {
        "host": db_host,
        "dbname": db_name,
        "user": db_user,
        "password": db_password,
    }

    db.init_app(app)

    from routes.account import account_bp
    from routes.admin import admin_bp
    from routes.auth import auth_bp
    from routes.wallet import wallet_bp

    app.register_blueprint(auth_bp)
    app.register_blueprint(account_bp)
    app.register_blueprint(wallet_bp)
    app.register_blueprint(admin_bp)

    with app.app_context():
        for attempt in range(15):
            try:
                db.create_all()
                break
            except OperationalError:
                time.sleep(2)
        seed_if_empty()

    @app.context_processor
    def inject_current_user():
        current_user = None
        uid = session.get("user_id")
        if uid:
            current_user = User.query.get(uid)
        return {"current_user": current_user}

    return app


app = create_app()

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
