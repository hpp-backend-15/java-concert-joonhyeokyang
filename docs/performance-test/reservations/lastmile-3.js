import http from 'k6/http';
import {check, sleep} from 'k6';
import {SharedArray} from 'k6/data'
import {vu} from 'k6/execution';

import papaparse from 'https://jslib.k6.io/papaparse/5.1.1/index.js';

const BASE_URL = 'http://localhost:8080';
const MAX_USER_ID = 20; // Max user ID 20,000
const MAX_ITERATIONS = 5; // Each VU will poll for 5 iterations
// Global variables for sequential user IDs

const users = new SharedArray("some name", function () {
    let data = papaparse.parse(open('./users-s.csv'), {header: true}).data;
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
            exec: 'registerWaitQueueAndWaitForActivation'
        },
        reservation: {
            executor: 'per-vu-iterations',
            vus: 1,
            iterations: 2,
            exec: 'reservation',
            startTime: '15s'

        },
        payment: {
            executor: 'per-vu-iterations',
            vus: 1,
            iterations: 1,
            exec: 'payment',
            startTime: '20s'
        }
    },
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
    },
};

export function registerWaitQueueAndWaitForActivation() {
    console.log('VU: ' + vu.idInTest + ' / username: ', users[vu.idInTest - 1].userId);
    const userId = users[vu.idInTest - 1].userId
    const payload = JSON.stringify({userId});
    const headers = {'Content-Type': 'application/json'};

    // Skip if the user has already been processed

    const registerRes = http.post(`${BASE_URL}/queue`, payload, {headers});
    check(registerRes, {'status 201 on register': (r) => r.status === 201});
    console.log(`registering... ${userId}`)


    if (registerRes.status === 201) {
        const waitToken = registerRes.headers['Wait-Token'];
        headers["Wait-Token"] = waitToken;
        users[vu.idInTest - 1].waitToken =  waitToken;

        for (let i = 0; i < MAX_ITERATIONS; i++) {

            // Poll the queue status for a set number of iterations
            const statusRes = http.get(`${BASE_URL}/queue`, {headers});
            check(statusRes, {'status 200 on queue check': (r) => r.status === 200});

            if (statusRes.status === 200) {
                const responseBody = statusRes.json();
                console.log(`Queue status for userId ${userId}: ${responseBody.status}`);
            }

            // Sleep to simulate user think time before next status check
            sleep(2);
        }
    }
}

export function reservation() {
    const userIndex = vu.idInTest - 1;
    const user = users[userIndex];
    const userId = user.userId;
    const headers = {'Content-Type': 'application/json'};
    console.log(`reservation userId here ${userId}`)
    // 대기열 토큰을 가져오지 않았다면 결제 진행하지 않음
    if (!user.waitToken) {
        console.log(`Waiting for reservation to complete for userId ${userId}`);
        return;
    }
    const {waitToken} = user;

    headers["Wait-Token"] = waitToken; // 대기열 토큰을 헤더에 추가

    const seatId = Math.floor(Math.random() * 1000) + 1; // Random seat
    const reservationPayload = JSON.stringify({
        concertId: 1,
        performanceDateId: Math.floor(Math.random() * 20) + 1,
        seatId,
        userId,
    });

    users[vu.idInTest - 1].seatId = seatId;


    const reserveRes = http.post(`${BASE_URL}/reservations`, reservationPayload, {headers});
    check(reserveRes, {
        'status 201 on reservation': (r) =>
            r.status === 201
    });
    const reservationId = reserveRes.reservationId
    users[vu.idInTest - 1].reservationId = reservationId;

    console.log(`Reservation Trying... ${reserveRes}: ${reserveRes.status}`);
}

export function payment() {
    const userIndex = vu.idInTest - 1;
    const user = users[userIndex];
    const userId = user.userId;
    const headers = {'Content-Type': 'application/json'};
    console.log(`pay userId here ${userId}`)

    // 대기열 토큰을 가져오지 않았다면 결제 진행하지 않음
    if (!user.waitToken || !user.reservationId) {
        console.log(`Waiting for reservation to complete for userId ${userId}`);
        return;
    }

    const {seatId, waitToken, reservationId} = user;

    headers["Wait-Token"] = waitToken; // 대기열 토큰을 헤더에 추가


    const paymentRes = http.post(`${BASE_URL}/users/${userId}/reservations/${reservationId}/pay`, {headers});
    check(paymentRes, {'status 201 on payment': (r) => r.status === 201});
    console.log(`Payment Request for ${userId}: ${paymentRes.status}`);

    // 결제 TPS 제한을 위한 sleep
    sleep(0.5); // 2 TPS로 설정하면 0.5초마다 결제 요청
}
