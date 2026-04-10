import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1000,          // 동시 가상 유저 1000명
    iterations: 10000   // 총 1만 번 요청
};

export default function () {
    const userId = __ITER * 1000 + __VU;
    const couponId = 1;
    const res = http.post(`http://localhost:8080/coupon/${couponId}/issue?userId=${userId}`);

    check(res, {
        '200 OK': (r) => r.status === 200,
    });
}

