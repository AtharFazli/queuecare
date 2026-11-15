"""Jalankan dari root proyek: python docs/impeccable/verifikasi.py."""
import json
import re
import subprocess
from pathlib import Path

import yaml

ROOT = Path(__file__).resolve().parents[2]
old_bytes = (ROOT / 'docs/DESIGN-sebelum-impeccable.md').read_bytes()
new_bytes = (ROOT / 'DESIGN.md').read_bytes()
assert old_bytes in new_bytes, 'Isi cadangan harus tetap utuh byte per byte'
old = old_bytes.decode('utf-8')
new = new_bytes.decode('utf-8')
front, body = new.split('---', 2)[1:]
data = yaml.safe_load(front)
headings = lambda text: re.findall(r'^#{1,6} .+$', text, re.MULTILINE)
before, after = headings(old), headings(body)
assert after[:len(before)] == before, 'Judul lama berubah atau hilang'
assert len(after) == len(set(after)), 'Judul ganda'
css_tokens = dict(re.findall(r'(--[\w-]+)\s*:\s*([^;]+);', (ROOT / 'src/main/resources/static/css/tokens.css').read_text()))
for name, value in css_tokens.items():
    key = name[2:]
    if name.startswith('--warna-'):
        assert data['colors'][key] == value
    elif name.startswith('--font-'):
        assert data['typography'][key]['fontFamily'] == value
    elif name.startswith('--teks-'):
        assert data['typography'][key]['fontSize'] == value
    elif name.startswith('--radius-'):
        assert data['rounded'][key] == value
    elif name.startswith('--jarak-'):
        assert data['spacing'][key] == value
    else:
        assert '# ' + name + ': ' + value in front
sidecar = json.loads((ROOT / '.impeccable/design.json').read_text(encoding='utf-8'))
assert sidecar['schemaVersion'] == 2
assert len(sidecar['components']) == 10
for component in sidecar['components']:
    assert component['css'] and component['html']
    if 'refersTo' in component:
        assert component['refersTo'] in data['components']
product = (ROOT / 'PRODUCT.md').read_text(encoding='utf-8')
assert '<!-- impeccable:product-schema 1 -->' in product
assert re.search(r'## Platform\s+web\s', product)
protected = ['src/main/resources/templates', 'src/main/resources/static/js', 'scripts/pengujian']
diff = subprocess.check_output(['git', 'diff', '72aa9ce', '--', *protected], cwd=ROOT)
assert not diff, 'Template, JavaScript, atau skrip pengujian berubah'
old_css = subprocess.check_output(['git', 'show', '6f917b7:src/main/resources/static/css/components.css'], cwd=ROOT).decode()
css = (ROOT / 'src/main/resources/static/css/components.css').read_text()
expected_css = old_css.replace('#8F3022', 'var(--warna-bahaya-gelap)').replace('pointer-events: auto; }', 'pointer-events: auto; }\ndialog.modal:not([open]) { display: none; }')
assert css.replace('\r\n', '\n') == expected_css.replace('\r\n', '\n'), 'Perubahan CSS melampaui dua patch B'
old_tokens = subprocess.check_output(['git', 'show', '6f917b7:src/main/resources/static/css/tokens.css'], cwd=ROOT).decode()
assert (ROOT / 'src/main/resources/static/css/tokens.css').read_text() == old_tokens.replace('  --warna-bahaya: #B3402F;', '  --warna-bahaya: #B3402F;\n  --warna-bahaya-gelap: #8F3022;')
record = {'old_body_byte_identical': True, 'old_heading_count': len(before), 'new_heading_count': len(after), 'before': before, 'after': after, 'added': after[len(before):], 'css_token_count': len(css_tokens), 'protected_diff_empty': True}
(ROOT / 'docs/impeccable/judul-sebelum-sesudah.json').write_text(json.dumps(record, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
print(json.dumps(record, ensure_ascii=False, indent=2))
