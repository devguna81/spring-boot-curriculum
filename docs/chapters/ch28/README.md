# ch28. 계층별 테스트

## 학습 목표

- `@WebMvcTest`로 웹 계층만 띄우고 서비스는 `@MockitoBean`으로 바꿔 검사한다.
- `@MybatisTest`로 MyBatis와 DB 계층만 띄워 매퍼와 SQL을 검사한다.
- 서비스는 스프링 없이 Mockito로 검사한다(ch27).
- 슬라이스에 어떤 빈이 포함되고 빠지는지 직접 확인한다.

## 선수학습

ch09, ch27

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch28-slice-tests`](../../../examples/ch28-slice-tests) (ch09의 사용자 CRUD 코드를 같은 구조로 가져와 테스트를 계층별로 다시 작성) |
| 테스트 | 웹 7건, 서비스 3건, 매퍼 6건, 전체 컨텍스트 1건 (17건) |
| 다루지 않는 것 | `@DataJpaTest` 등 JPA 슬라이스, 슬라이스 테스트의 속도 측정 |

## 계층별로 무엇을 검사하나

| 테스트 | 띄우는 것 | 검사하는 것 |
|---|---|---|
| `UserControllerWebTests` | 컨트롤러, MVC 설정, 검증 | 상태 코드, JSON, 검증 오류 400, 예외 매핑 409. 서비스는 목 |
| `UserServiceTests` | 아무것도 (스프링 없음) | 서비스의 분기. 매퍼는 목 |
| `UserMapperSliceTests` | DataSource, MyBatis, 매퍼, 초기 SQL | SQL 결과, 생성된 id, UNIQUE 제약, 롤백 |

## 알아 둘 점

- `@WebMvcTest`의 `@MockitoBean`은 서비스를 대신하므로, 서비스와 매퍼의 실제 동작은 이 테스트로 보장되지 않는다. 계층이 이어진 동작은 ch29에서 확인한다.
- 슬라이스에서 빠진 빈을 쓰려고 하면 컨텍스트가 뜨지 않는다. 테스트가 포함된 빈 목록을 직접 확인하도록 했다.

## 실행

```bash
./gradlew :examples:ch28-slice-tests:test
```

## 다음 챕터

ch29. 통합 테스트
