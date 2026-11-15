import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
SCRATCH = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
OUT = "D:/PBO-Java/docs/screenshots"


def csrf(jar, path):
    html = subprocess.run(["curl", "-s", "-b", jar, BASE + path], capture_output=True, text=True).stdout
    m = re.search(r'name="_csrf"[^>]*value="([^"]+)"', html)
    return m.group(1) if m else ""


def login(page, email, password):
    page.goto(BASE + "/login", wait_until="networkidle")
    page.fill('input[name="username"]', email)
    page.fill('input[name="password"]', password)
    page.click('button[type="submit"]')
    page.wait_for_load_state("networkidle")


JC = SCRATCH + "/f8c.jar"
# Satu reservasi untuk akun Customer demo supaya halaman riwayat dan antrean terisi.
subprocess.run(["curl", "-s", "-c", JC, "-o", "/dev/null", BASE + "/login"], check=False)
tok = csrf(JC, "/login")
subprocess.run(["curl", "-s", "-b", JC, "-c", JC, "-o", "/dev/null", "-X", "POST", BASE + "/login",
                "--data-urlencode", "_csrf=" + tok, "--data-urlencode", "username=customer@queuecare.id",
                "--data-urlencode", "password=customer123"], check=False)
tok = csrf(JC, "/reservation/create/28?tanggal=2026-11-16")
subprocess.run(["curl", "-s", "-b", JC, "-c", JC, "-o", "/dev/null", "-X", "POST",
                BASE + "/reservation/create/28", "--data-urlencode", "_csrf=" + tok,
                "--data-urlencode", "tanggal=2026-11-16", "--data-urlencode", "jam=10:00"],
               check=False)

halaman_publik = [
    ("beranda", "/"),
    ("katalog", "/businesses"),
    ("detail-bisnis", "/businesses/20"),
    ("masuk", "/login"),
    ("daftar", "/register"),
]

halaman_customer = [
    ("customer-dashboard", "/customer"),
    ("customer-riwayat", "/reservations"),
    ("customer-profil", "/profile"),
]

halaman_owner = [
    ("owner-dashboard", "/owner"),
    ("owner-bisnis", "/owner/businesses"),
    ("owner-profil-bisnis", "/owner/business"),
    ("owner-layanan", "/owner/services"),
    ("owner-jadwal", "/owner/schedules"),
    ("owner-kapasitas", "/owner/capacity"),
    ("owner-antrean", "/owner/queues"),
    ("owner-laporan", "/owner/reports"),
    ("owner-profil", "/profile"),
]

halaman_admin = [
    ("admin-dashboard", "/admin"),
    ("admin-pengguna", "/admin/users"),
    ("admin-bisnis", "/admin/businesses"),
    ("admin-kategori", "/admin/categories"),
    ("admin-reservasi", "/admin/reservations"),
    ("admin-laporan", "/admin/reports"),
    ("admin-profil", "/profile"),
]

AKUN = {
    "customer": ("customer@queuecare.id", "customer123"),
    "owner": ("owner@queuecare.id", "owner123"),
    "admin": ("admin@queuecare.id", "admin123"),
}


def jalankan(browser, daftar, peran):
    for nama, url in daftar:
        for lebar in (375, 1280):
            ctx = browser.new_context(viewport={"width": lebar, "height": 900})
            page = ctx.new_page()
            if peran:
                login(page, *AKUN[peran])
            page.goto(BASE + url, wait_until="networkidle")
            page.wait_for_timeout(300)
            sw = page.evaluate("document.documentElement.scrollWidth")
            iw = page.evaluate("window.innerWidth")
            print(("OK" if sw <= iw else "OVERFLOW"), f"{nama} {lebar}px {sw} vs {iw}")
            page.screenshot(path=f"{OUT}/fase8-{nama}-{lebar}.png", full_page=True)
            ctx.close()


with sync_playwright() as p:
    browser = p.chromium.launch(channel="msedge", headless=True)
    jalankan(browser, halaman_publik, None)
    jalankan(browser, halaman_customer, "customer")
    jalankan(browser, halaman_owner, "owner")
    jalankan(browser, halaman_admin, "admin")
    browser.close()
