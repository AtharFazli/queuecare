import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"


def csrf(jar, path):
    html = subprocess.run(["curl", "-s", "-b", jar, BASE + path], capture_output=True, text=True).stdout
    m = re.search(r'name="_csrf"[^>]*value="([^"]+)"', html)
    return m.group(1) if m else ""


def login(jar, email, password):
    subprocess.run(["curl", "-s", "-c", jar, "-o", "/dev/null", BASE + "/login"], check=False)
    tok = csrf(jar, "/login")
    return subprocess.run(
        ["curl", "-s", "-b", jar, "-c", jar, "-o", "/dev/null", "-w", "%{http_code}",
         "-X", "POST", BASE + "/login", "--data-urlencode", "_csrf=" + tok,
         "--data-urlencode", "username=" + email, "--data-urlencode", "password=" + password],
        capture_output=True, text=True).stdout.strip()


def login_browser(page, email, password):
    page.goto(BASE + "/login", wait_until="networkidle")
    page.fill('input[name="username"]', email)
    page.fill('input[name="password"]', password)
    page.click('button[type="submit"]')
    page.wait_for_load_state("networkidle")


JA = SCRATCH + "/f7ba.jar"
JO = SCRATCH + "/f7bo.jar"
JC = SCRATCH + "/f7bc.jar"
print("admin:", login(JA, "admin@queuecare.id", "admin123"))
print("owner:", login(JO, "owner@queuecare.id", "owner123"))
print("customer:", login(JC, "customer@queuecare.id", "customer123"))

halaman = [
    ("admin-users", "admin", "/admin/users"),
    ("admin-users-tambah", "admin", "/admin/users/tambah"),
    ("profile-customer", "customer", "/profile"),
    ("profile-owner", "owner", "/profile"),
    ("profile-admin", "admin", "/profile"),
]

with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    for lebar in (375, 1280):
        for nama, peran, url in halaman:
            ctx = browser.new_context(viewport={"width": lebar, "height": 900})
            page = ctx.new_page()
            akun = {"admin": ("admin@queuecare.id", "admin123"),
                    "owner": ("owner@queuecare.id", "owner123"),
                    "customer": ("customer@queuecare.id", "customer123")}[peran]
            login_browser(page, *akun)
            page.goto(BASE + url, wait_until="networkidle")
            page.wait_for_timeout(300)
            sw = page.evaluate("document.documentElement.scrollWidth")
            iw = page.evaluate("window.innerWidth")
            print(("OK" if sw <= iw else "OVERFLOW"), f"{nama} {lebar}px {sw} vs {iw}")
            page.screenshot(path=f"docs/screenshots/fase7b-{nama}-{lebar}.png", full_page=True)
            ctx.close()
    browser.close()
