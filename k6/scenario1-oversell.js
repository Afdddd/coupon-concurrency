import http from 'k6/http';
import { check } from 'k6';

// 시나리오 1: 초과 발급 (lost update)
// 쿠폰 1,000개 / 유저 10,000명 / 1인 1요청
export const options = {
    vus: 10000,
    iterations: 10000,
};

export default function () {
    const userId = __VU;
    const couponId = 1;
    const res = http.post(`http://localhost:8080/coupon/${couponId}/issue?userId=${userId}`);

    check(res, {
        '200 OK': (r) => r.status === 200,
    });
}
