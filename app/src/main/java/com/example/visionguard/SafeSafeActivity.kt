package com.example.visionguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class StartSafeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_safe)
    }

    fun openYouTube(view: View) {
        openBrowser("https://www.youtube.com")
    }

    fun openFacebook(view: View) {
        openBrowser("https://www.facebook.com")
    }

    fun openInstagram(view: View) {
        openBrowser("https://www.instagram.com")
    }

    private fun openBrowser(url: String) {
        val intent = Intent(this, SafeBrowserActivity::class.java)
        intent.putExtra("URL", url)
        startActivity(intent)
    }
}
