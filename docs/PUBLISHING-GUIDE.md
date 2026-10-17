# PUBLISHING-GUIDE

Claude Code가 만든 초안을 사용자가 검토하고, 승인된 챕터만 Agy가 블로그에 게시하는 절차.

## 역할

| 단계 | 담당 | 산출물 |
|---|---|---|
| 코드 구현, 빌드, 테스트 | Claude Code | `examples/chNN-*`, `verification.md` |
| MD 초안 | Claude Code | `docs/chapters/chNN/blog-draft.md` |
| 검토와 승인 | 사용자 | `CURRICULUM-STATUS.md`의 상태를 `REVIEWED`로 변경 |
| 게시 | Agy | 블로그 글, 게시 URL |

Claude Code는 블로그에 직접 게시하지 않는다. 승인되지 않은 챕터는 전달하지 않는다.

## 대상 블로그

- 카테고리 ID: **46** (https://jqdom.com/blog?categoryId=46)
- 언어: 한국어

## 상태 전이

```text
PLANNED -> IMPLEMENTING -> CODE_READY -> VERIFIED -> DRAFT_READY -> REVIEWED -> PUBLISHED
```

`VERIFIED` 전에는 초안을 쓰지 않는다. 코드가 바뀌면 `CODE_READY`로 되돌린다.

## Agy 인계 패키지

사용자가 승인(`REVIEWED`)한 챕터마다 아래 항목을 전달한다.

| 항목 | 위치 |
|---|---|
| Chapter ID | 예: `ch01` |
| 승인된 초안 | `docs/chapters/chNN/blog-draft.md` |
| GitHub 예제 URL | 초안의 "GitHub 예제 링크" 절 |
| 검증된 Commit SHA | `verification.md`, `CURRICULUM-STATUS.md` |
| 빌드/테스트 결과 | `verification.md` |
| 블로그 카테고리 ID | 46 |
| 이전/다음 챕터 | `CURRICULUM.md`의 챕터 표 |

## Agy 준수 사항

1. 초안의 기술 내용(코드, 명령, 출력, 버전)을 바꾸지 않는다. 오탈자와 서식 보정만 허용한다.
2. 기술 내용 수정이 필요하면 게시를 멈추고 Claude Code에 되돌려 보낸다.
3. 코드 링크는 초안의 링크를 그대로 쓴다. `main` 링크가 열리지 않으면 게시하지 않는다.
4. 게시 후 URL을 `CURRICULUM-STATUS.md`에 기록하고 상태를 `PUBLISHED`로 바꾼다.
5. 이전/다음 챕터 링크는 해당 글이 게시된 뒤에 연결한다.

## 링크 정책

- 디렉터리: `https://github.com/devguna81/spring-boot-curriculum/tree/main/examples/{경로}`
- 파일: `https://github.com/devguna81/spring-boot-curriculum/blob/main/{경로}`
- 존재하지 않는 파일, `main`에 반영되지 않은 코드의 `main` 링크는 쓰지 않는다.
- 글이 가리키는 버전을 고정하기 위해 검증된 Commit SHA를 글 하단에 기록한다.
  SHA로 고정한 링크는 `blob/{SHA}/{경로}` 형식이다.

## 인계 현황

### ch01 (REVIEWED, Agy 전달 대기)

| 항목 | 값 |
|---|---|
| Chapter ID | `ch01` |
| 제목 | Spring Boot 4 시작하기 (1): 프로젝트 만들고 첫 API 실행하기 |
| 승인된 초안 | `docs/chapters/ch01/blog-draft.md` (맨 위 HTML 주석은 검토자 메모이므로 게시에서 제외) |
| GitHub 예제 URL | https://github.com/devguna81/spring-boot-curriculum/tree/main/examples/ch01-hello-spring |
| 검증된 Commit SHA | `61a3bb118238a2f9e8632802c81844f48e9018cc` (예제 코드), `main` 머지 커밋 `fabbb471c311661f17a180eed2cae656f9910c6d` |
| 빌드/테스트 결과 | 로컬 Docker와 GitHub Actions 모두 `clean build` 성공, 테스트 4/4 통과. 상세는 `docs/chapters/ch01/verification.md` |
| 블로그 카테고리 ID | 46 |
| 이전 챕터 | 없음 (시리즈 첫 글) |
| 다음 챕터 | ch02. 설정과 Profile (미작성, 게시 전이므로 링크 없음) |
| 미검증 | Windows(`gradlew.bat`) 실행 |

게시 후 Agy가 게시 URL을 `docs/CURRICULUM-STATUS.md`의 ch01 항목에 기록하고 상태를 `PUBLISHED`로 바꾼다.
