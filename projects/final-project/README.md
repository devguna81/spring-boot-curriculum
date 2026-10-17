# 종합 프로젝트: 사용자·게시판 관리 시스템

지금까지 배운 기능을 하나의 프로젝트로 묶는다. 회원이 가입하고, 글을 쓰고, 검색하며, 본인 글(관리자는 모든 글)을 수정·삭제한다.

## 단계

| 단계 | 내용 | 설명 |
|---|---|---|
| f01 | 설계 | [docs/chapters/f01](../../docs/chapters/f01/README.md) |
| f02 | 백엔드 | [docs/chapters/f02](../../docs/chapters/f02/README.md) |
| f03 | 화면과 문서 | [docs/chapters/f03](../../docs/chapters/f03/README.md) |
| f04 | 품질과 배포 | [docs/chapters/f04](../../docs/chapters/f04/README.md) |

## 설계 문서

| 문서 | 내용 |
|---|---|
| [01-requirements.md](docs/01-requirements.md) | 요구사항과 범위 |
| [02-architecture.md](docs/02-architecture.md) | 모듈 구조와 의존 규칙 |
| [03-database.md](docs/03-database.md) | 테이블 설계, H2와 Oracle 차이 |
| [04-api-contract.md](docs/04-api-contract.md) | API 계약과 오류 형식 |
| [05-screens.md](docs/05-screens.md) | 화면 구성과 화면 규칙 |

## 실행

```bash
./gradlew :projects:final-project:test
./gradlew :projects:final-project:bootRun
```

데모 계정(`admin`, `alice`, `bob`)과 글을 함께 만들려면 비밀번호를 직접 정해 환경 변수로 넘긴다. 코드에 기본 비밀번호는 없다.

```bash
SEED_DEMO_DATA=true DEMO_PASSWORD=원하는값8자이상 ./gradlew :projects:final-project:bootRun
```

`SEED_DEMO_DATA=true`인데 `DEMO_PASSWORD`가 없거나 8자 미만이면 시작하지 않는다. 데모 데이터 없이 실행하면 `/app/`의 가입 화면으로 계정을 만든다.

| 주소 | 내용 |
|---|---|
| `/pages/posts` | 서버 렌더링 글 목록 |
| `/swagger-ui.html` | API 문서 |
| `/app/` | React 화면(Docker 이미지에는 들어 있고, `bootRun`에는 빌드 결과를 `static/app`에 넣어야 한다) |
| `/actuator/health` | 상태 확인(세부 항목은 관리자만) |

### React 화면 개발

```bash
cd projects/final-project/frontend
npm ci
npm test
npm run dev        # http://localhost:5173, /api 는 8080으로 전달
npm run build      # dist/ 를 만든다
```

### Docker

```bash
docker build -f projects/final-project/Dockerfile -t final-project .
docker run --rm -p 8080:8080 -e SEED_DEMO_DATA=true -e DEMO_PASSWORD=원하는값8자이상 final-project

# 또는 compose
DEMO_PASSWORD=원하는값8자이상 SEED_DEMO_DATA=true docker compose -f projects/final-project/docker-compose.yml up --build

# 떠 있는 서버 점검
projects/final-project/smoke-test.sh http://localhost:8080
```

이미지는 세 단계로 만든다: React 빌드, 서버 JAR 빌드(React 결과를 `static/app`에 넣음), JRE만 있는 실행 이미지(root가 아닌 사용자).

## 인증

HTTP Basic, 세션 없음. 비밀번호는 bcrypt 해시로 저장한다. 읽기(`GET /api/v1/posts`)와 가입은 누구나, 쓰기는 로그인한 회원, `/api/v1/admin/**`와 운영 지표는 관리자만 할 수 있다. 규칙에 없는 경로는 기본적으로 로그인이 필요하다.

Oracle로 실행하려면 `oracle` 프로파일과 접속 환경 변수(`ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD`)가 필요하다. 테이블은 `src/main/resources/oracle/schema-oracle.sql`로 먼저 만든다.
