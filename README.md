# 🚀 Capsule: The Ultimate Developer Productivity & Career Suite

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![MongoDB](https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-B73BFE?style=for-the-badge&logo=vite&logoColor=FFD62E)

**Capsule** is a comprehensive, local-first developer productivity and career management suite. Designed to be your daily driver, it operates as a full-fledged Progressive Web App (PWA) and can even replace your Chrome New Tab page. 

Whether you're tracking job applications, prepping for interviews with AI, curating learning resources, or just trying to stay focused using the Pomodoro technique, Capsule keeps everything in one unified dashboard.

---

## ✨ Key Features

### 🧠 AI Career Assistant
- **Resume-Job Matcher**: Upload your PDF resume and paste a job description. The AI calculates a match score, highlights missing skills, provides a gap analysis, and suggests resume keywords.
- **Interview Question Generator**: Generate role-specific behavioral, technical, and company-focused interview questions based entirely on your resume's context.
- **Resume Library**: Securely store and manage multiple resume versions.

### 📌 Content Curator
- **Personal "Save-Anything" Board**: Save GitHub repos, tweets, articles, images, and quick notes. 
- **Auto-categorization**: Automatically detects the type of content based on the URL (e.g., GitHub Repo, Tweet, Video).
- **Tagging & Favorites**: Organize your saved resources easily.

### 📊 Advanced Job Tracking & Analytics
- **Pipeline Management**: Track applications across stages (Applied, OA, Interview, Offer, Rejected).
- **Deep Analytics**: View detailed insights like your response rate, weekly application velocity, most active days, and AI-driven suggestions (e.g., "Weekday applications perform better").

### ⏱️ Productivity Suite
- **Pomodoro Timer & Habit Tracker**: Local-first tools powered by IndexedDB (`localforage`) ensuring speed and privacy.
- **Task Management**: Online-synchronized Todo lists to keep your daily tasks aligned with your broader goals.

### 🌐 Seamless Access (PWA & Chrome Extension)
- **Progressive Web App**: Installable on desktop and mobile with offline caching support.
- **Chrome New Tab Extension**: Replace your default new tab page with the Capsule dashboard to stay focused on your goals every time you open a new tab.

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3 (Java 17)
- **Database**: MongoDB
- **Security**: Spring Security & JWT Authentication
- **Integrations**: WebFlux for OpenAI REST API calls, Apache PDFBox for Resume parsing, Web Push (VAPID) for PWA notifications.

### Frontend
- **Framework**: React.js 19 with Vite
- **Styling**: Tailwind CSS & clsx
- **Storage**: localforage (IndexedDB) for local-first features
- **Icons & Charts**: Lucide React, Recharts

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

Set the required environment variables:
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

## 🧩 Chrome Extension Setup

Keep Capsule front-and-center by making it your Chrome New Tab page!

1. Build the extension bundle from the `frontend` directory:
   ```bash
   npm run build:extension
   ```
2. Open Chrome and navigate to `chrome://extensions`
3. Enable **Developer mode** in the top right.
4. Click **Load unpacked** and select the `chrome-extension/` folder from this repository.

*Note: The extension points Chrome to the Vite build output and defaults to `http://localhost:5000` for API calls.*

---

## ☁️ Deployment

### Backend (Render / Docker)
- Deploy using the root `Dockerfile` or `backend/Dockerfile`.
- Required Variables: `MONGODB_URI`, `JWT_SECRET`, `PORT`, `ALLOWED_ORIGINS` (comma-separated).
- Optional Variables: `VAPID_*` keys for Push Notifications.

### Frontend (Vercel)
- Connect your repository to Vercel and deploy the `frontend/` directory.
- Ensure you set `VITE_API_BASE_URL` to your deployed backend URL.
