# LearnSphere — E-Learning Platform

Full-stack e-learning demo: **Spring Boot 3** REST API with JWT auth, **H2** database, and a **static HTML/CSS/JS** frontend. Repository: [github.com/Sudhakar-Kalathi/e-learning-app](https://github.com/Sudhakar-Kalathi/e-learning-app).

## Features

- **Roles:** `STUDENT`, `INSTRUCTOR`, `ADMIN` (Spring Security + method security).
- **Students:** browse catalog, enroll, view progress, submit assignments (API wired; file UI is demo).
- **Instructors:** create courses, add lessons and assignments.
- **Admins:** user list, aggregate stats, delete users.
- **Public catalog:** `GET /api/catalog/courses` and `GET /api/catalog/courses/{id}` (no login) for the marketing/course pages.

## Prerequisites

- **Java 17**
- **Maven 3.8+**

## Run the backend

```bash
cd backend
mvn spring-boot:run
```

API base URL: `http://localhost:8080/api`

On first startup, **demo users and sample courses** are created if the database is empty:

| Role        | Email                     | Password   |
|------------|---------------------------|------------|
| Admin      | `admin@learnsphere.demo`  | `Demo123!` |
| Instructor | `instructor@learnsphere.demo` | `Demo123!` |
| Student    | `student@learnsphere.demo` | `Demo123!` |

H2 console (optional): `http://localhost:8080/h2-console` (JDBC URL matches `application.properties`).

## Run the frontend

Open the static site with any static server so that relative paths behave consistently, for example:

```bash
cd frontend
npx --yes serve -l 3000
```

Then open `http://localhost:3000` (or open `frontend/index.html` via your editor’s live server).

The frontend expects the API at **`http://localhost:8080`**. To change it, edit `API_BASE_URL` in `frontend/js/main.js`.

## API overview

| Area        | Examples |
|------------|----------|
| Auth       | `POST /api/auth/register`, `POST /api/auth/login` |
| Catalog    | `GET /api/catalog/courses`, `GET /api/catalog/courses/{id}` |
| Student    | `GET /api/student/courses`, `POST /api/student/courses/{id}/enroll`, … |
| Instructor | `GET /api/instructor/courses`, `POST /api/instructor/courses`, … |
| Admin      | `GET /api/admin/stats`, `GET /api/admin/users`, … |

## Screenshots

PNG previews live in **`screenshots/`**. You can refresh them **manually**:

1. Start the **backend** (`cd backend`, then `mvn spring-boot:run`) so the API is on **http://localhost:8080**.
2. Serve the **frontend** (for example `cd frontend` and `npx serve -l 3000`) and open **http://localhost:3000** in your browser.
3. Visit each screen you care about (home, login, signup, courses, course detail, lesson, quiz, assignment, and each role dashboard after signing in with the demo accounts above).
4. Use your OS or browser (**Snipping Tool**, **Win+Shift+S**, **Print Screen**, or DevTools device toolbar for a fixed width) and save PNGs into **`screenshots/`** with clear names (for example `01-home.png`, `09-dashboard-student.png`).

## Project layout

```
backend/     Spring Boot application (Java)
frontend/    HTML pages, css/style.css, js/main.js
screenshots/ UI preview PNGs (optional; capture manually)
```

## Author

**Sudhakar Kalathi** — 2026
