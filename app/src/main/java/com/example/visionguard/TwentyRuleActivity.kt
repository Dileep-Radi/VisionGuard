package com.example.visionguard

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
    private val totalRounds = 3
    private val timerDuration = 20_000L  // 20 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twenty_rule)

        tvTimer = findViewById(R.id.tvTimer)
        tvMessage = findViewById(R.id.tvMessage)
        tvRound = findViewById(R.id.tvRound)
        progressBar = findViewById(R.id.progressBar)
        btnStart = findViewById(R.id.btnStartTimer)

        updateRoundDisplay()
        btnStart.setOnClickListener { startTimer() }
    }

    private fun updateRoundDisplay() {
        tvRound.text = "Round: $roundCount / $totalRounds"
    }

    private fun startTimer() {
        btnStart.isEnabled = false
        progressBar.progress = 0
        tvMessage.text = "Look at something 20 feet away"

        timer = object : CountDownTimer(timerDuration, 100) {

            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val progress = ((timerDuration - millisUntilFinished) / 1000).toInt()

                // Update timer display
                tvTimer.text = (seconds + 1).toString()
                progressBar.progress = progress

                // Motivational messages at key points
                when (seconds) {
                    19 -> tvMessage.text = "Great! Keep your focus"
                    15 -> tvMessage.text = "Steady... relax your eyes"
                    10 -> tvMessage.text = "Focus on that distant object"
                    5 -> tvMessage.text = "Almost there... keep looking"
                    2 -> tvMessage.text = "Final seconds..."
                }
            }

            override fun onFinish() {
                roundCount++
                tvTimer.text = "✓"
                tvMessage.text = "Round $roundCount complete! Eyes relaxed"
                tvRound.text = "Round: $roundCount / $totalRounds"
                progressBar.progress = 20

                saveBreakRecord()

                if (roundCount < totalRounds) {
                    btnStart.text = "Start Round ${roundCount + 1}"
                    btnStart.isEnabled = true
                } else {
                    finishAllRounds()
                }
            }
        }.start()
    }

    private fun finishAllRounds() {
        btnStart.visibility = View.GONE
        progressBar.visibility = View.GONE

        tvTimer.apply {
            text = "🎉"
            textSize = 72f
        }

        tvMessage.apply {
            text = "Excellent!\n\nYou completed 3 rounds of the 20-20-20 rule.\nYour eyes are now well relaxed!"
            textSize = 18f
        }
    }

    private fun saveBreakRecord() {
        val prefs = getSharedPreferences("VG_PREFS", MODE_PRIVATE)
        val count = prefs.getInt("TAKE_BREAK", 0)
        prefs.edit().putInt("TAKE_BREAK", count + 1).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
