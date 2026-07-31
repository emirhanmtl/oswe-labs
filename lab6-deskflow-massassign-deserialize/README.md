# Lab 6 — DeskFlow (PHP + PostgreSQL)

An internal IT helpdesk / ticketing app. Customers open tickets, agents and admins work
the queue. Whitebox source is under `app/src/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8086

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/src/`)
- A registration form that gives you a normal, unprivileged `customer` account

You do **not** have:
- Shell access to the containers
- Any staff (`agent`/`admin`) credentials

## Scope

- `web` container and its application code/database are in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

This one is a three-stage chain, and each stage lands you somewhere you need the *next*
stage to get further — none of them is useful entirely on its own:

1. Register a customer account and poke around. There's a ticket search feature gated to
   staff, and a different lookup feature that isn't — read both and compare how each one
   builds its query.
2. Whatever you land as a result of step 1 almost certainly isn't `admin` (that account's
   password is long and random on purpose). Look for a feature that lets an authenticated
   user change *something about themselves* — read exactly which fields it lets you touch,
   and how it decides that.
3. Once you have full staff (or better) access, look at what admin-only tools exist for
   moving data in and out of the app. Anywhere that reconstructs a PHP object from data it
   didn't generate itself is worth a close look — and then anywhere that same object's class
   is used for real, elsewhere in the code, to see what it actually *does* when it's done
   being used.
4. Chain it into one script: low-priv register -> leak/derive staff credentials -> escalate
   your own privileges -> plant something via the admin tool that gets you a shell -> print
   the flag.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
