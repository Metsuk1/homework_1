import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,
    duration: '30s',
};

export default function () {
    // /api/time
    let res = http.get('http://localhost:8081/api/time');
    check(res, { 'time endpoint 200': (r) => r.status === 200 });

    // /api/echo
    let echo = http.post('http://localhost:8081/api/echo', JSON.stringify({msg: "hello"}), {
        headers: { 'Content-Type': 'application/json' },
    });
    check(echo, { 'echo endpoint 200': (r) => r.status === 200 });

    sleep(1);
}
