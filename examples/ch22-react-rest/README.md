# ch22. React와 REST API 연동

`fetch`로 REST API를 호출하고, HTTP 오류(4xx, 5xx)에서는 예외가 던져지지 않는다는 점을 직접 처리한다.
설명은 [docs/chapters/ch22](../../docs/chapters/ch22/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)
- Node.js 24 (npm)

## 범위

| 항목 | 내용 |
|---|---|
| API | `/api/users` 목록, 단건, 등록(201), 수정, 삭제(204), 오류는 `code`/`message`/`issues` |
| 테스트 | 백엔드 15건(API, 검증, 중복, CORS), 화면 18건(API 클라이언트 6, 화면 12) |
| 다루지 않는 것 | 인증과 토큰(ch30), 개발 서버 프록시, 데이터 캐싱 라이브러리, DB 저장(메모리 저장소 사용) |

## 구조

```text
ch22-react-rest/
├── build.gradle
├── frontend/
│   ├── .env.development
│   ├── index.html
│   ├── package-lock.json
│   ├── package.json
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.test.jsx
│   │   ├── api/
│   │   │   ├── usersApi.js
│   │   │   └── usersApi.test.js
│   │   ├── main.jsx
│   │   ├── pages/
│   │   │   ├── UserFormPage.jsx
│   │   │   └── UserListPage.jsx
│   │   ├── styles.css
│   │   └── test/
│   │       ├── fakeServer.js
│   │       └── setup.js
│   └── vite.config.js
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch22/
    │   │   ├── Ch22Application.java
    │   │   ├── CorsConfig.java
    │   │   ├── DuplicateEmailException.java
    │   │   ├── ErrorBody.java
    │   │   ├── GlobalExceptionHandler.java
    │   │   ├── User.java
    │   │   ├── UserController.java
    │   │   ├── UserNotFoundException.java
    │   │   ├── UserRequest.java
    │   │   └── UserService.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch22/
        └── UserApiTests.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch22-react-rest:bootRun` |
| Windows | `gradlew.bat :examples:ch22-react-rest:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch22-react-rest:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch22-react-rest:build` | `gradlew.bat clean build` |

## 화면 실행과 테스트 (`frontend/`에서)

```bash
cd frontend
npm ci
npm run dev    # 개발 서버
npm test
```
