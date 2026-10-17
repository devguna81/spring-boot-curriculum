# 02. 구조

## 패키지

```text
com.devguna.curriculum.finalproject
  contract/   외부에 약속하는 요청/응답 타입 (어떤 패키지도 참조하지 않는다)
  common/     오류 처리, 페이징 같은 공통 코드
  user/       회원 모듈   api/ (공개)  internal/ (비공개)
  post/       글 모듈     api/ (공개)  internal/ (비공개)
  web/        컨트롤러. 모듈의 api만 사용한다
  security/   인증·인가 설정
```

모듈은 ch26의 규칙을 따른다. `internal`은 패키지 전용이고, 다른 모듈은 `api` 인터페이스로만 접근한다. 모듈 사이에 순환이 없어야 한다.

## 의존 방향

```text
web -> post.api, user.api -> (구현은 각 internal)
post.internal -> user.api (작성자 존재 확인)
user -> post (없음)
```

## 기술 선택 기준

| 대상 | 선택 | 이유 |
|---|---|---|
| 단순 CRUD(회원) | MyBatis-Plus `BaseMapper` | 반복을 줄인다(ch12) |
| 검색, JOIN, JSON 조건(글) | MyBatis XML | 복잡한 질의는 SQL이 보여야 한다(ch10, ch14) |
| JSON 컬럼 | TypeHandler + record | 구조화된 값을 한 컬럼에 둔다(ch15) |
| 객체 변환 | MapStruct, 누락 필드는 컴파일 오류 | 변환 실수를 빌드에서 잡는다(ch16) |
| API 문서 | springdoc | 코드와 문서가 어긋나지 않게 한다(ch17) |

## 의도한 예외

글 검색 질의는 작성자 표시 이름을 얻으려고 `fp_users`를 JOIN한다. 모듈이 테이블을 하나씩 소유한다는 원칙(ch26)의 예외이며, 읽기 전용 조회에만 허용한다. 쓰기는 항상 소유 모듈을 거친다.
