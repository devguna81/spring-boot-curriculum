// 서버 없이 화면만 확인하려는 고정 데이터. ch22 에서 REST API 호출로 바꾼다.
// ch19 의 Thymeleaf 예제와 같은 데이터이고, 이름에 태그가 들어 있는 사용자가 포함돼 있다.
export const users = [
  { id: 1, name: 'kim', email: 'kim@example.com', age: 28, active: true },
  { id: 2, name: 'lee', email: 'lee@example.com', age: 35, active: true },
  { id: 3, name: 'park', email: 'park@example.com', age: 41, active: false },
  { id: 4, name: '<b>choi</b>', email: 'choi@example.com', age: 23, active: true },
]
