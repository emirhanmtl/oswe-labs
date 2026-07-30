# Lab 3 — ShopNode (Node.js / Express / MongoDB)

A small internal dashboard app. Source is under `app/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8083

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code (`app/routes`, `app/lib`, `app/models`, `app/views`, `app/server.js`)
- No credentials — there is no self-registration in this app

## Scope

- `web` and `db` containers / app code in scope.

## Suggested workflow

1. Read `routes/auth.js`. The login query is built straight from the request body and handed to Mongoose. Think about what happens when a field is a JSON object instead of a string, and what that does to a MongoDB query filter. Get yourself a valid session without knowing any password.
2. Read `lib/merge.js` used by `routes/preferences.js`. It's a generic recursive merge with no key filtering — think about what a JSON body containing a `__proto__` key does when walked by a plain `for...in` loop, and what object in the JS runtime you end up writing properties onto.
3. Check `package.json` for the exact `ejs` version pinned, and look at how `views/dashboard.ejs` gets rendered (`res.render`) with no explicit `outputFunctionName` option. Research how that specific EJS version turns `options.outputFunctionName` into part of the compiled template's generated source (there's a public advisory covering exactly this gadget in `ejs` versions before it was patched — worth finding and reading, and double-checking the pinned version actually predates the fix, in case the version boundary here is off by a patch release; adjust `app/package.json` and rebuild if you find it's already patched).
4. Chain it: forge a session via the injection in step 1, PUT a JSON payload to `/api/preferences` that pollutes the right global property with a JS-injection gadget, then hit `/dashboard` again to trigger the render and execute it.
5. There's no reverse-shell listener required — the app serves `app/public` statically, so a payload that writes a file there (or writes `/flag.txt`'s contents there) is retrievable with a plain GET request. Automate the whole chain in one script.

Reset with:

```bash
docker compose down -v && docker compose up --build
```
