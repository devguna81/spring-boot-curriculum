# ch24. Contract와 Port/Adapter

응용 계층이 외부 시스템에 바라는 것을 Port(인터페이스)로 적고, 실제 호출은 Adapter가 맡게 나눈다.
설명은 [docs/chapters/ch24](../../docs/chapters/ch24/README.md)에 있다.

## 환경

- JDK 21
- Gradle Wrapper 포함 (별도 설치 불필요)

## 범위

| 항목 | 내용 |
|---|---|
| API | `POST /api/members` (이름, 우편번호 5자리), `GET /api/members/{id}` |
| 외부 API | 우편번호로 주소를 조회하는 가상의 HTTP API (`GET /zipcodes/{code}`). 테스트에서는 JDK 내장 서버로 흉내 낸다 |
| 테스트 | 응용 3건, 어댑터 5건(응답 번역, 404, 500, 읽기 타임아웃, 연결 거부), API 5건, 구조 7건, 컨텍스트 1건 (21건) |
| 다루지 않는 것 | 재시도와 서킷 브레이커, 실제 상용 주소 API 연동, DB 저장(메모리 저장소 사용) |

## 구조

```text
ch24-port-adapter/
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/devguna/curriculum/ch24/
    │   │   ├── Ch24Application.java
    │   │   ├── adapter/
    │   │   │   ├── address/
    │   │   │   │   ├── AddressApiConfig.java
    │   │   │   │   ├── AddressApiProperties.java
    │   │   │   │   ├── ExternalAddressResponse.java
    │   │   │   │   └── HttpAddressLookupAdapter.java
    │   │   │   ├── persistence/
    │   │   │   │   └── InMemoryMemberStore.java
    │   │   │   └── web/
    │   │   │       ├── GlobalExceptionHandler.java
    │   │   │       ├── MemberController.java
    │   │   │       ├── MemberRequest.java
    │   │   │       └── MemberResponse.java
    │   │   ├── application/
    │   │   │   ├── port/
    │   │   │   │   ├── AddressLookupPort.java
    │   │   │   │   ├── AddressLookupUnavailableException.java
    │   │   │   │   └── MemberStore.java
    │   │   │   └── usecase/
    │   │   │       ├── MemberNotFoundException.java
    │   │   │       ├── RegisterMemberService.java
    │   │   │       ├── RegisterMemberUseCase.java
    │   │   │       └── UnknownPostalCodeException.java
    │   │   └── domain/
    │   │       ├── Address.java
    │   │       └── Member.java
    │   └── resources/
    │       └── application.yml
    └── test/java/com/devguna/curriculum/ch24/
        ├── ArchitectureTests.java
        ├── Ch24ApplicationTests.java
        ├── FakeAddressServer.java
        ├── HttpAddressLookupAdapterTests.java
        ├── MemberApiTests.java
        ├── RegisterMemberServiceTests.java
        └── fixture/leaky/
            └── LeakyService.java
```

## 실행 (저장소 루트에서)

| OS | 명령 |
|---|---|
| Linux / macOS | `./gradlew :examples:ch24-port-adapter:bootRun` |
| Windows | `gradlew.bat :examples:ch24-port-adapter:bootRun` |

## 빌드와 테스트

| OS | 이 챕터만 | 전체 |
|---|---|---|
| Linux / macOS | `./gradlew :examples:ch24-port-adapter:build` | `./gradlew clean build` |
| Windows | `gradlew.bat :examples:ch24-port-adapter:build` | `gradlew.bat clean build` |
