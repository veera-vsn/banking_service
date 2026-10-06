#!/bin/bash
# Usage: scripts/smoke-test.sh [base-url]     (default http://localhost:8090)
# Exits non-zero if any check fails, so Jenkins can use it as a pipeline stage later.
BASE=${1:-http://localhost:8090}
pass=0; fail=0
U="smoke$RANDOM$RANDOM"            # a fresh user name for every run

check() {   # $1 = name, $2 = expected text, $3 = actual
  if [[ "$3" == *"$2"* ]]; then echo "PASS  $1"; pass=$((pass+1))
  else echo "FAIL  $1   (expected '$2', got '$3')"; fail=$((fail+1)); fi
}
code() { curl -s -o /dev/null -w '%{http_code}' "$@"; }

check "frontend page loads"        "Acme Bank"          "$(curl -s "$BASE/")"
check "auth /health"               '"db":"UP"'          "$(curl -s "$BASE/api/auth/health")"
check "bank /health"               '"db":"UP"'          "$(curl -s "$BASE/api/bank/health")"
check "login demo user"            '"login":"success"'  "$(curl -s -X POST "$BASE/api/auth/login" -d 'username=nani&password=nani123')"
check "login wrong password"       "401"                "$(code -X POST "$BASE/api/auth/login" -d 'username=nani&password=wrong')"
check "balance demo user"          "25000.50"           "$(curl -s "$BASE/api/bank/balance?user=nani")"
check "balance unknown user"       "404"                "$(code "$BASE/api/bank/balance?user=nobody")"
check "register new user"          "201"                "$(code -X POST "$BASE/api/auth/register" --data-urlencode "fullName=Smoke Test" --data-urlencode "email=smoke@example.com" -d "username=$U&password=secret1")"
check "register same user again"   "409"                "$(code -X POST "$BASE/api/auth/register" --data-urlencode "fullName=Smoke Test" --data-urlencode "email=smoke@example.com" -d "username=$U&password=secret1")"
check "register bad input"         "400"                "$(code -X POST "$BASE/api/auth/register" -d "fullName=&email=x&username=a&password=1")"
check "open bank account"          '"open":"success"'   "$(curl -s -X POST "$BASE/api/bank/open" -d "user=$U")"
check "login new user"             '"login":"success"'  "$(curl -s -X POST "$BASE/api/auth/login" -d "username=$U&password=secret1")"
check "balance new user"           "1000.00"            "$(curl -s "$BASE/api/bank/balance?user=$U")"

echo "passed=$pass failed=$fail"
[ "$fail" -eq 0 ]
