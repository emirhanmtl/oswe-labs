import yaml
from flask import Blueprint, render_template, request

from extensions import db
from models import AppConfig, User
from routes.decorators import admin_required

admin_bp = Blueprint("admin", __name__)


@admin_bp.route("/admin")
@admin_required
def index(user):
    users = User.query.order_by(User.id).all()
    configs = AppConfig.query.order_by(AppConfig.key).all()
    return render_template("admin_index.html", user=user, users=users, configs=configs)


@admin_bp.route("/admin/settings/import", methods=["GET", "POST"])
@admin_required
def import_settings(user):
    """Restores platform settings (fee schedule, supported currencies, transfer limits)
    from a config blob prepared on another SwiftPay instance - either pasted directly
    or uploaded as a file. Switched from yaml.load() to yaml.unsafe_load() a while back
    to silence the PyYAMLLoadWarning without dropping support for the richer tag set
    (nested dicts, dates, etc.) a real exported config can contain."""
    result = None
    error = None

    if request.method == "POST":
        upload = request.files.get("config_file")
        if upload and upload.filename:
            yaml_text = upload.read().decode("utf-8", errors="ignore")
        else:
            yaml_text = request.form.get("yaml_config", "")

        try:
            parsed = yaml.unsafe_load(yaml_text)
            if isinstance(parsed, dict):
                for key, value in parsed.items():
                    cfg = AppConfig.query.filter_by(key=key).first() or AppConfig(key=key)
                    cfg.value_yaml = yaml.safe_dump(value)
                    db.session.add(cfg)
                db.session.commit()
            result = parsed
        except yaml.YAMLError as e:
            error = f"Could not parse config: {e}"

    return render_template("admin_import.html", user=user, result=result, error=error)


@admin_bp.route("/admin/settings/export")
@admin_required
def export_settings(user):
    """Read-only counterpart to the import above - dumps the current settings back out
    as YAML using the safe dumper, so an operator can carry a known-good config to
    another instance."""
    configs = AppConfig.query.order_by(AppConfig.key).all()
    data = {c.key: yaml.safe_load(c.value_yaml) for c in configs}
    body = yaml.safe_dump(data)
    return body, 200, {"Content-Type": "text/yaml; charset=utf-8"}
