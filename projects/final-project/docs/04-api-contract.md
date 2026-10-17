# 04. API 계약

경로는 `/api/v1`로 시작한다. 요청/응답 타입은 `contract` 패키지에 있다.

| 메서드 | 경로 | 권한 | 성공 | 실패 |
|---|---|---|---|---|
| POST | `/users` | 누구나 | 201 `UserView` | 400, 409(아이디 중복) |
| GET | `/users/me` | 로그인 | 200 `UserView` | 401 |
| GET | `/posts` | 누구나 | 200 `PageResponse<PostSummary>` | 400 |
| GET | `/posts/{id}` | 누구나 | 200 `PostView` | 404 |
| POST | `/posts` | 로그인 | 201 `PostView` | 400, 401 |
| PUT | `/posts/{id}` | 작성자/관리자 | 200 `PostView` | 400, 401, 403, 404 |
| DELETE | `/posts/{id}` | 작성자/관리자 | 204 | 401, 403, 404 |
| GET | `/admin/stats` | 관리자 | 200 작성자별 글 수 | 401, 403 |

## 검색 질의 매개변수 (`GET /posts`)

| 이름 | 의미 | 기본값 |
|---|---|---|
| keyword | 제목 또는 본문에 포함된 글자 | 없음 |
| authorId | 작성자 | 없음 |
| tag | 태그 하나(정확히 일치) | 없음 |
| page | 0부터 시작 | 0 |
| size | 1~50 | 10 |

정렬은 고정 글이 먼저, 그다음 최신순이다.

## 오류 형식

```json
{ "code": "VALIDATION_FAILED", "message": "입력값이 올바르지 않습니다",
  "issues": [ { "field": "title", "reason": "must not be blank" } ] }
```

| code | 상태 | 뜻 |
|---|---|---|
| VALIDATION_FAILED | 400 | 입력 검증 실패 |
| UNAUTHORIZED | 401 | 로그인 필요 |
| FORBIDDEN | 403 | 권한 없음 |
| NOT_FOUND | 404 | 대상 없음 |
| DUPLICATE_USERNAME | 409 | 아이디 중복 |
