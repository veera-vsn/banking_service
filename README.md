# Acme Bank (learning project)

Browser -> **frontend** (nginx, :8090) -> `/api/auth/*` -> **auth** (Tomcat) -> **mysql**
                                       -> `/api/bank/*` -> **bank** (Tomcat) -> **mysql**

Only the frontend publishes a port. MySQL, auth and bank are reachable only on the `bank-net` network.

## Run
    cp .env.example .env        # then edit the passwords
    docker compose up -d --build
    scripts/smoke-test.sh       # expect: passed=13 failed=0
    docker compose ps           # all services should be (healthy)

## Stop
    docker compose down         # keeps the database volume
    docker compose down -v      # also deletes the database volume

Demo login: nani / nani123   (or create your own account on the "Create account" tab)
