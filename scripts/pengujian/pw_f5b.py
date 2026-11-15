import datetime
import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
MYSQL = "C:/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"
TGL = (datetime.date.today() + datetime.timedelta(days=1)).isoformat()


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


Q3 = db(f"SELECT q.id FROM queues q WHERE q.business_id=20 AND q.status='IN_SERVICE' ORDER BY q.queue_date, q.queue_number LIMIT 1;")
R3 = db(f"SELECT reservation_id FROM queues WHERE id={Q3}")
RC = db("SELECT id FROM reservations WHERE status='CONFIRMED' LIMIT 1;")
print("Q3 =", Q3, "reservasi", R3, "| reservasi CONFIRMED =", RC)

JO = SCRATCH + "/f5o.jar"
print("owner:", login_curl(JO, "owner@queuecare.id", "owner123"))
tok = csrf(JO, "/owner/businesses")
subprocess.run(["curl", "-s", "-b", JO, "-c", JO, "-o", "/dev/null", "-X", "POST",
                BASE + "/owner/businesses/20/pilih", "--data-urlencode", "_csrf=" + tok], check=False)

with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    for lebar in (375, 1280):
        ctx = browser.new_context(viewport={"width": lebar, "height": 900})
        page = ctx.new_page()
        print("login owner", lebar, "->", login_browser(page, "owner@queuecare.id", "owner123"))
        page.goto(BASE + "/owner/queues?tanggal=" + TGL, wait_until="networkidle")
        page.wait_for_timeout(300)
        sw = page.evaluate("document.documentElement.scrollWidth")
        iw = page.evaluate("window.innerWidth")
        print(("OK" if sw <= iw else "OVERFLOW"), f"/owner/queues {lebar}px {sw} vs {iw}")
        page.screenshot(path=f"docs/screenshots/fase5-owner-queues-{lebar}.png", full_page=True)
        ctx.close()

        ctx = browser.new_context(viewport={"width": lebar, "height": 900})
        page = ctx.new_page()
        print("login customer", lebar, "->", login_browser(page, "dummy1@queuecare.id", "customer123"))
        page.goto(BASE + f"/queue/{Q3}", wait_until="networkidle")
        page.wait_for_timeout(300)
        sw = page.evaluate("document.documentElement.scrollWidth")
        iw = page.evaluate("window.innerWidth")
        print(("OK" if sw <= iw else "OVERFLOW"), f"/queue/id {lebar}px {sw} vs {iw}")
        page.screenshot(path=f"docs/screenshots/fase5-monitor-{lebar}.png", full_page=True)
        ctx.close()

    # pembaruan berkala: owner mengubah status, halaman Customer berubah tanpa muat ulang
    ctx = browser.new_context(viewport={"width": 1280, "height": 900})
    page = ctx.new_page()
    login_browser(page, "dummy1@queuecare.id", "customer123")
    page.goto(BASE + f"/queue/{Q3}", wait_until="networkidle")
    awal = page.text_content("#antrean-status")
    print("status awal:", awal)
    tok = csrf(JO, "/owner/businesses")
    subprocess.run(["curl", "-s", "-b", JO, "-c", JO, "-o", "/dev/null", "-X", "POST",
                    BASE + f"/owner/queues/{Q3}/selesai", "--data-urlencode", "_csrf=" + tok,
                    "--data-urlencode", "tanggal=" + TGL], check=False)
    page.wait_for_function(
        "(awal) => document.getElementById('antrean-status').textContent !== awal", arg=awal, timeout=20000
    )
    print("status setelah polling:", page.text_content("#antrean-status"),
          "| posisi:", page.text_content("#antrean-posisi"),
          "|", page.text_content("#antrean-keterangan"))
    print("url halaman (tidak berubah):", page.url)
    page.screenshot(path="docs/screenshots/fase5-monitor-setelah.png", full_page=True)

    page.goto(BASE + f"/reservation/success/{RC}", wait_until="networkidle")
    html = page.content()
    print("lencana--confirmed ada:", "lencana--confirmed" in html)
    print("lencana--in-service ada:", "lencana--in-service" in html)
    page.screenshot(path="docs/screenshots/fase5-lencana-confirmed-1280.png", full_page=True)
    ctx.close()
    browser.close()
