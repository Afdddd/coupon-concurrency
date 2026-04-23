import http from 'k6/http';
import { check } from 'k6';

// 시나리오 1: 초과 발급 측정: 재고보다 많은 동시 요청이 몰릴 때 발급 건수가 1,000을 넘는지 확인
// 쿠폰 1,000개 / 유저 10,000명 / 1인 1요청
export const options = {
    scenarios: {
        oversell: {
            executor: 'per-vu-iterations',
            vus: 10000,
            iterations: 1,
            maxDuration: '1m'
        }
    }
};

export default function () {
    const userId = __VU;
    const couponId = 1;
    const res = http.post(`http://localhost:8080/coupon/${couponId}/issue?userId=${userId}`);

    check(res, {
        '200 OK': (r) => r.status === 200,
    });
}
