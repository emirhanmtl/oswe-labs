# Lab 4 — JavaBank (Java / Jetty)

A minimal Java web app with a "remember me" cookie. Source is under `app/src/main/java/com/javabank/`.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8084

## Goal

Get code execution on the `web` container and read `/flag.txt`.

You have:
- Full source code
- One low-privilege credential in the source (`jsmith` / `Summer2024!`)
- `pom.xml`, so you know the exact third-party library versions on the classpath

## Scope

- `web` container / app code in scope.

## Suggested workflow

1. Read `AccountServlet.java`. Notice what it does with the `rememberMe` cookie before checking whether the result is even the expected `UserToken` type. You don't actually need a valid login for this endpoint — read `LoginServlet.java` to see it only affects how the cookie is generated for you; the deserialization happens the same way on any bytes you send.
2. Check `pom.xml` for the exact version of `commons-collections` bundled with the app. That version has publicly known gadget chains for Java deserialization.
3. Generate a malicious serialized payload targeting that gadget chain (a well-known public tool for this is `ysoserial` — building your own minimal gadget chain by hand is also a great exercise once you understand how the InvokerTransformer-style chain works).
4. Base64-encode the payload and send it as the `rememberMe` cookie value to `GET /account`.
5. The container has no shell access for you — prove RCE by having your payload's command copy `/flag.txt` into `/app/static/` (served at `/static/*`), then fetch it over HTTP. Automate steps 3-5 in one script.

Reset with:

```bash
docker compose down && docker compose up --build
```
