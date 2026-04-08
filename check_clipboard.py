from PIL import ImageGrab
try:
    img = ImageGrab.grabclipboard()
    if img is not None:
        if isinstance(img, list):
            # sometimes grabclipboard returns a list of file paths
            from PIL import Image
            img = Image.open(img[0])
        else:
            # it's an Image object
            pass
        img.save('clipboard_icon.png')
        print("SAVED_CLIPBOARD")
    else:
        print("NO_IMAGE_IN_CLIPBOARD")
except Exception as e:
    print(f"ERROR: {e}")
