import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data'
import {vu} from 'k6/execution';

import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';

const BASE_URL = 'http://localhost:8080';
const MAX_USER_ID = 20; // Max user ID 20,000
const MAX_ITERATIONS = 5; // Each VU will poll for 5 iterations
// Global variables for sequential user IDs

let used = {}

const users = new SharedArray("some name", function () {
    let data = papaparse.parse(open('./users-l.csv'), {header: true}).data;
    return data.map(user => ({
        userId: user.userId,      // 사용자 ID
        seatId: null,             // 예약된 좌석 ID (초기값은 null)
        waitToken: null,           // 대기열 토큰 (초기값은 null)
        reservationId: null
    }));
});

export const options = {
    scenarios: {
        registerWaitQueueAndWaitForActivation: {
            executor: 'per-vu-iterations',
            vus: users.length,
            iterations: 1,
            maxDuration: '5m',
            exec: 'registerWaitQueueAndWaitForActivation'
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<5000'], // 95% of requests should be below 500ms
    },
};

export function registerWaitQueueAndWaitForActivation() {
    console.log('VU: ' + vu.idInTest + ' / username: ', users[vu.idInTest - 1].userId);
    const userId = users[vu.idInTest - 1].userId
    const payload = JSON.stringify({userId});
    const headers = {'Content-Type': 'application/json'};

    // Skip if the user has already been processed
    if (used?.userId === true) return

    const registerRes = http.post(`${BASE_URL}/queue`, payload, {headers});
    check(registerRes, {'status 201 on register': (r) => r.status === 201});
    console.log(`registering... ${userId}`)
    used[userId] = true;


    if (registerRes.status === 201) {
        const waitToken = registerRes.headers['Wait-Token'];
        headers["Wait-Token"] = waitToken;

        for (let i = 0; i < MAX_ITERATIONS; i++) {

            // Poll the queue status for a set number of iterations
            const statusRes = http.get(`${BASE_URL}/queue`, {headers});
            check(statusRes, {'status 200 on queue check': (r) => r.status === 200});

            const responseBody = statusRes.json();
            console.log(`Queue status for userId ${userId}: ${responseBody.status}`);
            sleep(2);
            if (responseBody.status === 'ACTIVE') {

                const seatId = Math.random() < 0.8
                    ? Math.floor(Math.random() * 4000) + 1  // 인기 좌석 (1~4,000)
                    : Math.floor(Math.random() * 16000) + 4001; // 일반 좌석 (4,001~20,000)

                const reservationPayload = JSON.stringify({
                    concertId: 1,
                    performanceDateId: Math.floor(Math.random() * 20) + 1,
                    seatId,
                    userId,
                });


                const reserveRes = http.post(`${BASE_URL}/reservations`, reservationPayload, {headers});
                check(reserveRes, {
                    'status 201 on reservation': (r) =>
                        r.status === 201
                });
                if (reserveRes.status === 201) {
                    const reservationId = reserveRes.json().reservationId

                    // Sleep to simulate user think time before next status check
                    const paymentRes = http.post(`${BASE_URL}/users/${userId}/reservations/${reservationId}/pay`, {headers});
                    check(paymentRes, {'status 200 on payment': (r) => r.status === 200});
                    console.log(`Payment Request for ${userId}: ${paymentRes.status}`);
                    break;
                }
            }
            sleep(4);
        }
    }
}
