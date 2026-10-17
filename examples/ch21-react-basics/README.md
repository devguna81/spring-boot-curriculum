# ch21. React 기초

Vite로 React 개발 서버와 프로덕션 빌드를 실행한다.
설명은 [docs/chapters/ch21](../../docs/chapters/ch21/README.md)에 있다.

## 환경

- Node.js 24 (npm)

## 범위

| 항목 | 내용 |
|---|---|
| 화면 | `/users` 목록(활성 필터, 이름 검색), `/users/:id` 상세, 찾을 수 없음 |
| 테스트 | 목록 렌더링, 필터와 검색, 이스케이프, 문서 제목, 라우팅 (12건) |
| 다루지 않는 것 | 서버 호출(ch22), TypeScript, 상태 관리 라이브러리, 서버 사이드 렌더링 |

## 구조

```text
ch21-react-basics/
├── index.html
├── package-lock.json
├── package.json
├── src/
│   ├── App.jsx
│   ├── App.test.jsx
│   ├── components/
│   │   ├── UserRow.jsx
│   │   └── UserTable.jsx
│   ├── data/
│   │   └── users.js
│   ├── main.jsx
│   ├── pages/
│   │   ├── NotFoundPage.jsx
│   │   ├── UserDetailPage.jsx
│   │   └── UserListPage.jsx
│   ├── styles.css
│   └── test/
│       └── setup.js
└── vite.config.js
```

## 실행과 테스트 (이 폴더에서)

```bash
npm ci
npm run dev    # 개발 서버
npm test
```
