# Lab 5 — AuthGate (Node.js / Express)

A small internal reporting API. Source is under `app/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8085

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/auth.js`, `app/routes/*.js`, `app/server.js`)
- One low-privilege credential in the source (`guest` / `guestpass123`) — logging in only ever gets you a `role: user` token; there is no login path that issues an admin token

## Scope

- `web` container / app code in scope.

## Suggested workflow

1. Read `auth.js`. Notice how the JWT is signed and verified, and where the secret comes from if the environment variable isn't set (check `docker-compose.yml` — is `JWT_SECRET` ever set there?).
2. With that secret, you can mint a token with whatever claims you want, including a role no login endpoint would ever give you. Write a small script that forges one.
3. Read `routes/reports.js` and `routes/admin.js` together. Users can only save files into `reports/user_reports/`; admins can `require()` and run named plugins out of `reports/plugins/`. Look closely at how the save path and the run path are each built from user input — is either of them actually constrained to its intended directory?
4. Chain it: forge an admin token, use the user-facing save endpoint (which only needs a `user`-role token) to land a file inside the plugins directory instead of the reports directory, then use the admin run endpoint to execute it.
5. Your planted plugin just needs to export a `run()` function — whatever it returns comes straight back in the JSON response, so there's no need for a reverse shell or a static file trick here. Automate the whole thing in one script: forge token -> traverse-write payload -> trigger run -> print the flag.

Reset with:

```bash
docker compose down && docker compose up --build
```
