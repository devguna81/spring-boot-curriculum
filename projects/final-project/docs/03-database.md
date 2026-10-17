# 03. 데이터베이스

테이블 이름은 같은 Oracle 스키마를 쓰는 다른 예제와 겹치지 않게 `fp_` 접두사를 붙인다.

## fp_users

| 컬럼 | 타입 | 제약 |
|---|---|---|
| id | BIGINT 자동 증가 | PK |
| username | VARCHAR(30) | NOT NULL, UNIQUE |
| password_hash | VARCHAR(100) | NOT NULL (bcrypt) |
| display_name | VARCHAR(30) | NOT NULL |
| role | VARCHAR(10) | NOT NULL, `USER` 또는 `ADMIN` |
| created_at | TIMESTAMP | 기본값 현재 시각 |

## fp_posts

| 컬럼 | 타입 | 제약 |
|---|---|---|
| id | BIGINT 자동 증가 | PK |
| author_id | BIGINT | NOT NULL, FK -> fp_users.id |
| title | VARCHAR(100) | NOT NULL |
| content | CLOB | NOT NULL |
| pinned | BOOLEAN(H2) / NUMBER(1)(Oracle) | NOT NULL, 기본값 false. 정렬에 쓰므로 JSON 밖의 컬럼이다 |
| meta | CLOB(H2) / JSON(Oracle) | NOT NULL. `{"tags":[...]}` |
| created_at | TIMESTAMP | 기본값 현재 시각 |

인덱스: `fp_posts(author_id)`, `fp_posts(created_at)`.

## H2와 Oracle의 차이

| 항목 | H2 | Oracle |
|---|---|---|
| meta 컬럼 | CLOB (JSON 타입은 문자열을 따옴표로 감싸 저장하는 함정이 있다) | JSON |
| 태그 조건 | 따옴표로 감싼 태그가 meta 문자열에 포함되는지 검색 | `JSON_EXISTS` |
| 스크립트 | `schema-h2.sql` (자동 실행) | `oracle/schema-oracle.sql` (직접 실행, 자동 실행 안 함) |
| 페이징 방언 | H2 | ORACLE |
