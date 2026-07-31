# Lab 2 — TemplateMailer (Flask)

A tiny internal marketing tool. Source is `app/app.py` + `app/templates/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8082

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code
- Two low-privilege credentials in the source (`bob`/`bobspassword`, `carol`/`carolspassword`)
- No admin account exists in this app at all — admin-ness is decided some other way, read the code to see how

## Scope

- `web` container / app code in scope only.

## Suggested workflow

1. Read `app.py` end to end. Pay attention to how `session["role"]` gets set, and how `admin_required` decides who's an admin.
2. Note how `app.secret_key` is derived. Flask's session cookie is signed (via `itsdangerous`), not encrypted — if you have the same key the server has, you can mint your own cookie contents from scratch, with no login required.
3. Write a Python script that builds a session cookie with arbitrary session data using `itsdangerous` (same signer Flask itself uses internally), and set it directly as a cookie on your requests session.
4. Once you can reach `/admin/compose` as an "admin", look at what happens to your input server-side (`render_template_string`). Work out a Jinja2 SSTI payload that reaches `os.popen`/`subprocess` through the object graph reachable from a template (e.g. via `config`, `self`, class introspection, `__globals__`).
5. Automate: forge cookie -> POST payload to `/admin/compose` -> parse command output out of the rendered preview -> read the flag.

Reset with:

```bash
docker compose down && docker compose up --build
```
