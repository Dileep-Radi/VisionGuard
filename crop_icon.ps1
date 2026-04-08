Add-Type -AssemblyName System.Drawing
$imgPath = "c:\Users\divya dileep\OneDrive\Desktop\VisionGuard - Copy\app\src\main\res\mipmap-xxxhdpi\ic_launcher.png"
$outPath = "c:\Users\divya dileep\OneDrive\Desktop\VisionGuard - Copy\app\src\main\res\drawable\ic_app_icon.png"

$img = [System.Drawing.Image]::FromFile($imgPath)
$w = $img.Width
$h = $img.Height

# We want to remove the bottom ~20% containing the text
$newH = [int]($h * 0.82)
# Make it a perfect square using the newly cropped height
$newW = $newH
# Center horizontally
$x = [int](($w - $newW) / 2)
$y = 0

$rect = New-Object System.Drawing.Rectangle($x, $y, $newW, $newH)
$bmp = New-Object System.Drawing.Bitmap($newW, $newH)
$graphics = [System.Drawing.Graphics]::FromImage($bmp)
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$graphics.DrawImage($img, 0, 0, $rect, [System.Drawing.GraphicsUnit]::Pixel)

$graphics.Dispose()
$img.Dispose()

if (Test-Path $outPath) { Remove-Item $outPath }
$bmp.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
Write-Output "Cropped icon successfully."
