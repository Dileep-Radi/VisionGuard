package com.example.visionguard

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TwentyRuleActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvRound: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStart: Button

    private var timer: CountDownTimer? = null
    private var roundCount = 0
    private val totalRounds = 3   // user completes 3 eye breaks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twenty_rule)

        tvTimer = findViewById(R.id.tvTimer)
        tvMessage = findViewById(R.id.tvMessage)
        tvRound = findViewById(R.id.tvRound)
        progressBar = findViewById(R.id.progressBar)
        btnStart = findViewById(R.id.btnStartTimer)

        tvMessage.text = "👀 Ready to relax your eyes?"
        tvRound.text = "Round: 0 / $totalRounds"

        btnStart.setOnClickListener {
            startTimer()
        }
    }

    // ---------------- START TIMER ----------------
    private fun startTimer() {
        btnStart.isEnabled = false
        progressBar.progress = 0
        tvMessage.text = "👁 Look at something 20 feet away"
        tvMessage.setTextColor(Color.WHITE)

        timer = object : CountDownTimer(20_000, 1_000) {

            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                tvTimer.text = "⏳ $seconds sec"
                progressBar.progress = (20 - seconds).toInt()

                // Gentle encouragement
                when (seconds) {
                    15L -> tvMessage.text = "🙂 Keep going..."
                    10L -> tvMessage.text = "😌 Relax your eyes"
                    5L -> tvMessage.text = "✨ Almost done!"
                }
            }

            override fun onFinish() {
                roundCount++
                tvTimer.text = "✅ Done!"
                tvMessage.text = "🎉 Great job! Eyes relaxed"
                tvMessage.setTextColor(Color.GREEN)
                tvRound.text = "Round: $roundCount / $totalRounds"

                saveBreakCount()

                if (roundCount < totalRounds) {
                    btnStart.text = "Start Next Round"
                    btnStart.isEnabled = true
                } else {
                    finishGame()
                }
            }
        }.start()
    }

    // ---------------- FINISH GAME ----------------
    private fun finishGame() {
        btnStart.visibility = View.GONE
        progressBar.visibility = View.GONE
        tvTimer.text = "🏆"
        tvTimer.textSize = 48f
        tvMessage.text =
            "🎉 Eye Exercise Complete!\nYou followed the 20-20-20 rule"
        tvMessage.setTextColor(Color.CYAN)
    }

    // ---------------- SAVE BREAK DATA ----------------
    private fun saveBreakCount() {
        val prefs = getSharedPreferences("VG_PREFS", MODE_PRIVATE)
        val count = prefs.getInt("TAKE_BREAK", 0)
        prefs.edit().putInt("TAKE_BREAK", count + 1).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
