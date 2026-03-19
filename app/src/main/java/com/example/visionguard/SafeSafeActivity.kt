package com.example.visionguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartSafeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_safe)

        // YouTube Button
        val youtubeBtn = findViewById<Button>(R.id.youtube_btn)
        youtubeBtn.setOnClickListener {
            openBrowser("https://www.youtube.com")
        }

        // Facebook Button
        val facebookBtn = findViewById<Button>(R.id.facebook_btn)
        facebookBtn.setOnClickListener {
            openBrowser("https://www.facebook.com")
        }

        // Instagram Button
        val instagramBtn = findViewById<Button>(R.id.instagram_btn)
        instagramBtn.setOnClickListener {
            openBrowser("https://www.instagram.com")
        }
    }

    private fun openBrowser(url: String) {
        val intent = Intent(this, SafeBrowserActivity::class.java)
        intent.putExtra("URL", url)
        startActivity(intent)
    }
}
