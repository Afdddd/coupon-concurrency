import http from 'k6/http';
import { check } from 'k6';

// 실험군: 50 VU가 전부 같은 상품(1번) 주문
// → 같은 row 비관적 락 경합. 결제 sleep 동안 락이 직렬화돼야 정상.
export const options = {
  vus: 50,
  duration: '30s',
};

const BASE = 'http://localhost:8080';

export default function () {
  const body = JSON.stringify({ items: [{ productId: 1, quantity: 1 }] });
  const res = http.post(`${BASE}/orders?userId=${__VU}`, body, {
    headers: { 'Content-Type': 'application/json' },
  });
  // 200 아니면 재고소진/404 등 오염 신호 → 에러율 올라가면 데이터 버려라
  check(res, { 'status 200': (r) => r.status === 200 });
}
