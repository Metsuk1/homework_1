import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10, // 10 virtual users
    duration: '30s', // Run for 30 seconds
    thresholds: {
        http_req_failed: ['rate<0.01'], // Error rate < 1%
        http_req_duration: ['p(95)<500'], // 95% of requests < 500ms
        checks: ['rate>0.95'], // 95% of checks should pass
    },
};

const BASE_URL = 'http://localhost:8080';
const API_URL = `${BASE_URL}/api/v1/users`;

export default function () {
    // Test GET / (index.html) - Static resource
    let indexRes = http.get(BASE_URL);
    check(indexRes, {
        'GET / status is 200': (r) => r.status === 200,
        'GET / contains <html>': (r) => r.body.includes('<html'),
    });

    // Test POST /api/v1/users (create user)
    let createPayload = JSON.stringify({
        name: `User${__VU}_${__ITER}`, // Unique name per VU and iteration
        email: `user${__VU}_${__ITER}@example.com`,
    });
    let createRes = http.post(API_URL, createPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    check(createRes, {
        'POST /api/v1/users status is 200': (r) => r.status === 200,
        'POST /api/v1/users returns user ID': (r) => {
            let body = JSON.parse(r.body);
            return body.id !== undefined && body.id !== null;
        },
    });

    // Extract user ID from create response
    let userId = JSON.parse(createRes.body).id;

    // Test GET /api/v1/users (get all users)
    let getAllRes = http.get(API_URL);
    check(getAllRes, {
        'GET /api/v1/users status is 200': (r) => r.status === 200,
        'GET /api/v1/users returns array': (r) => Array.isArray(JSON.parse(r.body)),
    });

    // Test GET /api/v1/users/{id} (get user by ID)
    if (userId) {
        let getUserRes = http.get(`${API_URL}/${userId}`);
        check(getUserRes, {
            'GET /api/v1/users/{id} status is 200': (r) => r.status === 200,
            'GET /api/v1/users/{id} returns correct user': (r) => {
                let body = JSON.parse(r.body);
                return body.id === userId;
            },
        });

        // Test PUT /api/v1/users/{id} (update user)
        let updatePayload = JSON.stringify({
            name: `UpdatedUser${__VU}_${__ITER}`,
            email: `updated${__VU}_${__ITER}@example.com`,
        });
        let updateRes = http.put(`${API_URL}/${userId}`, updatePayload, {
            headers: { 'Content-Type': 'application/json' },
        });
        check(updateRes, {
            'PUT /api/v1/users/{id} status is 200': (r) => r.status === 200,
            'PUT /api/v1/users/{id} updates name': (r) => {
                let body = JSON.parse(r.body);
                return body.name === `UpdatedUser${__VU}_${__ITER}`;
            },
        });

        // Test PATCH /api/v1/users/{id} (partial update)
        let patchPayload = JSON.stringify({
            name: `PatchedUser${__VU}_${__ITER}`,
        });
        let patchRes = http.request('PATCH', `${API_URL}/${userId}`, patchPayload, {
            headers: { 'Content-Type': 'application/json' },
        });
        check(patchRes, {
            'PATCH /api/v1/users/{id} status is 200': (r) => r.status === 200,
            'PATCH /api/v1/users/{id} patches name': (r) => {
                let body = JSON.parse(r.body);
                return body.name === `PatchedUser${__VU}_${__ITER}`;
            },
        });

        // Test DELETE /api/v1/users/{id} (delete user)
        let deleteRes = http.del(`${API_URL}/${userId}`);
        check(deleteRes, {
            'DELETE /api/v1/users/{id} status is 200': (r) => r.status === 200,
        });

        // Verify user is deleted
        let getDeletedRes = http.get(`${API_URL}/${userId}`);
        check(getDeletedRes, {
            'GET /api/v1/users/{id} after delete status is 400': (r) => r.status === 400,
            'GET /api/v1/users/{id} after delete returns not found': (r) => r.body.includes('not found'),
        });
    }

    // Test invalid email validation
    let invalidPayload = JSON.stringify({
        name: 'Invalid User',
        email: 'invalid',
    });
    let invalidRes = http.post(API_URL, invalidPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    check(invalidRes, {
        'POST /api/v1/users with invalid email status is 400': (r) => r.status === 400,
        'POST /api/v1/users with invalid email returns error': (r) => r.body.includes('Invalid email format'),
    });

    // Sleep to prevent overwhelming the server
    sleep(1);
}