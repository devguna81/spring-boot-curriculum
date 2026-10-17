# ch24. Contract와 Port/Adapter

## 학습 목표

- 응용 계층이 외부 시스템에 바라는 것을 Port(인터페이스)로 적고, 실제 호출은 Adapter가 맡게 나눈다.
- 외부 API의 응답 모양(`ExternalAddressResponse`)이 어댑터 밖으로 새지 않도록 도메인 객체로 번역한다.
- 외부 호출의 실패(오류 응답, 타임아웃, 연결 거부)를 Port가 약속한 예외로 바꾸고, 웹 계층에서 503으로 응답한다.
- 외부 API 없이도 응용 계층을 테스트하고, 어댑터는 진짜 HTTP 서버(JDK 내장)로 따로 테스트한다.
- 의존 방향(adapter -> application -> domain)을 ArchUnit으로 고정한다.

## 선수학습

ch18, ch23

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch24-port-adapter`](../../../examples/ch24-port-adapter) |
| API | `POST /api/members` (이름, 우편번호 5자리), `GET /api/members/{id}` |
| 외부 API | 우편번호로 주소를 조회하는 가상의 HTTP API (`GET /zipcodes/{code}`). 테스트에서는 JDK 내장 서버로 흉내 낸다 |
| 테스트 | 응용 3건, 어댑터 5건(응답 번역, 404, 500, 읽기 타임아웃, 연결 거부), API 5건, 구조 7건, 컨텍스트 1건 (21건) |
| 다루지 않는 것 | 재시도와 서킷 브레이커, 실제 상용 주소 API 연동, DB 저장(메모리 저장소 사용) |

## 구조

```text
adapter.web         MemberController          (바깥 -> 응용: 입구 Adapter)
application.usecase RegisterMemberUseCase     (바깥이 부르는 입구)
application.port    AddressLookupPort         (응용이 바깥에 바라는 것: 출구 Port)
adapter.address     HttpAddressLookupAdapter  (출구 Adapter: 외부 HTTP API 호출과 번역)
adapter.persistence InMemoryMemberStore       (출구 Adapter: 저장)
domain              Member, Address
```

## 구현하며 확인한 것

외부 API가 없는 우편번호에 `404`와 함께 빈 JSON(`{}`)을 돌려주는 경우, 본문을 그대로 객체로 읽으면 모든 필드가 `null`인 주소가 만들어져 "있는 주소"처럼 보입니다. 처음 구현에서 실제로 이 문제가 테스트로 드러났고, 상태 코드를 먼저 보고 404이면 본문을 읽지 않도록 고쳤습니다.

## 실행과 테스트

```bash
./gradlew :examples:ch24-port-adapter:test
```

`bootRun`으로 직접 실행하려면 `app.address-api.base-url`이 가리키는 주소 API가 있어야 합니다. 기본값은 `http://localhost:9090`이며, 이 저장소에는 해당 서버가 들어 있지 않습니다. 동작 확인은 테스트로 합니다.

## 다음 챕터

ch25. Composite와 Orchestration
