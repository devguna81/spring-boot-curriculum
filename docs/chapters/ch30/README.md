# ch30. Spring Security

## 학습 목표

- 인증(누구인가)과 인가(무엇을 할 수 있는가)를 구분한다.
- `SecurityFilterChain`으로 URL별 규칙을 정하고, 401(인증 안 됨)과 403(권한 없음)을 JSON으로 응답한다.
- 비밀번호를 `PasswordEncoder`(bcrypt)로 저장한다.
- `@PreAuthorize`로 "본인 것이거나 관리자만" 같은 데이터에 따른 권한을 건다.
- `@WithMockUser`, `httpBasic()`으로 보안 규칙을 테스트한다.

## 선수학습

ch07

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch30-security`](../../../examples/ch30-security) |
| 방식 | HTTP Basic, 세션 없음(stateless), CSRF 토큰 끔 |
| 계정 | 학습용 메모리 계정 3개(kim, lee: USER / root: USER+ADMIN) |
| 테스트 | 인증 5건, 인가 5건, 메서드 보안 4건, 무상태 2건, 컨텍스트 1건 (16건) |
| 다루지 않는 것 | 폼 로그인, JWT, OAuth2, CORS 연동(ch22 참고), DB 계정 저장 |

## 확인한 것

- Spring Security 7에서는 비밀번호로 로그인한 사용자의 권한 목록에 `ROLE_USER` 외에 `FACTOR_PASSWORD`가 함께 붙어 있었다. 권한을 목록의 위치로 단정하지 말고 포함 여부로 검사한다.
- Boot 4에서는 `spring-security-test`만으로는 `@WithMockUser`가 MockMvc 요청에 적용되지 않아 모든 요청이 401이었다. `spring-boot-starter-security-test`로 바꾸자 동작했다.
- 관리자 규칙(`hasRole("ADMIN")`)과 `@PreAuthorize`를 각각 지우면 해당 테스트가 실패한다.
- CSRF 토큰을 끈 것은 쿠키 세션이 없는 API라서 가능한 선택이다. 쿠키 세션을 쓰는 화면(ch20)이라면 켜 두어야 한다.

## 주의

예제의 계정과 비밀번호는 학습용이다. 실제 서비스에서는 코드나 저장소에 비밀번호를 두지 않는다.

## 실행

```bash
./gradlew :examples:ch30-security:test
./gradlew :examples:ch30-security:bootRun
# curl -u kim:kim-pass http://localhost:8080/api/me
```

## 다음 챕터

ch31. Logging과 Actuator
