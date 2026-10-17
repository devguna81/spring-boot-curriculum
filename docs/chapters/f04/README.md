# f04. 품질과 배포

## 학습 목표

- 임시 인증을 HTTP Basic 인증으로 바꾸되, 서비스 코드는 고치지 않는다.
- 필터 단계의 오류(401, 403)도 API의 오류 형식과 같게 만든다.
- 인가 규칙을 "허용 목록 + 기본 거부" 방식으로 쓰고 테스트로 고정한다.
- Actuator는 필요한 것만 열고 운영 정보는 관리자에게만 보인다.
- React 빌드와 서버 빌드를 한 이미지로 묶고, 실행 중인 서버를 스모크 테스트로 점검한다.
- CI에서 모든 검증(서버, React, Oracle, 이미지)을 돌린다.

## 선수학습

ch29, ch30, ch31, ch32, ch33, f03

## 범위

| 항목 | 내용 |
|---|---|
| 프로젝트 | [`projects/final-project`](../../../projects/final-project) |
| 인증·인가 | `security` 패키지: `SecurityConfig`, `AppUserDetailsService`, `JsonErrorHandlers` |
| 운영 | Actuator: `health`(공개), `info`, `metrics`(관리자) |
| 배포 | [`Dockerfile`](../../../projects/final-project/Dockerfile)(3단계), [`docker-compose.yml`](../../../projects/final-project/docker-compose.yml), [`smoke-test.sh`](../../../projects/final-project/smoke-test.sh) |
| CI | `build.yml`(서버, Oracle, React), `image.yml`(이미지와 스모크 테스트) |
| 테스트 | 보안 11건, 구조 8건(누적), React 36건(누적), 스모크 테스트 |

## 핵심 설계

### 인증을 바꿔도 서비스는 그대로

f02에서 서비스는 `Actor(id, role)`만 받도록 만들었다. 그래서 인증 방식을 헤더에서 HTTP Basic으로 바꿀 때 고친 곳은 `CurrentActor` 한 클래스다. 구조 테스트가 이를 고정한다.

| 규칙 | 의미 |
|---|---|
| `post`는 Spring Security를 모른다 | 서비스가 인증 방식에 묶이지 않는다 |
| `SecurityContextHolder`는 `security`와 `web`만 쓴다 | 보안 객체가 여기저기 퍼지지 않는다 |
| `security`는 `post`와 `user.internal`을 모른다 | 보안 설정이 업무 구현에 묶이지 않는다 |

### 인가는 두 층

| 층 | 판단 | 위치 |
|---|---|---|
| 필터 | 로그인했는가, 관리자인가 | `SecurityConfig` |
| 서비스 | 내 글인가 | `PostService` |

글의 주인은 URL만으로 알 수 없다. 데이터를 읽어야 하므로 서비스가 판단한다.

### 허용 목록과 기본 거부

`SecurityConfig`는 공개할 경로를 하나씩 나열하고 마지막에 `anyRequest().authenticated()`를 둔다. 경로를 새로 추가하고 규칙을 깜빡해도 공개되지 않는다. 반대로 "막을 것을 나열하고 나머지를 허용"하는 방식은 깜빡하는 순간 열린다.

### 401에 `WWW-Authenticate`를 싣지 않는다

헤더가 있으면 브라우저가 자체 로그인 창을 띄워 React 화면의 로그인 흐름을 가린다. 응답 본문은 컨트롤러가 내는 `ErrorBody`와 같은 모양으로 직접 쓴다. 필터 단계의 예외는 `@RestControllerAdvice`에 닿지 않기 때문이다.

### 계정 존재 여부를 숨긴다

없는 아이디와 틀린 비밀번호는 같은 응답이다. 다르면 어떤 아이디가 있는지 알아낼 수 있다. `SecurityTests`가 두 응답 본문이 같은지 비교한다.

### CSRF를 끈 이유

쿠키 세션 없이 매 요청에 `Authorization` 헤더를 직접 싣는다. 브라우저가 자동으로 붙이는 쿠키가 없어 CSRF 공격이 성립하지 않는다. 쿠키로 로그인 상태를 유지하는 방식으로 바꾸면 반드시 켠다.

### 브라우저에 저장하는 값

`sessionStorage`에 비밀번호 원문이 아니라 만들어 둔 `Basic ...` 헤더 값을 둔다. 탭을 닫으면 사라지지만, 같은 출처에서 스크립트가 실행되면(XSS) 읽힐 수 있다. 그래서 f03에서 사용자 입력을 HTML로 해석하지 않는 기준을 세웠다. 이 한계를 없애려면 토큰이나 HttpOnly 쿠키 방식이 필요하며 이 프로젝트의 범위 밖이다.

### Actuator

| 경로 | 접근 |
|---|---|
| `/actuator/health`, `/liveness`, `/readiness` | 누구나. 상태(UP/DOWN)만 보인다 |
| `/actuator/health`의 세부 항목(DB 등) | 관리자 |
| `/actuator/metrics` | 관리자 |
| `env`, `beans` 등 | 노출하지 않음(관리자도 404) |

### 데모 데이터와 비밀번호

코드에 기본 비밀번호를 두지 않는다. `SEED_DEMO_DATA=true`이면 `DEMO_PASSWORD`(8자 이상)를 직접 정해야 하고, 없으면 서버가 시작하지 않는다. CI는 실행마다 무작위 값을 만들어 로그에 가린다.

### 이미지

| 단계 | 하는 일 |
|---|---|
| 1. frontend | `npm ci`, `npm run build` |
| 2. build | React 결과를 `static/app`에 넣고 `bootJar`, 계층(layer)으로 풀기 |
| 3. 실행 | JRE만, root가 아닌 사용자, 헬스체크 |

테스트는 이미지 빌드에서 돌리지 않는다. CI의 `build` 작업이 이미 돌렸고, 이미지 작업은 "이 이미지가 실제로 뜨는가"를 `smoke-test.sh`로 확인한다.

### 스모크 테스트가 보는 것

readiness, 서버 렌더링 화면, 이미지에 React가 들어 있는지, 가입과 로그인, 로그인 없는 쓰기 거부(401), 일반 회원의 관리자 API 거부(403), 틀린 비밀번호 거부, 글 작성 후 태그 검색으로 다시 찾기. 200만 확인하면 오류 페이지도 통과하므로 응답 내용까지 본다.

## 자주 만나는 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| 모든 요청이 401 | 규칙 목록에 없는 경로는 기본 거부다 | 공개할 경로를 `SecurityConfig`에 추가한다 |
| `@WithMockUser`가 401 | `spring-security-test`만으로는 부족하다 | `spring-boot-starter-security-test` |
| 브라우저에 로그인 창이 뜬다 | 401에 `WWW-Authenticate: Basic`이 실렸다 | 직접 만든 EntryPoint로 응답한다 |
| 한글 비밀번호로 로그인이 안 된다 | `btoa`는 Latin-1만 받는다 | UTF-8 바이트로 바꾼 뒤 Base64 |
| 데모 데이터를 켰더니 서버가 안 뜬다 | `DEMO_PASSWORD`가 없거나 짧다 | 8자 이상으로 지정한다 |

## 실행

```bash
./gradlew :projects:final-project:test
cd projects/final-project/frontend && npm ci && npm test
docker build -f projects/final-project/Dockerfile -t final-project .
docker run -d -p 8080:8080 -e SEED_DEMO_DATA=true -e DEMO_PASSWORD=원하는값8자이상 final-project
projects/final-project/smoke-test.sh http://localhost:8080
```

## 마무리

종합 프로젝트는 여기까지다. ch01~ch33에서 배운 기능이 한 프로젝트에서 어떻게 맞물리는지, 그리고 인증처럼 나중에 바뀌는 부분을 어디에 가둬 두면 바뀌는 범위가 작아지는지를 확인했다.
