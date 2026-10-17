# ch08. DataSource와 H2

## 학습 목표

- DataSource와 커넥션 풀(HikariCP)이 하는 일을 설명한다.
- spring.datasource 설정으로 인메모리 H2에 연결한다.
- schema.sql, data.sql로 초기 테이블과 데이터를 만든다.
- JdbcClient로 조회하고 이름 있는 파라미터를 바인딩한다.

## 선수학습

ch05, ch07

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch08-datasource-h2`](../../../examples/ch08-datasource-h2) |
| API | `GET /users`, `GET /users/{id}`, `GET /db/info` |
| 테스트 | DataSource 설정, 초기 데이터, JdbcClient 조회 (11건) |
| 다루지 않는 것 | 쓰기 쿼리, MyBatis(ch09), 트랜잭션(ch11) |

## 다음 챕터

ch09. MyBatis 기본과 CRUD
