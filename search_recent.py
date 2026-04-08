import os
import time

search_root = r"C:\Users\divya dileep"
extensions = {".png", ".jpg", ".jpeg", ".webp", ".avif"}
now = time.time()
one_hour = 60 * 60

recent_files = []

for root, dirs, files in os.walk(search_root):
    if any(skip in root for skip in ['AppData\\Local\\Microsoft', 'node_modules', '.gradle', '.android']):
        continue
    for f in files:
        ext = os.path.splitext(f)[1].lower()
        if ext in extensions:
            fp = os.path.join(root, f)
            try:
                mtime = os.path.getmtime(fp)
                if now - mtime < one_hour:
                    recent_files.append((fp, mtime))
            except:
                pass

recent_files.sort(key=lambda x: x[1], reverse=True)
print("RECENT_IMAGES_START")
for rp in recent_files[:20]:
    print(rp[0])
print("RECENT_IMAGES_END")
