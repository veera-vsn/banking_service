#!/bin/bash
# Usage: scripts/smoke-test.sh [base-url]     (default http://localhost:8090)
# Exits non-zero if any check fails, so Jenkins can use it as a pipeline stage later.
BASE=${1:-http://localhost:8090}
pass=0; fail=0

check() {   # $1 = name, $2 = expected text, $3 = actual
  if [[ "$3" == *"$2"* ]]; then echo "PASS  $1"; pass=$((pass+1))
  else echo "FAIL  $1   (expected '$2', got '$3')"; fail=$((fail+1)); fi
}

check "frontend page loads"   "Acme Bank"          "$(curl -s "$BASE/")"
check "auth /health"          '"db":"UP"'          "$(curl -s "$BASE/api/auth/health")"
check "bank /health"          '"db":"UP"'          "$(curl -s "$BASE/api/bank/health")"
check "login correct"         '"login":"success"'  "$(curl -s -X POST "$BASE/api/auth/login" -d 'username=nani&password=nani123')"
check "login wrong password"  "401"                "$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE/api/auth/login" -d 'username=nani&password=wrong')"
check "balance for nani"      "25000.50"           "$(curl -s "$BASE/api/bank/balance?user=nani")"
check "balance unknown user"  "404"                "$(curl -s -o /dev/null -w '%{http_code}' "$BASE/api/bank/balance?user=nobody")"

echo "passed=$pass failed=$fail"
[ "$fail" -eq 0 ]
