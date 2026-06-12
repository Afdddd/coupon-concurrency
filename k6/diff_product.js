import http from 'k6/http';
import { check } from 'k6';

// 대조군: VU마다 다른 상품(__VU번, 1~50) 주문
// → 락 경합 없음. 여기서 느려지면 원인은 커넥션 풀(기본 10) 점유다.
export const options = {
  vus: 50,
  duration: '30s',
};

const BASE = 'http://localhost:8080';

export default function () {
  const body = JSON.stringify({ items: [{ productId: __VU, quantity: 1 }] });
  const res = http.post(`${BASE}/orders?userId=${__VU}`, body, {
    headers: { 'Content-Type': 'application/json' },
  });
  check(res, { 'status 200': (r) => r.status === 200 });
}
