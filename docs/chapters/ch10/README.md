# ch10. 동적 SQL, JOIN, 페이징

## 학습 목표

- <if>, <where>, <foreach>, <choose>로 검색 조건을 조합한다.
- JOIN 결과를 중첩 객체(association)와 목록(collection)으로 매핑한다.
- OFFSET/FETCH 페이징과 전체 건수 조회를 구현한다.
- 정렬 기준을 허용 목록으로 제한해 SQL 주입을 막는다.

## 선수학습

ch09

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch10-dynamic-sql`](../../../examples/ch10-dynamic-sql) |
| API | `GET /posts`, `GET /users/{id}/posts` |
| 테스트 | 조건 조합, JOIN 매핑, 페이징 경계 (22건) |
| 다루지 않는 것 | N+1 문제의 해결 전략, 커서 기반 페이징 |

## 다음 챕터

ch11. 트랜잭션
