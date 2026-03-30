/**
 * Requires backend http://localhost:8080 and static site http://localhost:3456
 * Run: npm run screenshots
 */
import puppeteer from "puppeteer";
import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const ROOT = path.join(__dirname, "..");
const OUT = path.join(ROOT, "screenshots");

const BASE = "http://localhost:3456";
const API = "http://localhost:8080/api";

const USERS = {
  student: { email: "student@learnsphere.demo", password: "Demo123!" },
  instructor: { email: "instructor@learnsphere.demo", password: "Demo123!" },
  admin: { email: "admin@learnsphere.demo", password: "Demo123!" },
};

async function waitForApi(maxMs = 120000) {
  const start = Date.now();
  while (Date.now() - start < maxMs) {
    try {
      const r = await fetch(`${API}/catalog/courses`);
      if (r.ok) return;
    } catch {
      /* retry */
    }
    await new Promise((r) => setTimeout(r, 2000));
  }
  throw new Error("Backend not reachable at " + API);
}

async function waitForStatic(maxMs = 60000) {
  const start = Date.now();
  while (Date.now() - start < maxMs) {
    try {
      const r = await fetch(BASE + "/");
      if (r.ok) return;
    } catch {
      /* retry */
    }
    await new Promise((r) => setTimeout(r, 1000));
  }
  throw new Error("Frontend not reachable at " + BASE);
}

async function fetchCatalog(courseId) {
  const r = await fetch(`${API}/catalog/courses/${courseId}`);
  if (!r.ok) throw new Error("Catalog course " + courseId + " failed");
  return r.json();
}

async function loginApi(role) {
  const body = USERS[role];
  const r = await fetch(`${API}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!r.ok) throw new Error("Login failed for " + role + ": " + (await r.text()));
  return r.json();
}

async function clearSession(page) {
  await page.goto(`${BASE}/index.html`, { waitUntil: "load", timeout: 120000 });
  await page.evaluate(() => {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userName");
  });
}

async function applySession(page, jwt) {
  await page.evaluate((t) => {
    localStorage.setItem("jwtToken", t.token);
    localStorage.setItem("userRole", t.role);
    localStorage.setItem("userName", t.name);
  }, jwt);
}

function findChrome() {
  const candidates = [
    "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
    "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe",
    (process.env.LOCALAPPDATA || "") + "\\Google\\Chrome\\Application\\chrome.exe",
  ];
  for (const p of candidates) {
    if (p && fs.existsSync(p)) return p;
  }
  return undefined;
}

async function shot(page, name, url, extraWait = 500) {
  await page.goto(url, { waitUntil: "load", timeout: 120000 });
  await new Promise((r) => setTimeout(r, extraWait));
  const file = path.join(OUT, name);
  await page.screenshot({ path: file, fullPage: true });
  console.log("Wrote", file);
}

fs.mkdirSync(OUT, { recursive: true });

await waitForStatic();
await waitForApi();

const catalog = await fetchCatalog(1);
const firstLesson = catalog.lessons?.[0];
const firstAssignment = catalog.assignments?.[0];
const lessonUrl = firstLesson
  ? `${BASE}/lesson.html?courseId=1&lessonId=${firstLesson.id}`
  : `${BASE}/lesson.html?courseId=1&lessonId=1`;
const assignmentUrl = firstAssignment
  ? `${BASE}/assignment.html?id=${firstAssignment.id}&courseId=1`
  : `${BASE}/assignment.html?id=1&courseId=1`;

const browser = await puppeteer.launch({
  headless: true,
  executablePath: findChrome(),
  timeout: 120000,
  args: [
    "--no-sandbox",
    "--disable-setuid-sandbox",
    "--disable-dev-shm-usage",
    "--disable-gpu",
    "--window-size=1440,900",
  ],
});

const page = await browser.newPage();
await page.setViewport({ width: 1440, height: 900, deviceScaleFactor: 1 });

try {
  await clearSession(page);

  await shot(page, "01-home.png", `${BASE}/index.html`);
  await shot(page, "02-login.png", `${BASE}/login.html`);
  await shot(page, "03-signup.png", `${BASE}/signup.html`);
  await shot(page, "04-courses-catalog.png", `${BASE}/courses.html`);
  await shot(page, "05-course-detail-java.png", `${BASE}/course-detail.html?id=1`);
  await shot(page, "06-lesson.png", lessonUrl);
  await shot(page, "07-quiz.png", `${BASE}/quiz.html`);
  await shot(page, "08-assignment.png", assignmentUrl);

  let jwt = await loginApi("student");
  await page.goto(`${BASE}/index.html`, { waitUntil: "load", timeout: 120000 });
  await applySession(page, jwt);
  await shot(page, "09-dashboard-student.png", `${BASE}/student-dashboard.html`, 1200);
  await shot(page, "10-courses-view-as-student.png", `${BASE}/courses.html`, 800);

  jwt = await loginApi("instructor");
  await clearSession(page);
  await applySession(page, jwt);
  await shot(page, "11-dashboard-instructor.png", `${BASE}/instructor-dashboard.html`, 1200);
  await shot(page, "12-courses-view-as-instructor.png", `${BASE}/courses.html`, 800);

  jwt = await loginApi("admin");
  await clearSession(page);
  await applySession(page, jwt);
  await shot(page, "13-dashboard-admin.png", `${BASE}/admin-dashboard.html`, 1500);
  await shot(page, "14-courses-view-as-admin.png", `${BASE}/courses.html`, 800);

  await clearSession(page);
  await shot(page, "15-home-logged-out.png", `${BASE}/index.html`);
} finally {
  await browser.close();
}

console.log("Done. Screenshots in", OUT);
