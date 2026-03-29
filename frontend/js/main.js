const API_BASE_URL = 'http://localhost:8080/api';

document.addEventListener("DOMContentLoaded", () => {
    // Navbar toggle
    const toggle = document.querySelector(".menu-toggle");
    const links = document.querySelector(".nav-links");

    if (toggle && links) {
        toggle.addEventListener("click", () => {
            links.classList.toggle("show");
        });
    }

    // Dynamic Navbar based on role
    updateNavbar();
});

function updateNavbar() {
    const navLinks = document.querySelector(".nav-links");
    if (!navLinks) return;
    
    const userRole = localStorage.getItem("userRole");
    const token = localStorage.getItem("jwtToken");

    if (token && userRole) {
        let dashboardLink = "index.html";
        if (userRole === "ADMIN") dashboardLink = "admin-dashboard.html";
        if (userRole === "INSTRUCTOR") dashboardLink = "instructor-dashboard.html";
        if (userRole === "STUDENT") dashboardLink = "student-dashboard.html";

        navLinks.innerHTML = `
            <li><a href="index.html">Home</a></li>
            <li><a href="courses.html">Courses</a></li>
            <li><a href="${dashboardLink}">Dashboard</a></li>
            <li><a href="#" onclick="logout(); return false;">Logout</a></li>
        `;
    } else {
        navLinks.innerHTML = `
            <li><a href="index.html">Home</a></li>
            <li><a href="courses.html">Courses</a></li>
            <li><a href="login.html">Login</a></li>
            <li><a href="signup.html">Sign Up</a></li>
        `;
    }
}

function logout() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userName");
    window.location.href = "login.html";
}

async function apiFetch(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    // Add Authentication header if token exists
    const token = localStorage.getItem("jwtToken");
    if(!options.headers) {
        options.headers = {};
    }
    
    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    if (options.body && !(options.body instanceof FormData)) {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(options.body);
    }
    
    const response = await fetch(url, options);
    
    // Handle unauthorized responses automatically
    if (response.status === 401) {
        logout();
        throw new Error("Unauthorized access. Redirecting to login.");
    }
    
    if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'API Error');
    }
    
    return await response.json();
}
