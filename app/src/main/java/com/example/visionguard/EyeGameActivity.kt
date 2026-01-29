package com.example.visionguard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EyeGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_game)

        findViewById<Button>(R.id.btnBlink).setOnClickListener {
            startActivity(Intent(this, BlinkChallengeActivity::class.java))
        }

        findViewById<Button>(R.id.btnTwentyRule).setOnClickListener {
            startActivity(Intent(this, TwentyRuleActivity::class.java))
        }

        findViewById<Button>(R.id.btnStarCatch).setOnClickListener {
            startActivity(Intent(this, StarCatchActivity::class.java))
        }
    }
}
