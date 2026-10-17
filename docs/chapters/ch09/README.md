# ch09. MyBatis 기본과 CRUD

## 학습 목표

- Mapper 인터페이스와 XML Mapper가 이어지는 방식을 설명한다.
- resultMap으로 컬럼과 필드를 매핑한다.
- useGeneratedKeys로 생성된 id를 받는다.
- #{}와 ${}의 차이를 설명한다.
- UNIQUE 위반이 DuplicateKeyException으로 번역되는 것을 확인한다.

## 선수학습

ch08

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch09-mybatis-basics`](../../../examples/ch09-mybatis-basics) |
| API | `/users` CRUD (H2) |
| 테스트 | Mapper 단위 테스트와 API 테스트 (15건) |
| 다루지 않는 것 | 동적 SQL과 JOIN(ch10), 트랜잭션(ch11) |

## 다음 챕터

ch10. 동적 SQL, JOIN, 페이징
