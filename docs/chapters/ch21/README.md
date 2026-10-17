# ch21. React 기초

## 학습 목표

- Vite로 React 개발 서버와 프로덕션 빌드를 실행한다.
- 컴포넌트와 props로 화면을 나누고, 목록은 `key`를 붙여 그린다.
- `useState`, `useMemo`, `useEffect`의 역할을 구분해 사용한다.
- React Router로 목록, 상세, 찾을 수 없음 화면을 연결하고 `useParams`로 경로 값을 읽는다.
- Vitest와 Testing Library로 사용자가 보는 결과를 테스트한다.

## 선수학습

JavaScript 기본(변수, 함수, 배열 메서드, 모듈)

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch21-react-basics`](../../../examples/ch21-react-basics) |
| 화면 | `/users` 목록(활성 필터, 이름 검색), `/users/:id` 상세, 찾을 수 없음 |
| 테스트 | 목록 렌더링, 필터와 검색, 이스케이프, 문서 제목, 라우팅 (12건) |
| 다루지 않는 것 | 서버 호출(ch22), TypeScript, 상태 관리 라이브러리, 서버 사이드 렌더링 |

## 실행

Node 22.12 이상이 필요합니다(예제는 Node 24에서 확인).

```bash
cd examples/ch21-react-basics
npm ci
npm run dev      # http://localhost:5173
npm test
npm run build
```

## 다음 챕터

ch22. React와 REST API 연동
