# 05. 화면 구성

화면은 두 가지로 나눈다. 읽기만 하는 화면은 서버가 HTML을 만들고(Thymeleaf), 입력이 많은 화면은 React가 REST API를 호출해 그린다.

| 구분 | 주소 | 하는 일 | 구현 |
|---|---|---|---|
| 서버 렌더링 | `/pages/posts` | 글 목록, 검색, 페이징 | Thymeleaf |
| 서버 렌더링 | `/pages/posts/{id}` | 글 상세 | Thymeleaf |
| React | `/app/#/posts` | 글 목록, 검색, 페이징 | React |
| React | `/app/#/posts/{id}` | 상세, 수정·삭제 버튼 | React |
| React | `/app/#/posts/new`, `/app/#/posts/{id}/edit` | 글 작성, 수정 | React |
| React | `/app/#/signup`, `/app/#/login` | 가입, 로그인 | React |
| API 문서 | `/swagger-ui.html` | 요청을 직접 보내 보기 | springdoc |

## 왜 나누는가

- 읽기 화면은 서버에서 완성된 HTML을 주면 JavaScript 없이도 열리고, 검색 결과 주소를 그대로 공유할 수 있다.
- 쓰기 화면은 입력 검증 오류를 칸마다 보여 주고 저장 후 이동하는 흐름이 있어 화면 상태가 필요하다. 이 부분은 React가 낫다.
- 두 화면이 같은 서비스(`PostApi`)를 쓰므로 검색 규칙은 한 곳에만 있다.

## 보안 관점의 기준

- Thymeleaf는 `th:text`로 값을 넣어 HTML을 이스케이프한다. `th:utext`는 쓰지 않는다.
- React는 텍스트 노드를 이스케이프한다. `dangerouslySetInnerHTML`은 쓰지 않는다.
- 수정·삭제 버튼을 숨기는 것은 편의다. 권한 검사는 서버 서비스가 한다.

## React 화면 규칙

- 서버 호출은 `src/api`에만 둔다. 화면 컴포넌트는 `fetch`를 직접 부르지 않는다.
- 로그인 상태는 `src/auth/session.js`만 안다. 인증 방식이 바뀌어도 이 파일과 로그인·가입 화면만 고친다.
- 목록의 검색 조건은 주소에 둔다. 새로고침이나 주소 공유에도 같은 결과가 나온다.
- 서버 오류는 `ErrorBody`의 `issues`를 입력 칸 옆에 보여 준다.
- 정적 파일로 제공하므로 `HashRouter`를 쓴다. 새로고침해도 서버에 경로가 전달되지 않는다.
