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


Q7 = db("SELECT id FROM queues WHERE business_id=20 AND queue_number=7;")
R7 = db(f"SELECT reservation_id FROM queues WHERE id={Q7}")
Q3 = db("SELECT id FROM queues WHERE business_id=20 AND queue_number=3;")
TGL = db("SELECT queue_date FROM queues WHERE id=" + Q7)
print("Q7", Q7, "reservasi", R7, "Q3", Q3, TGL)

JO = SCRATCH + "/f6o.jar"
JC = SCRATCH + "/f6c.jar"
print("owner:", login_curl(JO, "owner@queuecare.id", "owner123"))
print("customer:", login_curl(JC, "dummy2@queuecare.id", "customer123"))
tok = csrf(JO, "/owner/businesses")
subprocess.run(["curl", "-s", "-b", JO, "-c", JO, "-o", "/dev/null", "-X", "POST",
                BASE + "/owner/businesses/20/pilih", "--data-urlencode", "_csrf=" + tok], check=False)

with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    for lebar in (375, 1280):
        ctx = browser.new_context(viewport={"width": lebar, "height": 900})
        page = ctx.new_page()
        login_browser(page, "dummy2@queuecare.id", "customer123")
        page.goto(BASE + f"/queue/{Q7}", wait_until="networkidle")
        page.wait_for_timeout(300)
        sw = page.evaluate("document.documentElement.scrollWidth")
        iw = page.evaluate("window.innerWidth")
        print(("OK" if sw <= iw else "OVERFLOW"), f"/queue/id {lebar}px {sw} vs {iw}",
              "| estimasi:", page.text_content("#smartwait-menit"))
        page.screenshot(path=f"docs/screenshots/fase6-monitor-{lebar}.png", full_page=True)

        page.goto(BASE + f"/reservations/{R7}", wait_until="networkidle")
        page.wait_for_timeout(300)
        sw = page.evaluate("document.documentElement.scrollWidth")
        iw = page.evaluate("window.innerWidth")
        print(("OK" if sw <= iw else "OVERFLOW"), f"/reservations/id {lebar}px {sw} vs {iw}",
              "| estimasi:", page.text_content(".smartwait__menit"))
        page.screenshot(path=f"docs/screenshots/fase6-detail-{lebar}.png", full_page=True)
        ctx.close()

    # estimasi turun tanpa muat ulang saat antrean di depan diselesaikan
    ctx = browser.new_context(viewport={"width": 1280, "height": 900})
    page = ctx.new_page()
    login_browser(page, "dummy2@queuecare.id", "customer123")
    page.goto(BASE + f"/queue/{Q7}", wait_until="networkidle")
    awal = page.text_content("#smartwait-menit")
    print("estimasi awal:", awal)
    tok = csrf(JO, "/owner/businesses")
    subprocess.run(["curl", "-s", "-b", JO, "-c", JO, "-o", "/dev/null", "-X", "POST",
                    BASE + f"/owner/queues/{Q3}/selesai", "--data-urlencode", "_csrf=" + tok,
                    "--data-urlencode", "tanggal=" + TGL], check=False)
    page.wait_for_function(
        "(a) => document.getElementById('smartwait-menit').textContent !== a", arg=awal, timeout=20000
    )
    print("estimasi setelah polling:", page.text_content("#smartwait-menit"),
          "| orang di depan:", page.text_content("#smartwait-orang"),
          "| url:", page.url)
    print("catatan tampil:", page.text_content("#smartwait-catatan"))
    page.screenshot(path="docs/screenshots/fase6-monitor-setelah.png", full_page=True)
    ctx.close()
    browser.close()
