# Lab 7 — GitLite (Node.js / Express / MongoDB)

A minimal self-hosted code-snippet sharing service (think: a tiny internal Gitea-lite).
Users have snippets, some public, some private; there's an admin-only export tool. Source
is under `app/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8087

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/routes`, `app/lib`, `app/middleware`, `app/models`, `app/utils`,
  `app/server.js`)
- No credentials, and there is **no self-registration** — accounts are provisioned by an
  admin out of band. Everything you get, you have to take.

## Scope

- `web` and `db` containers / app code in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

This is a three-stage chain. Each stage gets you somewhere you need for the *next* one —
none of them is the finish line on its own.

1. Read `lib/jwt.js` closely — specifically how tokens get signed vs. how they get
   *verified*, and exactly which algorithm families the verify call is willing to accept.
   Then find the two unauthenticated endpoints that, between them, give you everything
   you'd need to build a token from scratch for an account you've never logged into:
   one hands out cryptographic material this app uses for verification, the other lets
   you browse content well enough to learn a real user's internal ID. Research how a
   JWT signed with a symmetric algorithm is actually verified end-to-end, and think about
   what happens when the "key" on one side of that check was never meant to be a shared
   secret at all. Don't assume — a lot of the well-known JWT attacks (bare `alg: none`,
   for instance) are dead on arrival against any current `jsonwebtoken` release; test
   whatever you try against the actual library version pinned in `package.json` before
   concluding it doesn't work.
2. Once you can present a token that verifies for *some* account, look at `routes/profile.js`.
   Read exactly which fields it lets you touch when you update yourself, and how (or
   whether) it decides that. Notice what comes back in the response after a successful
   update — you won't need to log in again to pick up whatever changed.
3. With full admin access, look at what admin-only tooling exists for turning a snippet
   into a distributable file (`routes/export.js`). Read how the destination filename gets
   used, all the way down to the line that actually runs something.
4. Chain it into one script: forge a token for stage 1 -> escalate yourself in stage 2 ->
   plant a command in stage 3's filename field -> read the flag back from the response body
   (no reverse shell or static-file trick needed — whatever that last step's process prints
   comes straight back to you over HTTP).

A couple of things that look like they might be part of the chain but aren't: there's a
snippet search endpoint that *looks* like a textbook NoSQL-injection candidate (free-text
term dropped into a query) — read `utils/regexEscape.js` and how `routes/search.js` actually
uses it before spending time there. Same idea for the ownership checks scattered through
`routes/snippets.js` and `routes/comments.js` — they're not the intended way in.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
