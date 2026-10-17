# ch13. Oracle 연결

## 학습 목표

- 기본 Profile(H2)과 oracle Profile을 분리한다.
- 접속 정보를 환경 변수로 주입한다.
- ojdbc11 드라이버로 Oracle에 연결하고 같은 Mapper XML이 두 데이터베이스에서 동작하는 것을 확인한다.
- Oracle에서 달라지는 점(null 바인딩, 빈 문자열, 식별 컬럼)을 확인한다.
- Oracle 테스트를 환경이 있을 때만 실행하도록 분리한다.

## 선수학습

ch02, ch09

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch13-oracle-connection`](../../../examples/ch13-oracle-connection) |
| API | `GET /db/info` |
| 테스트 | H2 기본 테스트와 Oracle 전용 테스트 (H2 4건, Oracle 6건) |
| 다루지 않는 것 | Oracle RAC, Wallet, 커넥션 풀 튜닝 |

## 다음 챕터

ch14. Oracle JSON
