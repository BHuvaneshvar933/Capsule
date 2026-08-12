import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 50 }, // Ramp up to 50 users over 10s
    { duration: '30s', target: 50 }, // Stay at 50 users for 30s
    { duration: '10s', target: 0 },  // Ramp down to 0 users over 10s
  ],
};

export default function () {
  const params = {
    headers: {
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGxvYWQuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc4NjQ0MzQ1MCwiZXhwIjoxNzg5MDM1NDUwfQ.0zpr8i6g_fHSRfvpi-ljmCXeg5Us2yezZj6FiSLNjnQ',
      'Content-Type': 'application/json',
    },
  };
  const res = http.get('http://localhost:5000/api/applications', params);
  
  check(res, {
    'status is 200': (r) => r.status === 200,
    'status is not 500': (r) => r.status !== 500,
  });
  
  sleep(1);
}
