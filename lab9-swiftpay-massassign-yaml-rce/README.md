# Lab 9 — SwiftPay (Flask + PostgreSQL)

A small peer-to-peer payments/wallet demo. Users hold a balance, send money to other users
by username, and review their own transaction statement. There's also an admin panel for
managing platform settings (transfer limits, fee schedule, supported currencies). Whitebox
source is under `app/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8089

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/`)
- A registration form that gives you a normal, unprivileged account with a starting balance

You do **not** have:
- Shell access to the containers
- Any admin credentials, or credentials for any other seeded account

## Scope

- `web` container and its application code/database are in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

This one is a three-stage chain, and each stage lands you somewhere you need the *next*
stage to get further — none of them is useful entirely on its own:

1. There's a normal login form, and a separate "forgot access" / account-recovery feature
   for people who no longer have their password handy. Read both and compare how each one
   builds and runs its database query — one of them isn't using the database driver the way
   the other is.
2. Whatever account you land in as a result of step 1 is very unlikely to be an admin
   account on its own. Look for a way to change something about your own account, and pay
   close attention to whether there are actually two different code paths for doing that —
   one reachable from a page in the UI, and one that isn't linked from anywhere but still
   works. Check exactly which fields each path lets you touch, and how the app decides
   whether you're allowed into admin-only tooling on your very next request.
3. Once you're an admin, look at what admin-only tooling exists for moving configuration
   data in and out of the app. Anywhere that turns an uploaded/pasted file straight into a
   live Python object without a "safe" mode is worth a very close look — and note that
   switching from one PyYAML loading function to another doesn't always mean what people
   think it means.
4. Chain it into one script: recover into someone else's session -> escalate your own
   privileges -> plant a config payload that gets you a shell -> print the flag.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
