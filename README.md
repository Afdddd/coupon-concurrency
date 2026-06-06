# 선착순 쿠폰 발급 시스템

대량 동시 요청 환경에서 동시성 문제를 단계별로 분석하고 개선하는 프로젝트

## 정리
- [선착순 1,000장 쿠폰이 1,967장 발급된 이유](https://inyeop.notion.site/1-000-1-967-3550c52c121980d7937dfcd3f5e8b5c4)

## 기술 스택

- Kotlin + Spring Boot
- MySQL (InnoDB)
- k6 (부하 테스트)

## 핵심 API

```
POST /coupons/{couponId}/issue
```

## Scenario

### Scenario1 : 초과 발급
- 쿠폰 수량: 1,000개
- 동시 유저: 10,000명
- 요청: 1인 1요청 (총 10,000회)

### Scenario2 : 중복 발급
- 쿠폰 수량: 10,000개 (수량 부족이 아닌 중복만 테스트)
- 동시 유저: 100명
- 요청: 1인 10요청 (총 1,000회)

## Phase

### Phase 1 
- 단순하게 구현
- k6로 동시 1만 요청 → 초과 발급, 중복 발급 확인

### Phase 2 — DB 레벨 동시성 제어
- Schedule 관점 분석 (serial / nonserial / conflict serializable)
- 격리 수준별 비교 (READ_COMMITTED → REPEATABLE_READ → SERIALIZABLE)
- 비관적 락(2PL) / 낙관적 락
- 데드락 재현 / 해결
- 각 단계마다 동일 조건 부하테스트 → TPS, 응답시간, 에러율 비교

### Phase 3~ — 확장
