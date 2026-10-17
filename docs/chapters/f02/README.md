# f02. 백엔드

## 학습 목표

- 모듈 경계(`api`와 `internal`)를 지키며 사용자와 게시글 기능을 구현한다.
- 단순 CRUD는 MyBatis-Plus `BaseMapper`로, 검색·JOIN·집계는 XML Mapper로 나눠 쓴다.
- JSON 컬럼을 TypeHandler로 객체에 매핑하고, DB마다 다른 JSON 조건을 `_databaseId`로 분기한다.
- MapStruct로 저장용 객체를 계약 타입으로 바꾸고, 빠진 필드를 컴파일 시점에 잡는다.
- 검증 실패, 권한 없음, 대상 없음, 중복을 같은 모양의 오류 응답으로 돌려준다.

## 선수학습

ch10, ch12, ch15, ch16, ch26, f01

## 범위

| 항목 | 내용 |
|---|---|
| 프로젝트 | [`projects/final-project`](../../../projects/final-project) |
| 사용자 모듈 | 가입(bcrypt 해시), 조회, 계정 생성 (`user.api` / `user.internal`) |
| 게시글 모듈 | 작성, 수정, 삭제, 상세, 검색, 작성자별 통계 (`post.api` / `post.internal`) |
| 검색 | 키워드(제목·본문), 작성자, 태그(정확히 일치), 페이징, 고정 글 우선 정렬 |
| 데모 데이터 | `app.seed-demo-data=true`일 때 계정 3개와 글 4개를 만든다 |
| 임시 인증 | `X-User-Id` 헤더. 위조가 가능하므로 f04에서 Spring Security로 교체한다 |
| 테스트 | 사용자 7건, 게시글 API 10건, 검색 시나리오 11건(H2, Oracle 각각), TypeHandler 3건, 구조 5건 |

## 핵심 설계

### 어떤 도구를 어디에 쓰는가

| 작업 | 도구 | 이유 |
|---|---|---|
| 사용자 저장·조회 | `BaseMapper` | 조건이 단순해서 SQL을 쓸 필요가 없다 |
| 글 저장·수정·삭제 | `BaseMapper` | 같은 이유 |
| 글 목록 검색, 건수 | XML Mapper | JOIN, 동적 조건, JSON 필터, 페이징이 한 쿼리에 모인다 |
| 작성자별 통계 | XML Mapper | LEFT JOIN과 GROUP BY |

### 검색 조건은 한 곳에서 만든다

목록과 건수는 같은 조건을 써야 총 개수가 어긋나지 않는다. `<sql id="searchConditions">`에 조건을 두고 두 쿼리가 `<include>`한다.

### JSON 태그 필터

| DB | 방식 |
|---|---|
| Oracle | `JSON_EXISTS(meta, '$.tags[*]?(@ == $t)' PASSING #{tag} AS "t")` |
| H2 | meta가 CLOB이라 `"태그"` 모양(따옴표 포함)을 `LIKE`로 찾는다 |

`DatabaseIdProvider` 빈을 등록해야 XML의 `_databaseId`가 `h2`, `oracle`로 채워진다. 설정이 없으면 항상 null이라 H2 쪽 분기만 탄다.

### LIKE 특수문자

사용자가 `%`나 `_`를 입력해도 글자 그대로 찾도록 서비스에서 이스케이프하고, SQL은 `ESCAPE '\'`를 쓴다. 태그는 JSON 문자열로 먼저 바꾼 뒤 이스케이프하므로 따옴표가 들어 있어도 쿼리가 깨지지 않는다.

### 모듈 사이의 약속

- 다른 모듈은 `user.api`, `post.api`만 안다. `internal`을 참조하면 구조 테스트가 실패한다.
- `post`는 `user.api`를 쓰지만 `user`는 `post`를 모른다. 의존은 한 방향이다.
- 서비스는 HTTP를 모르고 `Actor`(id, 역할)만 받는다. 인증 방식이 바뀌어도 서비스는 그대로다.

### MapStruct

`unmappedTargetPolicy=ERROR`로 컴파일해서 계약 타입에 필드를 추가하고 변환을 고치지 않으면 빌드가 실패한다. 생성된 구현체가 접근자를 호출하므로 엔티티의 getter와 setter는 `public`이어야 한다.

## 자주 만나는 문제

| 증상 | 원인 | 해결 |
|---|---|---|
| `NoSuchMethodException: PostRow.<init>(Long, ...)` | resultMap 생성자 인자의 `javaType="long"`이 `Long`으로 해석된다 | record의 기본 타입이면 `_long`, `_boolean`을 쓴다 |
| MapStruct가 `has no property named` 오류 | 엔티티 접근자가 package-private | `public`으로 연다 |
| meta 조회에서 `String`이 객체로 변환되지 않는다 | `autoResultMap`이 꺼져 있다 | `@TableName(autoResultMap = true)` |

## 실행

```bash
./gradlew :projects:final-project:test
./gradlew :projects:final-project:bootRun
```

Oracle까지 확인하려면 `ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD`를 설정하고 같은 테스트를 실행한다. 설정이 없으면 Oracle 테스트는 건너뛴다.

## 다음 챕터

f03. 화면과 문서
