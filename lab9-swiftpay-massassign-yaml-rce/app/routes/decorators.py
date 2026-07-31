from functools import wraps

from flask import abort, redirect, session, url_for

from models import User


def login_required(view):
    """Loads the current user fresh from the database on every request and hands it
    to the view as the first argument. Nothing about role/privilege is ever cached in
    the session itself - the session only ever stores a bare user_id."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        user = User.query.get(session.get("user_id"))
        if not user:
            return redirect(url_for("auth.login"))
        return view(user, *args, **kwargs)

    return wrapped


def admin_required(view):
    """Same as login_required, but also re-checks is_admin straight from the database
    on every request - so a privilege change takes effect on the very next request,
    with no re-login or session refresh needed."""

    @wraps(view)
    def wrapped(*args, **kwargs):
        user = User.query.get(session.get("user_id"))
        if not user or not user.is_admin:
            abort(403)
        return view(user, *args, **kwargs)

    return wrapped
