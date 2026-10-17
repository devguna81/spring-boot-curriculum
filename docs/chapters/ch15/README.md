# ch15. JSON과 Java 객체 변환

## 학습 목표

- Jackson으로 객체와 JSON 문자열을 오간다.
- MyBatis TypeHandler로 JSON 컬럼을 record에 매핑한다.
- TypeHandler를 Spring Bean으로 등록해 Boot의 JsonMapper를 주입한다.
- 누락된 키, 추가 키, null, 잘못된 JSON이 어떻게 처리되는지 확인한다.

## 선수학습

ch14

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch15-json-typehandler`](../../../examples/ch15-json-typehandler) |
| API | `POST /products`, `GET /products/{id}` |
| 테스트 | TypeHandler 단위 테스트, H2/Oracle 왕복 (H2 14건, Oracle 3건) |
| 다루지 않는 것 | JSON 스키마 검증, 다형 타입 직렬화 |

## 다음 챕터

ch16. DTO, Domain, MapStruct
