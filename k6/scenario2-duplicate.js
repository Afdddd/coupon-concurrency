import http from 'k6/http';
import { check } from 'k6';

// 시나리오 2: 중복 발급 (1인 1매 race condition)
// 쿠폰 10,000개 / 유저 100명 / 1인 10요청
export const options = {
    vus: 100,
    iterations: 1000,
};

export default function () {
    const userId = __VU;
    const couponId = 1;
    const res = http.post(`http://localhost:8080/coupon/${couponId}/issue?userId=${userId}`);

    check(res, {
        '200 OK': (r) => r.status === 200,
    });
}
