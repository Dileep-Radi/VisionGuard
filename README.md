**VisionGuard**

Hey there! Welcome to VisionGuard – an app I built to help us take better care of our eyes while we're glued to our screens. Let's be real, we all spend way too much time looking at our phones and it's taking a toll on our health. I wanted to create something that not only tracks our eye health but makes it a bit fun and interactive!

So, what is it? VisionGuard is a sweet Android app that uses some cool face-tracking tech to encourage healthy screen habits. It reminds you to rest your eyes, checks if you're blinking enough, and even includes a few mini-games to give your brain and eyes a break.

**Cool Features**

* Safe Browser: A clean, distraction-free browser designed to be gentle on the eyes and keep you safe online.
* Eye Games: Because eye exercises shouldn't be boring!
  * Blink Challenge: This one is actually super cool. It uses your camera to make sure you're blinking enough so your eyes don't dry out. 
  * Eye Exercise: Keep your focus sharp and your eye movements tracking smoothly.
  * Star Catcher: A quick, fun game to give your brain and eyes a relaxing break.
* Reports: Keep track of your screen time and see how your eye habits are improving over time.
* Settings: Customize your app preferences and settings to make VisionGuard perfectly suited for your personal routine.

**Tech Stuff & Requirements**

If you're interested in the geeky details, here's what makes the app tick:

* Platform: Android
* Minimum Android Version: Android 7.0 (Nougat / API 24)
* Target Version: Android 14 (API 34)
* Language: Kotlin 
* The Magic Sauce: 
  * I used Google's CameraX along with ML Kit Face Detection to get the blink tracking working perfectly in real-time.
  * Material Design to make the UI look clean and modern.

**How to Run It**

Want to try it out on your own device?
1. Clone it down:
   ```bash
   git clone https://github.com/Dileep-Radi/VisionGuard.git
   ```
2. Open it up in Android Studio.
3. Let Gradle do its thing and sync the dependencies.
4. Hit run on your emulator or physical Android phone (just make sure you have camera permissions enabled so the Blink Challenge works!).

**My Journey Building This**

Building VisionGuard was an awesome learning experience. Here are a few things I'm especially proud of:

* Setting up the whole app architecture, separating the different mini-games, browser, and main dashboard so it's super easy to navigate.
* Hooking up Google ML Kit Face Detection with the device camera. It took some tweaking, but getting the app to recognize when a user blinks for the Blink Challenge was incredibly rewarding!
* Designing the "Star Catcher" and "Eye Exercise" games to make eye breaks feel less like a chore and more like a fun little puzzle.
* Giving the app a fresh look with new icons and adding signed release configs so it's ready to be exported and shared.

Enjoy taking care of your eyes!

**Download the App**

Want to skip the build process and just install the app right away? You can download the latest assembled APK file directly from the repository here:
[Download VisionGuard APK](app/build/outputs/apk/debug/app-debug.apk) *(Note: Ensure you allow installations from unknown sources in your device settings).*
