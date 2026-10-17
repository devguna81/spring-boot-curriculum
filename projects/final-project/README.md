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

실행하면 데모 계정(`admin`, `alice`, `bob`)과 글이 들어 있다. 끄려면 `SEED_DEMO_DATA=false`.

| 주소 | 내용 |
|---|---|
| `/pages/posts` | 서버 렌더링 글 목록 |
| `/swagger-ui.html` | API 문서 |
| `/app/` | React 화면(빌드해서 넣었을 때) |

### React 화면 개발

```bash
cd projects/final-project/frontend
npm ci
npm test
npm run dev        # http://localhost:5173, /api 는 8080으로 전달
npm run build      # dist/ 를 만든다
```

배포 이미지에서는 `dist/`를 `static/app/`에 넣는다(f04).

Oracle로 실행하려면 `oracle` 프로파일과 접속 환경 변수(`ORACLE_URL`, `ORACLE_USERNAME`, `ORACLE_PASSWORD`)가 필요하다. 테이블은 `src/main/resources/oracle/schema-oracle.sql`로 먼저 만든다.
