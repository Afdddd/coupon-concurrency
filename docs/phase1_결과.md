# Phase 1 결과 (요약 인덱스)

> **Source of truth → Notion 글**: [선착순 쿠폰 1,000장 발급에 1,967건 발급된 사건](https://inyeop.notion.site/1-000-1-967-3550c52c121980d7937dfcd3f5e8b5c4)
>
> Phase 1 baseline + Phase 2 4해법(원자 UPDATE / SERIALIZABLE / 비관 / 낙관) 비교까지 한 문서에서 다룸. 이 파일은 핵심 수치만 빠르게 보기 위한 로컬 인덱스.

---

## 환경
- Kotlin + Spring Boot + MySQL 8.0 (InnoDB)
- 격리 수준: REPEATABLE_READ (MySQL 기본)
- 동시성 제어: 없음 (Phase 1)
- 부하 도구: k6 (`per-vu-iterations`로 1-per-VU 보장)

---

## 시나리오 1 — 초과 발급 (Lost Update)

조건: 쿠폰 1,000개 / 동시 유저 10,000명 / 1인 1요청

| 항목 | 기대값 | 실측값 |
|---|---|---|
| 발급 건수 (`coupon_issue`) | 1,000 | **1,967** |
| 잔량 (`coupon.count`) | 0 | 140 |
| Lost Update | 0 | **1,107건** |
| TPS | — | 654 |

**원인**: Read-Modify-Write 충돌. 여러 트랜잭션이 같은 `count`를 동시에 읽고 각자 -1 → 한쪽 차감을 다른 쪽이 덮어씀. RR(MySQL 기본)도 막지 못함.

```
TX1 READ count=500
TX2 READ count=500
TX1 UPDATE count=499
TX2 UPDATE count=499  ← TX1 차감 덮음
```

데드락도 동시 관찰됨 (FK-induced S→X 승급) — 분석은 `docs/socratic-learning.md`, 원시 로그는 `docs/materials/deadlock-phase1-scenario1.txt`.

---

## Phase 2 4해법 비교 (Notion 글 참조)

| 해법 | TPS | 소요시간 | 발급 정합성 | 데드락 |
|---|---|---|---|---|
| 락 없음 (baseline) | 654 | 15s | ✗ (1,967건) | O |
| 원자 UPDATE | 1,060 | 9.4s | ✓ | — |
| SERIALIZABLE | 629 | 15s | ✓ (584건만 통과) | O |
| **비관적 락** | **1,214** | **8.23s** | **✓** | **X** |
| 낙관적 락 (재시도 X) | 846 | 11.8s | ✓ (992건만 성공) | O |

**결론**: 정합성을 지키면서 TPS 가장 높은 **비관적 락**이 이 시나리오에서 우위.
**이론적 뒷받침**: 선착순은 같은 row에 충돌이 극단적으로 몰리는 시나리오 → "충돌 비용을 wait로 미리 내는" 비관락이 "retry로 사후에 내는" 낙관락보다 유리.

상세 수치(p95/avg, version 충돌 등)와 분석은 Notion 글 참조.

---

## 시나리오 2 — 중복 발급 (보류)

이론상 Check-Then-Act race로 동일 유저 중복 발급 가능.
**실측은 의도 보류**: 글이 길어져 제외. 해법 방향은 `userId` 유니크 + 검증 빼고 INSERT-only (SQL-level 원자화 + 유니크 제약).

---

## 한계 (다음 단계로 넘긴 항목)
- 낙관적 락 재시도 미구현 → TPS 재측정은 백로그
- SERIALIZABLE의 p95/avg 수치 일부 누락
- 비관락 메커니즘이 "X락 쥔다" 수준에서 멈춤 → **Phase 2 - B 단계(비관락 딥다이브)** 에서 락 범위/인덱스 의존성/FK 승급/대기 타임아웃 보강 예정
