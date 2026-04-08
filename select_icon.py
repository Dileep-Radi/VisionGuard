import tkinter as tk
from tkinter import filedialog
import sys

def main():
    root = tk.Tk()
    root.withdraw()
    root.attributes('-topmost', True)
    
    file_path = filedialog.askopenfilename(
        title="Please select the Icon Image for VisionGuard",
        filetypes=[("Image files", "*.png *.jpg *.jpeg *.webp *.avif")]
    )
    
    if file_path:
        with open("selected_icon.txt", "w", encoding="utf-8") as f:
            f.write(file_path)
            
if __name__ == '__main__':
    main()
