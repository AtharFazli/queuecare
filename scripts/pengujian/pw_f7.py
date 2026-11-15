import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
MYSQL = "C:/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"


def db(sql):
    return subprocess.run([MYSQL, "-u", "root", "queuecare", "-N", "-B", "-e", sql],
                          capture_output=True, text=True).stdout.strip()


def csrf(jar, path):
    html = subprocess.run(["curl", "-s", "-b", jar, BASE + path], capture_output=True, text=True).stdout
    return re.search(r'name="_csrf"[^>]*value="([^"]+)"', html).group(1)


def login_curl(jar, email, password):
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
    return page.url


TGL = db("SELECT queue_date FROM queues ORDER BY queue_date LIMIT 1")

JO = SCRATCH + "/f7o.jar"
JA = SCRATCH + "/f7a.jar"
print("owner:", login_curl(JO, "owner@queuecare.id", "owner123"))
print("admin:", login_curl(JA, "admin@queuecare.id", "admin123"))
tok = csrf(JO, "/owner/businesses")
subprocess.run(["curl", "-s", "-b", JO, "-c", JO, "-o", "/dev/null", "-X", "POST",
                BASE + "/owner/businesses/20/pilih", "--data-urlencode", "_csrf=" + tok], check=False)

halaman = [
    ("owner", "owner", "/owner"),
    ("owner-reports", "owner", "/owner/reports?dari=2026-11-01&sampai=2026-11-30"),
    ("admin", "admin", "/admin"),
    ("admin-reports", "admin", "/admin/reports"),
    ("admin-reservations", "admin", "/admin/reservations"),
]

with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    for lebar in (375, 1280):
        for nama, peran, url in halaman:
            ctx = browser.new_context(viewport={"width": lebar, "height": 900})
            page = ctx.new_page()
            if peran == "owner":
                login_browser(page, "owner@queuecare.id", "owner123")
            else:
                login_browser(page, "admin@queuecare.id", "admin123")
            page.goto(BASE + url, wait_until="networkidle")
            page.wait_for_timeout(300)
            sw = page.evaluate("document.documentElement.scrollWidth")
            iw = page.evaluate("window.innerWidth")
            # lebar tabel di dalam .tabel-bungkus boleh digeser, bukan melebarkan halaman
            wrap = page.evaluate(
                "(() => { var w = document.querySelector('.tabel-bungkus');"
                " return w ? w.scrollWidth > w.clientWidth : false; })()"
            )
            print(("OK" if sw <= iw else "OVERFLOW"), f"{nama} {lebar}px {sw} vs {iw}",
                  "| tabel bisa digeser:", wrap)
            page.screenshot(path=f"docs/screenshots/fase7-{nama}-{lebar}.png", full_page=True)
            ctx.close()
    browser.close()
