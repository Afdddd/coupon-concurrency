# coupon-concurrency

> 기능을 하나씩 확장하며 마주친 문제를 직접 재현하고, 실무에서 쓰는 기술로 해결하는 과정을 기록한 프로젝트

선착순 쿠폰 발급에서 시작해 주문·재고·결제로 도메인을 넓히며,
각 단계에서 터지는 동시성·정합성 문제를 재현하고 해결한다.


## 트러블슈팅

**주문 · 재고**
- [동시 주문 시 재고 정합성 깨짐과 비관적 락](docs/troubleshooting/동시-주문-재고-정합성-비관적-락.md)
  동시 주문에서 lost update와 데드락이 발생 → `SELECT FOR UPDATE`로 해결
- [비관적 락 + 외부 결제 호출이 만든 처리량 병목과 트랜잭션 분리](docs/troubleshooting/결제-호출-트랜잭션-분리.md)
  결제가 트랜잭션 안에 있어 락·커넥션을 오래 점유 → 외부 호출을 트랜잭션 밖으로 분리, TPS 약 1 → 48

**쿠폰 발급** (`archive/concurrency` 브랜치)
- [선착순 1,000장 쿠폰이 1,967장 발급된 이유](https://inyeop.notion.site/1-000-1-967-3550c52c121980d7937dfcd3f5e8b5c4)
  초과·중복 발급 재현 → 락 전략 비교, 데드락 해결

## 기술 스택

- Kotlin, Spring Boot, JPA
- MySQL (InnoDB)
- Testcontainers (통합 테스트), k6 (부하 테스트)

## 실행 방법

```bash
docker-compose up -d   # MySQL 컨테이너 기동
./gradlew bootRun
```
