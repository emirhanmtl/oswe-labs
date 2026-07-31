import os
from flask import Flask, request, session, redirect, url_for, render_template, render_template_string, abort

app = Flask(__name__)

# NOTE: ops was supposed to set SECRET_KEY in the environment for production.
# If it's not set, we fall back to the value used in every dev/staging box.
app.secret_key = os.environ.get("SECRET_KEY", "N0t3sM4iler_2024!")

# In-memory "database" of regular users. There is no admin account here -
# the admin role is granted purely by session data (see admin_required).
USERS = {
    "bob": "bobspassword",
    "carol": "carolspassword",
}


def admin_required(view):
    def wrapped(*args, **kwargs):
        if session.get("role") != "admin":
            abort(403)
        return view(*args, **kwargs)
    wrapped.__name__ = view.__name__
    return wrapped


def login_required(view):
    def wrapped(*args, **kwargs):
        if "username" not in session:
            return redirect(url_for("login"))
        return view(*args, **kwargs)
    wrapped.__name__ = view.__name__
    return wrapped


@app.route("/")
def index():
    return redirect(url_for("login"))


@app.route("/login", methods=["GET", "POST"])
def login():
    error = None
    if request.method == "POST":
        username = request.form.get("username", "")
        password = request.form.get("password", "")
        if USERS.get(username) == password:
            session["username"] = username
            session["role"] = "user"
            return redirect(url_for("dashboard"))
        error = "Invalid credentials."
    return render_template("login.html", error=error)


@app.route("/logout")
def logout():
    session.clear()
    return redirect(url_for("login"))


@app.route("/dashboard")
@login_required
def dashboard():
    return render_template("dashboard.html", username=session["username"], role=session.get("role", "user"))


@app.route("/admin/compose", methods=["GET", "POST"])
@admin_required
def admin_compose():
    preview = None
    body = ""
    if request.method == "POST":
        body = request.form.get("body", "")
        # Marketing wants live variable substitution ({{ customer_name }}, etc.)
        # in the preview before a campaign goes out.
        preview = render_template_string(body)
    return render_template("compose.html", preview=preview, body=body)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
