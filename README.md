# 🚀 Tracker1: Full-Stack Job Application Tracker

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-B73BFE?style=for-the-badge&logo=vite&logoColor=FFD62E)

Tracker1 is a comprehensive, full-stack application designed to help job seekers seamlessly track their job applications, prepare for interviews, and manage their productivity. 

Built with a robust Spring Boot backend and a modern React frontend, it features progressive web app (PWA) capabilities, local-first storage, and a Chrome extension variant to maintain focus.

## ✨ Key Features

- **Job Application Tracking**: Keep track of applications, interview stages, and outcomes.
- **Productivity Tools**: Integrated Pomodoro timer and Habit tracker (local-first via IndexedDB).
- **Task Management**: Online-synchronized Todo lists.
- **Advanced Analytics**: Detailed insights into application success rates, monthly metrics, and dynamic reporting.
- **PWA & Offline Support**: Access cached job applications even when offline.
- **Chrome New Tab Extension**: Replace your new tab page with your job tracker dashboard.
- **AI Integration & Metrics**: Tracks server-side AI response times and push notification delivery rates.

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot (Java)
- **Database**: MongoDB
- **Security**: JWT Authentication
- **Build Tool**: Maven

### Frontend
- **Framework**: React.js with Vite
- **Styling**: Tailwind CSS
- **Storage**: localforage (IndexedDB) for local-first features

---

## 🚀 Getting Started

Follow these steps to set up the project locally.

### Prerequisites
- Node.js (v18+)
- Java (JDK 17+)
- Maven
- MongoDB (running locally or a MongoDB URI)

### 1. Backend Setup

Navigate to the `backend` directory:
```bash
cd backend
```

Set the required environment variables (e.g., in your terminal or IDE):
```env
MONGODB_URI=mongodb://localhost:27017/jobtracker
JWT_SECRET=your_super_secret_jwt_key_must_be_32_chars
PORT=5000

# Optional: Demo Data Seeding
DEMO_SEED_ENABLED=true
DEMO_SEED_EMAIL=test@example.com
DEMO_SEED_PASSWORD=securepassword
```

Run the backend server:
```bash
mvn spring-boot:run
```

### 2. Frontend Setup

Navigate to the `frontend` directory:
```bash
cd frontend
```

Set the optional environment variable (defaults to localhost:5000):
```env
VITE_API_BASE_URL=http://localhost:5000
```

Install dependencies and start the dev server:
```bash
npm install
npm run dev
```

---

## 🌐 PWA & Offline Functionality

Tracker1 is designed to be resilient. To verify offline functionality:
1. Build and preview the frontend: `cd frontend && npm run build && npm run preview`
2. Open the app while online and visit the Job Tracker to cache the data.
3. Toggle your network offline (via OS or Chrome DevTools).
4. Verify the App Shell loads and the Job Tracker displays previously visited applications from the cache.

*Note: Todos are online-only, while the Pomodoro and Habit trackers are local-first.*

---

## 🧩 Chrome Extension

You can build Tracker1 as a Chrome extension that replaces your New Tab page!

1. Build the extension bundle:
   ```bash
   cd frontend
   npm run build:extension
   ```
2. Open Chrome and navigate to `chrome://extensions`
3. Enable **Developer mode** in the top right.
4. Click **Load unpacked** and select the `chrome-extension/` folder from this repository.

*Note: The backend API defaults to `http://localhost:5000`. Set `VITE_API_BASE_URL` during the build if using a hosted backend.*

---

## ☁️ Deployment

### Backend (Render / Docker)
- Deploy using the root `Dockerfile` or `backend/Dockerfile`.
- Required Environment Variables: `MONGODB_URI`, `JWT_SECRET`, `PORT`, `ALLOWED_ORIGINS` (comma-separated, e.g., `https://your-vercel-domain.com`).
- Optional Variables: `VAPID_*` keys for Push Notifications, `GEMINI_API_KEY` for AI features.

### Frontend (Vercel)
- Connect your repository to Vercel and deploy the `frontend/` directory.
- Ensure you set the `VITE_API_BASE_URL` environment variable to your deployed backend URL.

---

## 📊 API & System Metrics

The backend exposes endpoints for analytics and monitoring:

- **Analytics**: 
  - `GET /api/applications/analytics` (Basic)
  - `GET /api/applications/analytics/monthly` (Monthly)
  - `GET /api/applications/analytics/pro` (Professional: supports date ranges, grouping, and top N)
- **Metrics**:
  - `GET /api/metrics` (Tracks server-side AI response times, push delivery success rates)
  - *Frontend locally records AI call timing, visible in `Settings -> Demo & Metrics`.*
