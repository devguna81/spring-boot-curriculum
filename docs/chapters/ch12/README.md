# ch12. MyBatis-Plus로 반복 CRUD 줄이기

## 학습 목표

- BaseMapper로 반복되는 CRUD SQL을 제거한다.
- LambdaQueryWrapper로 조건을 타입 안전하게 조합한다.
- 페이징 플러그인을 등록하고 동작을 확인한다.
- XML Mapper와 BaseMapper를 한 인터페이스에서 함께 쓴다.
- MyBatis와 MyBatis-Plus 중 무엇을 고를지 기준을 세운다.

## 선수학습

ch09, ch11

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch12-mybatis-plus`](../../../examples/ch12-mybatis-plus) |
| API | `/users` CRUD, `GET /users/search`, `GET /users/older-than/{age}` |
| 테스트 | ch09와 같은 시나리오 + 조건 조합, 페이징 (19건) |
| 다루지 않는 것 | 코드 생성기, 논리 삭제, 자동 채움 |

## 다음 챕터

ch13. Oracle 연결
