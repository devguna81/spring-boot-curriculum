# ch33. GitHub Actions와 CI/CD

워크플로의 구성 요소(트리거, 잡, 스텝, 권한)를 읽고 쓸 수 있다.
설명은 [docs/chapters/ch33](../../docs/chapters/ch33/README.md)에 있다.

## 환경

- POSIX 셸(`sh`)과 `curl`
- 워크플로는 저장소의 [`.github/workflows/build.yml`](../../.github/workflows/build.yml)과 [`image.yml`](../../.github/workflows/image.yml)에 있다

## 범위

| 항목 | 내용 |
|---|---|
| `build.yml` | `build`(전체 Gradle 빌드와 테스트), `oracle`(Oracle 컨테이너 테스트), `frontend`(React 두 프로젝트 행렬) |
| `image.yml` | ch32 이미지 빌드 -> 컨테이너 실행 -> 스모크 테스트 -> 이미지 파일 업로드 |
| 다루지 않는 것 | 레지스트리 푸시, 환경별 배포, GitHub Environments의 승인 규칙, 셀프 호스티드 러너 |

## 구조

```text
ch33-cicd/
└── smoke-test.sh
```

## 스모크 테스트

```bash
examples/ch33-cicd/smoke-test.sh http://localhost:8080 90
```
