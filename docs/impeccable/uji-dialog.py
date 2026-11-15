"""Browser regression. Jalankan hanya terhadap server/database uji via --base."""
import argparse
import json
from pathlib import Path
from playwright.sync_api import sync_playwright

parser = argparse.ArgumentParser()
parser.add_argument('--base', default='http://localhost:8081')
parser.add_argument('--out', required=True)
args = parser.parse_args()
results = []

def login(page, email, password):
    page.goto(args.base + '/login', wait_until='networkidle')
    page.locator('[name=username]').fill(email)
    page.locator('[name=password]').fill(password)
    page.locator('.kartu button[type=submit]').click()
    page.wait_for_load_state('networkidle')
    assert '/login' not in page.url

def closed(page):
    states = page.locator('dialog:not([open])').evaluate_all('els => els.map(el => ({id:el.id,display:getComputedStyle(el).display,rects:el.getClientRects().length}))')
    assert states and all(v['display'] == 'none' and v['rects'] == 0 for v in states), states
    return states

def cycle(page, button):
    button.click()
    dialog = page.locator('dialog[open]')
    assert dialog.count() == 1 and dialog.is_visible()
    identifier = dialog.get_attribute('id')
    screen_dir = Path(args.out).parent / 'dialog-screenshots'
    screen_dir.mkdir(exist_ok=True)
    viewport = page.viewport_size['width']
    page.screenshot(path=str(screen_dir / f'{identifier}-{viewport}-open.png'))
    dialog.get_by_role('button', name='Kembali', exact=True).click()
    assert page.locator('dialog[open]').count() == 0
    closed(page)
    button.click()
    assert page.locator('dialog[open]').get_attribute('id') == identifier
    return page.locator('dialog[open]')

with sync_playwright() as p:
    browser = p.chromium.launch(channel='msedge', headless=True)
    for width in (375, 1280):
        ctx = browser.new_context(viewport={'width': width, 'height': 900})
        page = ctx.new_page()
        login(page, 'admin@queuecare.id', 'admin123')
        page.goto(args.base + '/admin/users', wait_until='networkidle')
        states = closed(page)
        page.goto(args.base + '/admin/users?q=dummy1', wait_until='networkidle')
        button = page.locator('button[onclick*=showModal]').first
        action = button.inner_text()
        dialog = cycle(page, button)
        dialog.locator('button[type=submit]').click()
        page.wait_for_load_state('networkidle')
        page.goto(args.base + '/admin/users?q=dummy1', wait_until='networkidle')
        assert page.locator('button[onclick*=showModal]').first.inner_text() != action
        closed(page)
        # Kembalikan status akun uji melalui konfirmasi browser juga.
        cycle(page, page.locator('button[onclick*=showModal]').first).locator('button[type=submit]').click()
        page.wait_for_load_state('networkidle')
        page.goto(args.base + '/admin/users?q=dummy1', wait_until='networkidle')
        assert page.locator('button[onclick*=showModal]').first.inner_text() == action
        results.append({'viewport':width,'route':'/admin/users','closed':states,'open_back_confirm':True,'status_restored':True})
        ctx.close()
        ctx = browser.new_context(viewport={'width':width,'height':900})
        page = ctx.new_page()
        login(page, 'owner@queuecare.id', 'owner123')
        page.goto(args.base + '/owner/businesses', wait_until='networkidle')
        page.locator('form[action="/owner/businesses/20/pilih"] button').click()
        page.wait_for_load_state('networkidle')
        page.goto(args.base + '/owner/queues?tanggal=2026-11-16', wait_until='networkidle')
        states = closed(page)
        button = page.get_by_role('button', name='Batalkan', exact=True).first
        dialog = cycle(page, button)
        identifier = dialog.get_attribute('id')
        row = page.locator('tr').filter(has=button)
        number = row.locator('td').first.inner_text()
        dialog.locator('button[type=submit]').click()
        page.wait_for_load_state('networkidle')
        row = page.locator('tbody tr').filter(has=page.get_by_text(number, exact=True))
        assert row.count() == 1 and row.locator('.lencana--cancelled').count() == 1
        closed(page)
        results.append({'viewport':width,'route':'/owner/queues','closed':states,'dialog':identifier,'open_back_confirm':True})
        ctx.close()
    browser.close()
Path(args.out).write_text(json.dumps(results,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print(json.dumps(results,ensure_ascii=False,indent=2))
