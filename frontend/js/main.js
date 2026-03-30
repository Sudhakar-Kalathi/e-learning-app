const API_BASE_URL = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.querySelector(".menu-toggle");
    const links = document.querySelector(".nav-links");

    if (toggle && links) {
        toggle.addEventListener("click", () => {
            links.classList.toggle("show");
        });
    }

    updateNavbar();
});

function updateNavbar() {
    const navLinks = document.querySelector(".nav-links");
    if (!navLinks) return;

    const userRole = localStorage.getItem("userRole");
    const token = localStorage.getItem("jwtToken");

    if (token && userRole) {
        let dashboardLink = "student-dashboard.html";
        if (userRole === "ADMIN") dashboardLink = "admin-dashboard.html";
        if (userRole === "INSTRUCTOR") dashboardLink = "instructor-dashboard.html";

        navLinks.innerHTML = `
            <a href="index.html" class="nav-link">Home</a>
            <a href="courses.html" class="nav-link">Courses</a>
            <a href="${dashboardLink}" class="nav-link nav-link--accent">Dashboard</a>
            <a href="#" class="nav-link" onclick="logout(); return false;">Logout</a>
        `;
    } else {
        navLinks.innerHTML = `
            <a href="index.html" class="nav-link">Home</a>
            <a href="courses.html" class="nav-link">Courses</a>
            <a href="login.html" class="nav-link">Sign In</a>
            <a href="signup.html" class="btn btn-primary btn-nav">Get Started</a>
        `;
    }
}

function logout() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userName");
    window.location.href = "login.html";
}

async function catalogFetch(endpoint) {
    const url = `${API_BASE_URL}${endpoint}`;
    const response = await fetch(url);
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "Catalog request failed");
    }
    return response.json();
}

async function apiFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;

    if (!options.headers) {
        options.headers = {};
    }

    const token = localStorage.getItem("jwtToken");
    if (token) {
        options.headers["Authorization"] = `Bearer ${token}`;
    }

    if (options.body && !(options.body instanceof FormData)) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, options);

    if (response.status === 401) {
        logout();
        throw new Error("Unauthorized access. Redirecting to login.");
    }

    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || "API Error");
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return response.json();
    }
    return null;
}
