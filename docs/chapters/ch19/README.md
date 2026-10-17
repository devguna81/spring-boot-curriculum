# ch19. Thymeleaf 기초

## 학습 목표

- `@Controller`가 반환한 문자열이 뷰 이름으로 해석되고, `Model`의 값이 템플릿으로 전달되는 흐름을 이해한다.
- `th:text`, `th:each`, `th:if`/`th:unless`, `th:href`, `th:classappend`를 사용한다.
- `th:text`(이스케이프)와 `th:utext`(이스케이프 안 함)의 차이를 확인한다.
- 공통 조각(fragment)을 `th:replace`로 가져다 쓴다.
- 없는 데이터에 대한 404 오류 페이지를 만든다.

## 선수학습

ch05

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch19-thymeleaf`](../../../examples/ch19-thymeleaf) |
| 화면 | `/users` 목록(활성 필터), `/users/{id}` 상세, 404 페이지 |
| 테스트 | 뷰 이름, 모델, 렌더링된 HTML, 이스케이프 (10건) |
| 다루지 않는 것 | 폼 입력과 저장(ch20), 정적 리소스 번들링, 다국어 메시지 |

## 다음 챕터

ch20. Thymeleaf CRUD
