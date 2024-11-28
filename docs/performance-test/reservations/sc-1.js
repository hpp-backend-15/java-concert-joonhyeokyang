/**
 * 한_유저가_한_좌석을_동시에_300번_예약시도한다_그러나_예약한사람은_오직한명이다
 */

import http from 'k6/http';
import {sleep} from "k6"

var BASE_URL = `http://${__ENV.TARGET_HOST}:8080/`

const TARGET_TPS = 3; // 목표 TPS 설정

export let options = {
    scenarios: {
        burst_test: {
            executor: 'constant-vus',
            vus: TARGET_TPS, // 동시에 요청을 보낼 VU 수 설정
            duration: '1s', // 지속 시간 (한번의 집중 burst를 위해 짧은 시간 설정)
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

export default function () {
    let url = BASE_URL + getUri()

    //set authorization Bearer token
    let headers = {
        'Content-Type': 'application/json',
        "Wait-Token": `1`,
    };

    let body = {
        userId: 1,
        seatId: 1
    }

    http.post(url, JSON.stringify(body), {headers});
    sleep(1);
};