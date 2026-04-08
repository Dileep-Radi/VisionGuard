import os
import shutil
from PIL import Image

src_img = r'C:\Users\divya dileep\OneDrive\Desktop\VisionGuard - Copy\clipboard_icon.png'
base_res = r'c:\Users\divya dileep\OneDrive\Desktop\VisionGuard - Copy\app\src\main\res'

sizes = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

try:
    img = Image.open(src_img)
    for dpi, size in sizes.items():
        folder = os.path.join(base_res, f'mipmap-{dpi}')
        if not os.path.exists(folder):
            os.makedirs(folder)
        
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        resized.save(os.path.join(folder, 'ic_launcher.png'))
        resized.save(os.path.join(folder, 'ic_launcher_round.png'))
        
    print('Icons resized and saved.')
except Exception as e:
    print('Pillow failed:', e)
    for dpi in sizes.keys():
        folder = os.path.join(base_res, f'mipmap-{dpi}')
        if not os.path.exists(folder):
            os.makedirs(folder)
        shutil.copy(src_img, os.path.join(folder, 'ic_launcher.png'))
        shutil.copy(src_img, os.path.join(folder, 'ic_launcher_round.png'))
    print('Copied original without resizing.')

anydpi = os.path.join(base_res, 'mipmap-anydpi-v26')
if os.path.exists(anydpi):
    shutil.rmtree(anydpi)
anydpi33 = os.path.join(base_res, 'mipmap-anydpi-v33')
if os.path.exists(anydpi33):
    shutil.rmtree(anydpi33)
print('Deleted adaptive icons xml')
