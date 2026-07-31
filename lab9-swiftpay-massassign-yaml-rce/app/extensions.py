from flask_sqlalchemy import SQLAlchemy

# Single shared SQLAlchemy instance, initialized against the real Flask app in app.py.
db = SQLAlchemy()
