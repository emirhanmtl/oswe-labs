# OSWE Prep Labs

Five self-contained, dockerized whitebox web app labs, each built around a vulnerability
chain in the style of OSWE (WEB-300): full source code is available, there's no shell
access to the box, and the goal is to read the code, find the bug(s), and write your own
exploit script (Python/Node — whatever you're comfortable automating HTTP with) rather
than clicking through Burp by hand.

Every lab plants an `OSWE_LABx{...}` flag inside the container; getting your exploit
script to print that flag is how you know you actually achieved code execution, not just
"probably would have."

| Lab | Stack | Chain |
|---|---|---|
| [lab1-notevault-sqli-upload](lab1-notevault-sqli-upload/) | PHP + MySQL | SQL injection auth bypass -> admin panel -> file upload extension-blacklist bypass -> webshell RCE |
| [lab2-templatemailer-ssti](lab2-templatemailer-ssti/) | Python / Flask | Weak/default `SECRET_KEY` -> forged signed session cookie -> admin-only Jinja2 SSTI -> RCE |
| [lab3-shopnode-protopollution](lab3-shopnode-protopollution/) | Node / Express / MongoDB | NoSQL injection auth bypass -> prototype pollution via unsafe merge -> EJS template engine gadget -> RCE |
| [lab4-javabank-deserialization](lab4-javabank-deserialization/) | Java / Jetty | Insecure Java deserialization of a "remember me" cookie with a known-vulnerable `commons-collections` gadget chain on the classpath -> RCE |
| [lab5-authgate-jwt-idor](lab5-authgate-jwt-idor/) | Node / Express | JWT signed with a hardcoded/guessable secret -> forged admin token -> path traversal in a file-save API -> arbitrary file write into a `require()`-driven plugin loader -> RCE |

## Requirements

- Docker + Docker Compose (Docker Desktop on Windows is fine)
- Whatever you want to write exploit scripts in — Python (`requests`, `pwntools`-style
  scripting) and Node (`axios`/`node-fetch`) both work well for this kind of thing

## Workflow per lab

```bash
cd lab1-notevault-sqli-upload   # or whichever lab
docker compose up --build
```

Each lab's own `README.md` has the specific goal, the credentials/access you start with,
and a suggested (not prescriptive) order of attack — it deliberately does **not** spell out
exact payloads, since the point is to find them yourself from the source.

Tear a lab down and reset all state with:

```bash
docker compose down -v
```

## Suggested way to work through each one

1. Stand the lab up, poke at it once in the browser so you know the surface.
2. Read the source top to bottom before touching Burp/curl — in OSWE, source review comes
   first, blackbox probing second.
3. Confirm each vulnerability individually (auth bypass works on its own, injection returns
   the data you expect, etc.) before trying to chain them.
4. Write one script per lab that runs the entire chain unattended, end to end, and prints
   the flag. That scripting exercise is the actual OSWE-relevant skill — the individual bugs
   are all things you could find with a scanner.

## Scope / ground rules

Everything here is built for you to attack. It's all synthetic, self-contained, and has no
connection to any real system — there's nothing outside `docker compose up` that's in
scope, and nothing here should be pointed at anything other than your own local containers.
