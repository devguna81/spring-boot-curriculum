# ch14. Oracle JSON

## 학습 목표

- JSON 타입 컬럼과 CLOB + IS JSON 제약의 차이를 설명한다.
- JSON_VALUE, JSON_QUERY, JSON_EXISTS, JSON_TABLE을 MyBatis XML에서 사용한다.
- JSON_VALUE의 기본 반환 타입이 문자열임을 알고 RETURNING NUMBER로 숫자 비교를 한다.
- 잘못된 JSON이 저장 단계에서 거부되는 것을 확인한다.

## 선수학습

ch13

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch14-oracle-json`](../../../examples/ch14-oracle-json) |
| API | `GET /products/{id}`, `GET /products/{id}/tags`, `GET /products` (oracle Profile) |
| 테스트 | Oracle 전용 질의 (Oracle 10건, 기본 1건) |
| 다루지 않는 것 | JSON 검색 인덱스, JSON Duality View |

## 다음 챕터

ch15. JSON과 Java 객체 변환
