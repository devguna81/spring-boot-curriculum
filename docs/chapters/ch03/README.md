# ch03. IoC, DI, Bean

## 학습 목표

- IoC와 DI가 무엇이고 왜 쓰는지 설명한다.
- `@Component`, `@Service`, `@Configuration`/`@Bean`으로 Bean을 등록한다.
- 같은 타입의 Bean이 여러 개일 때 `@Primary`, `@Qualifier`, `List` 주입으로 고른다.
- 컴포넌트 스캔의 범위와 Bean 이름 규칙을 이해한다.
- Bean의 Scope(singleton, prototype)와 생명주기(`@PostConstruct`, `@PreDestroy`)를 확인한다.

## 선수학습

ch01. Hello Spring Boot

## 범위

| 항목 | 내용 |
|---|---|
| 예제 | [`examples/ch03-ioc-di-bean`](../../../examples/ch03-ioc-di-bean) |
| API | `GET /orders`, `GET /senders`, `GET /beans` |
| 테스트 | 생성자 주입 단위 테스트, Bean 연결, Scope와 생명주기 (12건) |
| 다루지 않는 것 | Bean 후처리기, `@Conditional`, AOP |

## 다음 챕터

ch04. HTTP와 REST, Controller
