# ch16. DTO, Domain, MapStruct

## 학습 목표

- DTO와 Domain을 나누는 이유를 설명한다.
- MapStruct 매퍼를 선언하고 컴파일 때 생성되는 구현 코드를 읽는다.
- 이름이 다른 필드, 상수, 변환이 필요한 필드를 매핑한다.
- @MappingTarget과 null 무시 전략으로 부분 수정을 구현한다.
- 매핑되지 않은 필드를 컴파일 오류로 만든다.

## 선수학습

ch09

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch16-mapstruct`](../../../examples/ch16-mapstruct) |
| API | `POST /members`, `GET /members`, `GET /members/{id}`, `PATCH /members/{id}` |
| 테스트 | 매퍼 단위 테스트, 응답에 비밀값이 없는지 API 테스트 (13건) |
| 다루지 않는 것 | 여러 소스 객체 합치기, 사용자 정의 애노테이션 |

## 다음 챕터

ch17. OpenAPI와 Swagger UI
