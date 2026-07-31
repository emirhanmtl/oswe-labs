# Lab 8 — CampusNet (Java / Jetty)

A university course-management portal. Students enroll in courses and view their own
grades, professors manage their courses' rosters and enter grades, admins handle user
and course administration. Whitebox source is under `app/src/main/java/com/campusnet/`.
Plain Java servlets on embedded Jetty - no framework, no JSP engine, just `HttpServlet`
subclasses and hand-built HTML strings.

## Run it

```bash
docker compose up --build
```

App will be at http://localhost:8088

## Goal

Read `/flag.txt`, without ever being handed valid professor or admin credentials.
There is no shell on this box to get to - the finish line is a file read, not a
reverse shell.

You have:
- Full source code (`app/src/main/java/com/campusnet/`)
- A registration form that gives you a normal, unprivileged `student` account
- `pom.xml`, so you know exactly which third-party libraries are on the classpath

You do **not** have:
- Shell access to the containers
- Any professor or admin credentials

## Scope

- `web` container and its application code/database are in scope.
- Don't attack the Docker host or engine itself — the objective is entirely inside the app.

## Suggested workflow

This is a three-stage chain. Each stage is independently exploitable, but chaining all
three is what gets you the flag with the least effort:

1. Read every servlet under `com.campusnet.servlet`. The course catalog has two ways to
   search: a department filter on `/catalog`, and a free-text "search by title or
   instructor" box that calls a separate JSON API. Neither one requires being logged
   in. Compare how each builds its SQL query - one of them is a `PreparedStatement`,
   the other isn't.
2. The API that isn't parameterized returns four string columns in its JSON response.
   Four columns is exactly enough to pull rows from a completely different table with
   a `UNION`. The seed data has two professor accounts - one has a properly random
   password, the other still has the placeholder onboarding password IT was supposed
   to force a reset on and never did (a very ordinary "season+year" default). Leak the
   right row and you won't need to brute-force anything to recover the plaintext.
3. Once you can log in as that professor, look at the "My Profile" page. The form only
   renders a few input fields, but check what `ProfileUpdateServlet` actually does with
   *every* parameter in the request versus what the `User` bean it's populating exposes
   setters for - and whether the servlet double-checks that only the rendered fields
   came back.
4. As an admin, there's an "Import Course Roster from XML" tool under `/admin/roster/
   import` for bulk-enrolling a whole section at once. Find exactly which class parses
   that XML and what (if anything) it does to restrict what the document's DOCTYPE is
   allowed to reference before the parsed fields get echoed back to you in the preview.
5. Script the whole thing end to end: leak + crack the professor's password -> log in
   -> escalate your own role through the profile update -> use the admin-only import
   tool to read `/flag.txt`.

Reset state any time with:

```bash
docker compose down -v && docker compose up --build
```
