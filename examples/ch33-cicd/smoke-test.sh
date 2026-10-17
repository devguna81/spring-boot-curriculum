#!/bin/sh
# 컨테이너로 띄운 ch32 애플리케이션이 실제로 서비스할 준비가 되었는지 확인한다.
# 사용법: smoke-test.sh <기본 URL> [대기 시간(초)]
#   예) examples/ch33-cicd/smoke-test.sh http://localhost:8080 90
# CI 워크플로(.github/workflows/image.yml)와 로컬에서 같은 스크립트를 쓴다.
set -eu

BASE_URL="${1:?기본 URL을 첫 번째 인자로 주세요 (예: http://localhost:8080)}"
TIMEOUT="${2:-60}"

fail() {
  echo "SMOKE TEST FAILED: $1" >&2
  exit 1
}

# 1) readiness 프로브가 UP이 될 때까지 기다린다. 컨테이너가 떴다는 것과 요청을 받을 수 있다는 것은 다르다.
waited=0
until curl -fsS "$BASE_URL/actuator/health/readiness" 2>/dev/null | grep -q '"UP"'; do
  waited=$((waited + 2))
  [ "$waited" -le "$TIMEOUT" ] || fail "readiness가 ${TIMEOUT}초 안에 UP이 되지 않았습니다"
  sleep 2
done
echo "readiness UP after about ${waited}s"

# 2) 응답 본문까지 확인한다. 200만 보면 오류 페이지도 통과한다.
curl -fsS "$BASE_URL/hello" | grep -q '"hello from container"' || fail "/hello 응답이 예상과 다릅니다"
echo "/hello ok"

# 3) 데이터베이스를 거치는 경로가 동작하고, 호출할 때마다 값이 늘어나는지 확인한다.
first=$(curl -fsS "$BASE_URL/visits" | tr -dc '0-9')
second=$(curl -fsS "$BASE_URL/visits" | tr -dc '0-9')
[ -n "$first" ] && [ -n "$second" ] || fail "/visits 응답에서 숫자를 읽지 못했습니다"
[ "$second" -eq $((first + 1)) ] || fail "/visits가 1씩 늘지 않았습니다 (first=$first, second=$second)"
echo "/visits ok ($first -> $second)"

echo "SMOKE TEST PASSED"
