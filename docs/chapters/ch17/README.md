# ch17. OpenAPI와 Swagger UI

## 학습 목표

- springdoc이 컨트롤러에서 OpenAPI 문서를 만드는 방식을 설명한다.
- @Operation, @ApiResponses, @Schema로 문서를 보강한다.
- 검증 애노테이션이 스키마에 반영되는 것을 확인한다.
- 내부용 엔드포인트를 @Hidden으로 숨기고, 운영 Profile에서는 문서를 끈다.

## 선수학습

ch07

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch17-openapi`](../../../examples/ch17-openapi) |
| API | `/users` CRUD, `/v3/api-docs`, `/swagger-ui.html` |
| 테스트 | ch07 동작 테스트 + 문서 내용 테스트 (29건) |
| 다루지 않는 것 | 인증 스키마 문서화, 문서 기반 클라이언트 코드 생성 |

## 다음 챕터

ch18. API Contract
