# ch33. GitHub Actions와 CI/CD

## 학습 목표

- 워크플로의 구성 요소(트리거, 잡, 스텝, 권한)를 읽고 쓸 수 있다.
- 잡을 나누고(`build`, `oracle`, `frontend`, `image`), 행렬(matrix)로 같은 잡을 여러 프로젝트에 돌린다.
- 캐시(Gradle, npm, Docker 층)와 아티팩트(테스트 리포트, 이미지 파일)를 쓴다.
- 이미지 관련 파일이 바뀔 때만 이미지 잡이 돌도록 `paths`를 제한한다.
- 컨테이너를 실제로 띄워 스모크 테스트로 확인한 뒤에야 성공으로 본다.
- CI 성공 표시만 믿지 않고, 로그에서 테스트 건수를 직접 센다.

## 선수학습

ch32

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch33-cicd`](../../../examples/ch33-cicd) (`smoke-test.sh`), 워크플로는 [`.github/workflows/build.yml`](../../../.github/workflows/build.yml)과 [`image.yml`](../../../.github/workflows/image.yml) |
| `build.yml` | `build`(전체 Gradle 빌드와 테스트), `oracle`(Oracle 컨테이너 테스트), `frontend`(React 두 프로젝트 행렬) |
| `image.yml` | ch32 이미지 빌드 -> 컨테이너 실행 -> 스모크 테스트 -> 이미지 파일 업로드 |
| 다루지 않는 것 | 레지스트리 푸시, 환경별 배포, GitHub Environments의 승인 규칙, 셀프 호스티드 러너 |

## 워크플로에서 눈여겨볼 설정

| 설정 | 이유 |
|---|---|
| `permissions: contents: read` | 토큰에 필요한 최소 권한만 준다 |
| `concurrency` + `cancel-in-progress` | 같은 브랜치에 새 푸시가 오면 이전 실행을 취소한다. `main`은 취소하지 않는다 |
| `timeout-minutes` | 멈춘 잡이 러너를 오래 붙잡지 않게 한다 |
| `paths` (image.yml) | 이미지와 무관한 변경(문서, 다른 챕터)에서는 이미지 잡을 건너뛴다 |
| `push: false`, `load: true` | 이미지를 레지스트리에 올리지 않고 러너의 Docker에서 바로 실행해 본다 |
| `cache-from/to: type=gha` | Docker 층 캐시를 GitHub Actions 캐시에 저장한다 |
| `if: failure()` 로그 출력 | 스모크 테스트가 실패하면 컨테이너 로그를 남긴다 |

## 스모크 테스트

`examples/ch33-cicd/smoke-test.sh`는 CI와 로컬에서 같은 스크립트를 씁니다. readiness가 `UP`이 될 때까지 기다린 뒤, `/hello` 본문과 `/visits`가 호출마다 1씩 늘어나는 것(데이터베이스를 거치는 경로)을 확인하고, 하나라도 어긋나면 비정상 종료합니다. 응답 코드 200만 보지 않고 본문까지 확인하는 이유는 오류 페이지도 200일 수 있기 때문입니다.

```bash
examples/ch33-cicd/smoke-test.sh http://localhost:8080 60
```

## 확인하지 않은 것

- 비밀 값(secrets)과 환경(Environments)을 쓰는 배포 잡은 만들지 않았다. 개념과 설정 위치는 공식 문서를 참고한다.
- 포크에서 온 PR의 권한 제한이 이 워크플로에 미치는 영향은 확인하지 않았다.

## 다음 단계

Part 08. 종합 프로젝트 (f01~f04)
