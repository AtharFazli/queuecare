import re
import subprocess
from playwright.sync_api import sync_playwright

BASE = "http://localhost:8080"
S = "C:/Users/MyBook Hype AMD/AppData/Local/hermes/cache/scratch"
OUT = "D:/PBO-Java/docs/screenshots"
MYSQL = "C:/laragon/bin/mysql/mysql-8.0.30-winx64/bin/mysql.exe"
OK = 0
BAD = 0


def db(sql):
    return subprocess.run([MYSQL, "-u", "root", "queuecare", "-N", "-B", "-e", sql],
                          capture_output=True, text=True).stdout.strip()


def lulus(p):
    global OK
    OK += 1
    print("OK    -", p)


def gagal(p):
    global BAD
    BAD += 1
    print("GAGAL -", p)


EMAIL = "browsersial@queuecare.id"
subprocess.run([MYSQL, "-u", "root", "queuecare", "-e",
                f"DELETE FROM users WHERE email='{EMAIL}'"], capture_output=True)

with sync_playwright() as p:
    b = p.chromium.launch(channel="msedge", headless=True)
    ctx = b.new_context(viewport={"width": 1280, "height": 900})
    page = ctx.new_page()

    # 1. login admin lewat browser
    page.goto(BASE + "/login", wait_until="networkidle")
    page.fill('input[name="username"]', "admin@queuecare.id")
    page.fill('input[name="password"]', "admin123")
    page.locator('.kartu button[type="submit"]').click()
    page.wait_for_load_state("networkidle")
    if "/admin" in page.url:
        lulus("login admin lewat browser")
    else:
        gagal("login admin lewat browser: " + page.url)

    # 2. tambah pengguna lewat browser
    page.goto(BASE + "/admin/users/tambah", wait_until="networkidle")
    page.fill("#name", "Sial Browser")
    page.fill("#email", EMAIL)
    page.select_option("#role", "CUSTOMER")
    page.fill("#password", "rahasia123")
    page.locator('.kartu button[type="submit"]').click()
    page.wait_for_load_state("networkidle")
    if "Pengguna ditambahkan" in page.content():
        lulus("tambah pengguna lewat browser")
    else:
        gagal("tambah pengguna lewat browser")
    page.screenshot(path=f"{OUT}/fase8-tambah-pengguna-1280.png", full_page=True)

    # 3. email duplikat: isian harus tetap dan pesan galat tampil
    page.goto(BASE + "/admin/users/tambah", wait_until="networkidle")
    page.fill("#name", "Sial Duplikat")
    page.fill("#email", EMAIL)
    page.select_option("#role", "OWNER")
    page.fill("#password", "rahasia123")
    page.locator('.kartu button[type="submit"]').click()
    page.wait_for_load_state("networkidle")
    html = page.content()
    if "sudah dipakai pengguna lain" in html and page.input_value("#name") == "Sial Duplikat" \
            and page.input_value("#email") == EMAIL:
        lulus("email duplikat ditolak dan isian form tetap")
    else:
        gagal("email duplikat / isian form: name=%r email=%r pesan=%s"
              % (page.input_value("#name"), page.input_value("#email"),
                 "ada" if "sudah dipakai" in html else "tidak ada"))
    page.screenshot(path=f"{OUT}/fase8-form-pengguna-1280.png", full_page=True)

    # 4. ubah pengguna lewat browser
    uid = db(f"SELECT id FROM users WHERE email='{EMAIL}'")
    page.goto(BASE + f"/admin/users/{uid}/ubah", wait_until="networkidle")
    page.fill("#name", "Sial Browser Ubah")
    page.locator('.kartu button[type="submit"]').click()
    page.wait_for_load_state("networkidle")
    if db(f"SELECT name FROM users WHERE email='{EMAIL}'") == "Sial Browser Ubah":
        lulus("ubah pengguna lewat browser")
    else:
        gagal("ubah pengguna lewat browser")

    # 5. nonaktifkan lewat modal
    page.goto(BASE + "/admin/users?q=" + EMAIL, wait_until="networkidle")
    page.click("button[onclick*=showModal]")
    page.wait_for_timeout(300)
    page.click(f"dialog#status-{uid} button[type='submit']")
    page.wait_for_load_state("networkidle")
    if db(f"SELECT active FROM users WHERE email='{EMAIL}'") == "0":
        lulus("nonaktifkan pengguna lewat modal")
    else:
        gagal("nonaktifkan pengguna lewat modal")
    page.screenshot(path=f"{OUT}/fase8-nonaktif-pengguna-1280.png", full_page=True)

    # 6. login akun nonaktif ditolak
    ctx2 = b.new_context(viewport={"width": 375, "height": 900})
    page2 = ctx2.new_page()
    page2.goto(BASE + "/login", wait_until="networkidle")
    page2.fill('input[name="username"]', EMAIL)
    page2.fill('input[name="password"]', "rahasia123")
    page2.click('button[type="submit"]')
    page2.wait_for_load_state("networkidle")
    if "Akun dinonaktifkan" in page2.content():
        lulus("login akun nonaktif ditolak di browser")
    else:
        gagal("login akun nonaktif: " + page2.url)
    page2.screenshot(path=f"{OUT}/fase8-login-nonaktif-375.png", full_page=True)

    ctx2.close()
    ctx.close()
    b.close()

subprocess.run([MYSQL, "-u", "root", "queuecare", "-e",
                f"DELETE FROM users WHERE email='{EMAIL}'"], capture_output=True)
print("TOTAL OK=%d GAGAL=%d" % (OK, BAD))
