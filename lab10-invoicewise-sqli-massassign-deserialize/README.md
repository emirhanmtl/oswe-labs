# Lab 10 — InvoiceWise (.NET / ASP.NET Core)

A small invoicing/billing tool for a fictional small business - staff log in to create and
view client invoices, and there's an admin area for managing users and backing up/restoring
invoice drafts. Whitebox source is under `app/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8090

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/`)

You do **not** have:
- Shell access to the containers (that's the point)
- Any staff credentials — this is an internal tool provisioned by IT, there is no
  self-service registration
- Admin's password (long and random on purpose)

## Scope

- `web` container and its application code/database are in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

This is a three-stage chain, and each stage lands you somewhere you need the *next* stage
to get further — none of them is useful entirely on its own:

1. There is no registration page, so the login form itself is your entry point. Read how
   the login query is built versus how the "find invoice by client email" lookup right
   next to it is built — one of them is hand-written, one isn't. Read `db/schema.sql` too;
   it tells you exactly which accounts exist.
2. Whatever you land as isn't `admin` (that account's password is long and random on
   purpose). Look for a feature that lets a logged-in user change something about
   themselves, read exactly which fields the controller action actually binds versus which
   fields the form shows you, and check what the authorization filter re-checks on every
   request.
3. Once you have full admin access, look at the admin tool for moving an invoice draft in
   and out of the app as a backup blob. Find the class that gets reconstructed from that
   blob, and see how that same class is used for real elsewhere in the app — specifically
   what it *does* the moment it finishes being deserialized, and whether either of the two
   fields driving that behavior are ones you controlled when you built the blob. Then think
   about what's special about a `.cshtml` file sitting under `Views/` in an app that has
   Razor runtime compilation turned on, and what's reachable at the URL that file's
   controller action already serves.
4. Chain it into one script: SQLi login bypass -> escalate your own account to admin via
   the profile editor -> plant something via the admin backup/restore tool that gets you
   code execution -> print the flag.

This last stage is deliberately in the same spirit as real-world ASP.NET deserialization
RCEs against cookie/blob-based "restore" features (e.g. DotNetNuke's CVE-2017-9822) — a
gadget class that looks like ordinary infrastructure code, reachable through a feature that
was never meant to trust its input.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
