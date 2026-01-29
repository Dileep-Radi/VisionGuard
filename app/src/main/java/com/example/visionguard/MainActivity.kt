package com.example.visionguard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startSafe(view: View) {
        startActivity(Intent(this, StartSafeActivity::class.java))
    }

    fun openReports(view: View) {
        startActivity(Intent(this, ReportsActivity::class.java))
    }

    fun openSettings(view: View) {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    fun openEyeGame(view: View) {
        startActivity(Intent(this, EyeGameActivity::class.java))
    }
}
