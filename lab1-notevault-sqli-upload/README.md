# Lab 1 — NoteVault (PHP)

A small note-taking app. Whitebox source is under `app/src/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8081

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/src/`)
- A registration form that gives you a normal, unprivileged account
- No credentials for the `admin` account

You do **not** have:
- Shell access to the containers (that's the point)
- The admin password

## Scope

- `web` container and its application code/database are in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

1. Register a low-privilege account and log in. Read every PHP file that handles user input.
2. Look at how the login query and the note search query are built — write a small Python (`requests`) script to probe them instead of doing it by hand in the browser. Confirm boolean-based or classic bypass behavior before trying to escalate.
3. Once you can authenticate as `admin`, look at what the admin panel exposes.
4. The upload feature validates extensions with a blacklist — check what it actually blocks vs. what Apache will still execute as PHP.
5. Chain steps 2–4 into one script that: bypasses auth -> confirms admin access -> uploads a payload -> executes a command -> prints the flag.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
