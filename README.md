# Tracker1 (Interview Tracker)

Full-stack job application tracker.

## Tech

- Backend: Spring Boot + MongoDB + JWT (`backend/`)
- Frontend: React + Vite + Tailwind (`frontend/`)

## Local Setup

### Backend

Environment variables:

- `MONGODB_URI` (default: `mongodb://localhost:27017/jobtracker`)
- `JWT_SECRET` (required; must be >= 32 chars)
- `PORT` (default: `5000`)

Demo seed (optional):

- `DEMO_SEED_ENABLED` (`true`/`false`)
- `DEMO_SEED_EMAIL` (required if seeding)
- `DEMO_SEED_PASSWORD` (required if seeding)

Run:

```bash
cd backend
mvn spring-boot:run
```

### Frontend

Optional environment variables:

- `VITE_API_BASE_URL` (default: `http://localhost:5000`)

Run:

```bash
cd frontend
npm install
npm run dev
```

## Analytics

- Basic: `GET /api/applications/analytics`
- Monthly: `GET /api/applications/analytics/monthly`
- Professional: `GET /api/applications/analytics/pro?from=YYYY-MM-DD&to=YYYY-MM-DD&groupBy=month|day&topN=10`

## Week 1 Metrics

Backend exposes a simple metrics endpoint:

- `GET /api/metrics`

Includes:

- AI response time stats (server-side): avg/max per endpoint
- Push delivery rate: success/attempts (counts are from backend send attempts)

Frontend also records AI call timing locally (per browser). You can see a summary in `Settings -> Demo & Metrics`.

## Offline / PWA Verification

1. Build and preview the frontend: `cd frontend && npm run build && npm run preview`
2. Open the app once while online and visit Job Tracker to cache some pages/data.
3. Toggle your network offline (Chrome DevTools or OS).
4. Confirm: App shell still loads.
5. Confirm: Job Tracker lists previously visited applications from cache.

Notes:

- Todos are currently online-only.
- Pomodoro + Habits are local-first (IndexedDB via localforage).

## Deployment (Render backend + Vercel frontend)

Backend (Render):

- Use the root `Dockerfile` or `backend/Dockerfile`
- `MONGODB_URI`, `MONGODB_DATABASE` (optional), `JWT_SECRET`, `PORT`
- `ALLOWED_ORIGINS` (comma-separated, e.g. `https://<your-vercel-domain>`)
- Push (optional): `VAPID_PUBLIC_KEY`, `VAPID_PRIVATE_KEY`, `VAPID_SUBJECT`
- AI (optional): `GEMINI_API_KEY`
- Demo seed (optional): `DEMO_SEED_ENABLED=true`, `DEMO_SEED_EMAIL`, `DEMO_SEED_PASSWORD`

Frontend (Vercel):

- Deploy `frontend/`
- Set `VITE_API_BASE_URL` to your Render backend URL if you are not serving API on the same domain.

## Chrome New Tab Extension

This repo can be built as a Chrome extension that replaces the New Tab page with the same React app.

Build the extension bundle:

```bash
cd frontend
npm run build:extension
```

Load in Chrome:

1. Open `chrome://extensions`
2. Enable "Developer mode"
3. Click "Load unpacked"
4. Select the `chrome-extension/` folder

Notes:

- The extension points Chrome New Tab to `chrome-extension/dist/index.html` (the Vite build output).
- Routing uses hash URLs inside the extension so refresh/navigation works.
- The backend API is still `http://localhost:5000` by default. If you want the extension to talk to a hosted backend, set `VITE_API_BASE_URL` when building.
