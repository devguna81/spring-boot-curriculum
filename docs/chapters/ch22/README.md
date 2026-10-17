# ch22. React와 REST API 연동

## 학습 목표

- `fetch`로 REST API를 호출하고, HTTP 오류(4xx, 5xx)에서는 예외가 던져지지 않는다는 점을 직접 처리한다.
- API 호출 코드를 한 모듈로 모으고, 서버의 오류 본문을 `ApiError`로 전달한다.
- 목록, 등록, 수정, 삭제 화면에서 로딩, 오류, 빈 목록 상태를 나누어 보여 준다.
- 서버의 검증 오류(`issues`)를 입력 칸 옆에 표시하고, 중복 이메일(409)도 같은 방식으로 보여 준다.
- 포트가 다른 화면(5173)과 API(8080) 사이의 CORS 설정과 사전 요청(preflight)을 이해한다.
- ch20(Thymeleaf)과 같은 기능을 React로 구현해 두 방식을 비교한다.

## 선수학습

ch05, ch21

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch22-react-rest`](../../../examples/ch22-react-rest) (백엔드는 모듈 루트, 화면은 `frontend/`) |
| API | `/api/users` 목록, 단건, 등록(201), 수정, 삭제(204), 오류는 `code`/`message`/`issues` |
| 테스트 | 백엔드 15건(API, 검증, 중복, CORS), 화면 18건(API 클라이언트 6, 화면 12) |
| 다루지 않는 것 | 인증과 토큰(ch30), 개발 서버 프록시, 데이터 캐싱 라이브러리, DB 저장(메모리 저장소 사용) |

## 실행

터미널 두 개를 사용합니다.

```bash
# 1) 백엔드 (http://localhost:8080)
./gradlew :examples:ch22-react-rest:bootRun

# 2) 화면 (http://localhost:5173)
cd examples/ch22-react-rest/frontend
npm ci
npm run dev
```

화면은 `.env.development`의 `VITE_API_BASE`(기본 `http://localhost:8080`)로 API를 호출합니다. 허용 출처는 백엔드 `app.cors.allowed-origins`로 바꿉니다.

## Thymeleaf(ch20)와 React(ch22) 비교

| 구분 | Thymeleaf (ch20) | React + REST (ch22) |
|---|---|---|
| 화면을 만드는 곳 | 서버가 HTML을 완성해 보냄 | 브라우저가 JSON을 받아 화면을 그림 |
| 서버의 응답 | HTML | JSON (화면과 무관) |
| 검증 오류 표시 | `BindingResult`가 같은 폼을 다시 렌더링 | 서버의 `issues`를 받아 화면 코드가 입력 칸에 연결 |
| 저장 후 이동 | 서버가 redirect (PRG), 안내는 Flash 속성 | 화면 코드가 라우트를 이동 (`navigate`), 안내는 라우터 state |
| 새로고침 | 서버에서 다시 그림 | 화면 코드가 API를 다시 호출 |
| 포트와 CORS | 같은 출처라 필요 없음 | 출처가 다르면 서버가 허용해야 함 |
| 로딩과 오류 상태 | 서버가 끝낼 때까지 브라우저가 기다림 | 화면 코드가 직접 구분해서 보여 줘야 함 |
| 같은 API를 다른 클라이언트가 | 쓰기 어려움 | 모바일 앱 등도 같은 API 사용 가능 |

## 다음 챕터

ch23. Layered Architecture와 SOLID
