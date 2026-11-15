import io
import os
import textwrap

from PIL import Image, ImageDraw, ImageFont

SRC = "D:/PBO-Java/src/main/java/com/queuecare"
OUT = "D:/PBO-Java/docs/screenshots"
FONT = "C:/Windows/Fonts/consola.ttf"

BERKAS = [
    ("SmartWaitService.java", SRC + "/service/SmartWaitService.java"),
    ("ReservationService.java", SRC + "/service/ReservationService.java"),
    ("QueueService.java", SRC + "/service/QueueService.java"),
    ("AdminUserService.java", SRC + "/service/AdminUserService.java"),
    ("SecurityConfig.java", SRC + "/config/SecurityConfig.java"),
]

font = ImageFont.truetype(FONT, 15)
lebar_maks = 118

for nama, jalur in BERKAS:
    baris = io.open(jalur, encoding="utf-8").read().split("\n")
    teks = []
    for b in baris:
        teks.extend(textwrap.wrap(b, width=lebar_maks, replace_whitespace=False,
                                   drop_whitespace=False) or [""])
    margin = 18
    tinggi = margin * 2 + len(teks) * 20
    img = Image.new("RGB", (lebar_maks * 9 + margin * 2, tinggi), (252, 250, 245))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, img.width - 1, img.height - 1], outline=(220, 214, 205))
    for i, t in enumerate(teks):
        d.text((margin, margin + i * 20), t, font=font, fill=(34, 44, 48))
    path = os.path.join(OUT, "kode-" + nama.replace(".java", "").lower() + ".png")
    img.save(path)
    print("OK", path, img.size)
