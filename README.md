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
| [lab6-deskflow-massassign-deserialize](lab6-deskflow-massassign-deserialize/) | PHP / PostgreSQL | SQL injection in an unauthenticated-adjacent lookup endpoint leaks staff credentials -> mass assignment on a self-service profile update escalates to admin -> PHP object injection (unserialize gadget) on an admin import feature -> RCE |
| [lab7-gitlite-jwtconfusion-massassign](lab7-gitlite-jwtconfusion-massassign/) | Node / Express / MongoDB | JWT algorithm-confusion auth bypass (forge a token for an existing user using a leaked RSA public key as an HMAC secret) -> Mongo mass-assignment privilege escalation (`updateOne(filter, req.body)` writes `role` unchecked) -> OS command injection in an admin export feature -> RCE |
| [lab8-campusnet-sqli-massassign-xxe](lab8-campusnet-sqli-massassign-xxe/) | Java / Jetty | Unauthenticated UNION SQL injection in a public course-search API leaks a professor's weak/crackable password hash -> mass assignment via `BeanUtils.populate()` on a self-service profile update escalates to admin -> XXE in an admin XML roster-import tool reads `/flag.txt` |
| [lab9-swiftpay-massassign-yaml-rce](lab9-swiftpay-massassign-yaml-rce/) | Python / Flask | SQL injection (string-concatenated query) in the account-recovery flow bypasses auth into an existing user's session -> mass assignment on a JSON settings API (`setattr` loop, no allowlist) self-promotes to admin -> `yaml.unsafe_load()` on an imported settings file gives RCE |
| [lab10-invoicewise-sqli-massassign-deserialize](lab10-invoicewise-sqli-massassign-deserialize/) | .NET / ASP.NET Core | Raw ADO.NET login query built by string concatenation bypasses auth as a low-priv staff account -> `TryUpdateModelAsync` over-posting on the profile editor flips its own `IsAdmin` flag -> `BinaryFormatter.Deserialize()` on an admin backup-restore feature reconstructs a custom gadget that performs an attacker-controlled file write -> RCE |

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
