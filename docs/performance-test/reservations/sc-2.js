import http from 'k6/http';
import {sleep} from "k6"

// Base URL 설정
var BASE_URL = `http://${__ENV.TARGET_HOST}:8080/`

const TARGET_TPS = 5; // 목표 TPS 설정
const MAX_SEAT_ID = 150; // seatId의 최대값 설정

// 시나리오 설정
export let options = {
    scenarios: {
        burst_test: {
            executor: 'constant-vus',
            vus: TARGET_TPS, // 동시에 요청을 보낼 VU 수 설정
            duration: '5s', // 지속 시간 (한번의 집중 burst를 위해 짧은 시간 설정)
        }
    },

    thresholds:
        {
            http_req_duration: ['p(95)<100'],
        }
}

function getUri() {
    return "reservations";
}

// 메인 함수
export default function () {
    let url = BASE_URL + getUri()

    // Authorization 헤더 설정
    let headers = {
        'Content-Type': 'application/json',
        "Wait-Token": `1`,
    };

    // 각 VU별로 seatId를 균등 분배하여 순환할 수 있도록 설정
    let seatId = Math.floor(Math.random() * MAX_SEAT_ID) + 1;

    let body = {
        userId: 1,
        seatId: seatId
    }

    http.post(url, JSON.stringify(body), {headers});
    sleep(1);
};
