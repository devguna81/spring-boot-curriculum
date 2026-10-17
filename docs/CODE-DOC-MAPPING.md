# CODE-DOC-MAPPING

챕터별 예제 코드, 문서, 블로그 글의 대응표. 챕터가 추가될 때마다 행을 늘린다.
링크는 `main`에 실제로 반영된 경로만 적는다.

| 챕터 | 예제 코드 | 챕터 문서 | 블로그 초안 | 검증 기록 |
|---|---|---|---|---|
| ch01 | [examples/ch01-hello-spring](../examples/ch01-hello-spring) | [README](chapters/ch01/README.md) | [blog-draft.md](chapters/ch01/blog-draft.md) | [verification.md](chapters/ch01/verification.md) |
| ch02~ch32 | 예정 | | | |
| f01~f04 | `projects/final-project` 예정 | | | |

## 규칙

- 예제 디렉터리 이름은 `chNN-<영문-주제>`로 짓는다. 문서 디렉터리는 `docs/chapters/chNN`이다.
- 글에 인용한 코드는 예제 파일을 그대로 가져온 것이어야 한다. 글을 위해 코드를 따로 고치지 않는다.
- 예제 코드가 바뀌면 같은 PR에서 블로그 초안과 `verification.md`를 갱신하고, 상태를 `CODE_READY`로 되돌린다.
