# ch20. Thymeleaf CRUD

## 학습 목표

- 폼 객체와 `th:object`, `th:field`로 입력 화면을 만든다.
- `@Valid`와 `BindingResult`로 검증 오류를 받고, 같은 폼에 입력값과 오류를 다시 보여 준다.
- 변환 오류(숫자 칸에 문자)와 검증 오류(빈 값)가 다르게 나타나는 것을 확인한다.
- 등록, 수정, 삭제 뒤에 redirect 하는 PRG(Post - Redirect - Get) 패턴과 Flash 속성을 사용한다.
- 중복 이메일 같은 업무 오류를 필드 오류로 바꿔 화면에 보여 준다.

## 선수학습

ch06, ch19

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch20-thymeleaf-crud`](../../../examples/ch20-thymeleaf-crud) |
| 화면 | `/users`, `/users/new`, `/users/{id}/edit`, 수정과 삭제는 POST |
| 테스트 | 폼 표시, 검증 실패, 변환 오류, 중복 이메일, PRG와 Flash, 이스케이프, 수정, 삭제, 404 (14건) |
| 다루지 않는 것 | CSRF 방어와 인증(ch30), DB 저장(메모리 저장소 사용), 파일 업로드 |

## 다음 챕터

ch21. React 기초
