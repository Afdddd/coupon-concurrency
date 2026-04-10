# 선착순 쿠폰 발급 시스템

대량 동시 요청 환경에서 동시성 문제를 단계별로 분석하고 개선하는 프로젝트

## 기술 스택

- Kotlin + Spring Boot
- MySQL (InnoDB)
- k6 (부하 테스트)

## 핵심 API

```
POST /coupons/{couponId}/issue
```

- 총량 제한: 1,000장
- 1인 1매

## Phase

### Phase 1 — 순진한 버전
- 가장 단순하게 구현
- k6로 동시 1만 요청 → 초과 발급, 중복 발급 확인

### Phase 2 — DB 레벨 동시성 제어
- Schedule 관점 분석 (serial / nonserial / conflict serializable)
- 격리 수준별 비교 (READ_COMMITTED → REPEATABLE_READ → SERIALIZABLE)
- 비관적 락(2PL) / 낙관적 락
- 유니크 제약조건, 인덱스 튜닝, 커넥션 풀 튜닝
- 데드락 재현 / 해결
- 각 단계마다 동일 조건 부하테스트 → TPS, 응답시간, 에러율 비교

### Phase 3~ — 확장
- Redis, Kafka 등 (Phase 2 완료 후 기획)

## 기록 포맷

각 단계마다 아래 형식으로 정리:

```
문제 → 원인 → 개선 → 수치(before/after)
```
