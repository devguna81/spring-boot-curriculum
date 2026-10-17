#!/bin/sh
# 컨테이너로 띄운 종합 프로젝트가 실제로 서비스할 준비가 되었는지 확인한다.
# 사용법: smoke-test.sh <기본 URL> [대기 시간(초)]
#   예) projects/final-project/smoke-test.sh http://localhost:8080 90
# CI 워크플로(.github/workflows/image.yml)와 로컬에서 같은 스크립트를 쓴다.
set -eu

BASE_URL="${1:?기본 URL을 첫 번째 인자로 주세요 (예: http://localhost:8080)}"
TIMEOUT="${2:-60}"
# 실행할 때마다 다른 아이디를 써서 같은 서버에 여러 번 돌려도 충돌하지 않게 한다.
USER_NAME="smoke_$(date +%s)_$$"
PASSWORD="smoke-pass-$(date +%s)"

fail() {
  echo "SMOKE TEST FAILED: $1" >&2
  exit 1
}

# 응답 상태 코드만 돌려준다.
status_of() {
  curl -s -o /dev/null -w '%{http_code}' "$@"
}

# 1) readiness 프로브가 UP이 될 때까지 기다린다.
waited=0
until curl -fsS "$BASE_URL/actuator/health/readiness" 2>/dev/null | grep -q '"UP"'; do
  waited=$((waited + 2))
  [ "$waited" -le "$TIMEOUT" ] || fail "readiness가 ${TIMEOUT}초 안에 UP이 되지 않았습니다"
  sleep 2
done
echo "readiness UP after about ${waited}s"

# 2) 화면: 서버 렌더링 목록과, 이미지에 묶여 들어간 React 화면
curl -fsS "$BASE_URL/pages/posts" | grep -q '글 목록' || fail "/pages/posts 응답이 예상과 다릅니다"
echo "/pages/posts ok"
curl -fsS "$BASE_URL/app/index.html" | grep -q 'id="root"' || fail "/app/index.html(React 화면)이 이미지에 들어 있지 않습니다"
echo "/app/index.html ok"

# 3) 가입, 로그인 확인
signup=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/v1/users" \
  -H 'Content-Type: application/json' \
  -d "{\"username\":\"$USER_NAME\",\"password\":\"$PASSWORD\",\"displayName\":\"smoke\"}")
[ "$signup" = "201" ] || fail "가입이 201이 아닙니다 ($signup)"
curl -fsS -u "$USER_NAME:$PASSWORD" "$BASE_URL/api/v1/users/me" | grep -q "\"$USER_NAME\"" || fail "로그인 확인(/users/me)이 실패했습니다"
echo "signup/login ok"

# 4) 보안: 로그인 없이 쓰기와 운영 정보는 막혀 있어야 한다
[ "$(status_of -X POST "$BASE_URL/api/v1/posts" -H 'Content-Type: application/json' -d '{"title":"t","content":"c"}')" = "401" ] \
  || fail "로그인 없는 글 작성이 401이 아닙니다"
[ "$(status_of "$BASE_URL/actuator/metrics")" = "401" ] || fail "로그인 없는 /actuator/metrics가 401이 아닙니다"
[ "$(status_of -u "$USER_NAME:$PASSWORD" "$BASE_URL/api/v1/admin/stats")" = "403" ] || fail "일반 회원의 관리자 API가 403이 아닙니다"
[ "$(status_of -u "$USER_NAME:wrong-password" "$BASE_URL/api/v1/users/me")" = "401" ] || fail "틀린 비밀번호가 401이 아닙니다"
echo "security ok"

# 5) 글을 쓰고 검색으로 다시 찾는다. 데이터베이스와 검색 SQL까지 거치는 경로다.
TAG="smoketag$$"
location=$(curl -sS -D - -o /dev/null -u "$USER_NAME:$PASSWORD" -X POST "$BASE_URL/api/v1/posts" \
  -H 'Content-Type: application/json' \
  -d "{\"title\":\"smoke title\",\"content\":\"smoke body\",\"tags\":[\"$TAG\"],\"pinned\":false}" \
  | tr -d '\r' | sed -n 's/^[Ll]ocation: //p')
[ -n "$location" ] || fail "글 작성 응답에 Location이 없습니다"
curl -fsS "$BASE_URL/api/v1/posts?tag=$TAG" | grep -q '"total":1' || fail "태그 검색으로 방금 쓴 글을 찾지 못했습니다"
echo "post create/search ok"

echo "SMOKE TEST PASSED"
