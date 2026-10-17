# f03. 화면과 문서

## 학습 목표

- 읽기 화면과 쓰기 화면을 나눠 Thymeleaf와 React의 역할을 정한다.
- 같은 서비스를 REST 컨트롤러와 화면 컨트롤러가 함께 쓰게 한다.
- React에서 서버 호출, 로그인 상태, 화면을 분리하고 서버의 검증 오류를 입력 칸에 연결한다.
- springdoc으로 API 문서를 만들고 구현과 어긋나지 않는지 테스트한다.

## 선수학습

ch17, ch19, ch22, f02

## 범위

| 항목 | 내용 |
|---|---|
| 프로젝트 | [`projects/final-project`](../../../projects/final-project) |
| 서버 렌더링 | `/pages/posts`, `/pages/posts/{id}` (읽기 전용) |
| React | [`frontend`](../../../projects/final-project/frontend): 목록, 상세, 작성·수정, 가입, 로그인 |
| API 문서 | `/swagger-ui.html`, `/v3/api-docs` |
| 화면 설계 | [`docs/05-screens.md`](../../../projects/final-project/docs/05-screens.md) |
| 테스트 | 서버 렌더링 8건, API 문서 2건, React 31건(Vitest) |
| 다루지 않는 것 | 실제 인증(f04), 배포 이미지에 React 포함(f04) |

## 핵심 설계

### 두 화면이 한 서비스를 쓴다

`PageController`(HTML)와 `PostController`(JSON)는 모두 `PostApi.search`를 부른다. 화면마다 검색 규칙을 따로 만들면 결과가 달라지기 때문에 규칙은 서비스에만 둔다.

### 읽기 화면의 오류 처리

`PageController`는 자신만의 `@ExceptionHandler`로 404와 400을 HTML 오류 페이지로 돌려준다. 컨트롤러 안의 핸들러가 전역 `@RestControllerAdvice`보다 먼저 적용되므로, 같은 예외라도 화면에서는 HTML, API에서는 `ErrorBody`가 나간다.

### React의 구조

| 위치 | 책임 |
|---|---|
| `src/api` | `fetch` 호출, 오류를 `ApiError`로 변환, 질의 문자열 조립 |
| `src/auth/session.js` | 로그인 상태 보관, 요청 헤더 생성 |
| `src/pages` | 화면과 입력 처리 |
| `src/App.jsx` | 경로, 내 정보 조회, 상단 메뉴 |

임시 인증(`X-User-Id`)은 `session.js` 한 파일에 모여 있다. f04에서 인증 방식이 바뀔 때 고칠 곳이 여기와 로그인·가입 화면뿐이다.

### 검색 조건은 주소에

목록의 키워드, 태그, 페이지는 `?keyword=..&tag=..&page=..`로 주소에 둔다. 새로고침해도 같은 화면이 나오고, 주소를 그대로 공유할 수 있다.

### 개발 서버 프록시

개발 서버(5173)가 `/api` 요청을 백엔드(8080)로 넘긴다. 브라우저에는 같은 출처로 보이므로 CORS 설정을 추가하지 않는다.

### XSS

| 계층 | 기준 | 테스트 |
|---|---|---|
| Thymeleaf | `th:text` 사용, `th:utext` 금지 | 제목·본문의 `<script>`가 `&lt;script&gt;`로 나가는지 |
| React | 텍스트 노드로 출력, `dangerouslySetInnerHTML` 금지 | 본문의 `<b>`가 요소로 만들어지지 않는지 |

## 자주 만나는 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| `/app/` 새로고침 시 404 | 브라우저 경로를 서버가 모른다 | `HashRouter`를 쓴다 |
| 빌드한 화면에서 CSS·JS를 못 찾는다 | `base`가 `/`라서 `/app/` 아래 경로와 어긋난다 | `vite.config.js`의 `base: './'` |
| Swagger UI에서 요청이 401 | 인증 헤더가 없다 | Authorize에 `X-User-Id` 값을 넣는다 |

## 실행

```bash
./gradlew :projects:final-project:test
cd projects/final-project/frontend && npm ci && npm test && npm run build
```

## 다음 챕터

f04. 품질과 배포
