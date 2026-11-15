import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
OUT = "D:/PBO-Java/docs/screenshots"

with sync_playwright() as p:
    b = p.chromium.launch(channel="msedge", headless=True)
    page = b.new_context(viewport={"width": 1280, "height": 900}).new_page()
    page.goto(BASE + "/login", wait_until="networkidle")
    page.fill('input[name="username"]', "admin@queuecare.id")
    page.fill('input[name="password"]', "admin123")
    page.locator('.kartu button[type="submit"]').click()
    page.wait_for_load_state("networkidle")
    page.goto(BASE + "/admin/users", wait_until="networkidle")
    page.wait_for_timeout(300)
    page.locator("button[onclick*=showModal]").first.click()
    page.wait_for_timeout(400)
    print("dialog terbuka:", page.evaluate("document.querySelectorAll('dialog[open]').length"))
    page.screenshot(path=f"{OUT}/fase8-modal-pengguna-1280.png", full_page=False)
    print("UKURAN:", page.evaluate("[document.documentElement.scrollWidth, window.innerWidth]"))
    b.close()
