package com.example.visionguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Safe Browser Button
        findViewById<Button>(R.id.btn_start_safe).setOnClickListener {
            startActivity(Intent(this, StartSafeActivity::class.java))
        }

        // Eye Games Button
        findViewById<Button>(R.id.btn_eye_games).setOnClickListener {
            startActivity(Intent(this, EyeGameActivity::class.java))
        }

        // Reports Button
        findViewById<Button>(R.id.btn_reports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        // Settings Button
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
