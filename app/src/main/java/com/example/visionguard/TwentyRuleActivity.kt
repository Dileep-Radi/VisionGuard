package com.example.visionguard

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class TwentyRuleActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvRound: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnStart: Button
    
    private lateinit var timerCircle: View
    private lateinit var eyeDot: View
    private lateinit var rootFrame: View

    private var timer: CountDownTimer? = null
    private var dotAnimator: ValueAnimator? = null
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
        timerCircle = findViewById(R.id.timerCircle)
        eyeDot = findViewById(R.id.eyeDot)
        rootFrame = findViewById(R.id.rootFrame)

        updateRoundDisplay()
        btnStart.setOnClickListener { startTimer() }
    }

    private fun updateRoundDisplay() {
        tvRound.text = "Round: $roundCount / $totalRounds"
    }

    private fun startTimer() {
        btnStart.visibility = View.INVISIBLE
        progressBar.progress = 0
        tvMessage.text = "Follow the green dot with your eyes"

        // Hide static timer text and circle temporarily during exercise
        timerCircle.visibility = View.INVISIBLE

        startDotAnimation()

        timer = object : CountDownTimer(timerDuration, 100) {

            override fun onTick(millisUntilFinished: Long) {
                // The timer still runs in the background
                val progress = ((timerDuration - millisUntilFinished) / 1000).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                dotAnimator?.cancel()
                eyeDot.visibility = View.INVISIBLE
                timerCircle.visibility = View.VISIBLE
                
                roundCount++
                tvTimer.text = "✓"
                tvMessage.text = "Round $roundCount complete! Eyes relaxed"
                tvRound.text = "Round: $roundCount / $totalRounds"
                progressBar.progress = 20

                saveBreakRecord()

                if (roundCount < totalRounds) {
                    btnStart.text = "Start Round ${roundCount + 1}"
                    btnStart.visibility = View.VISIBLE
                } else {
                    finishAllRounds()
                }
            }
        }.start()
    }

    private fun startDotAnimation() {
        eyeDot.visibility = View.VISIBLE

        val rootWidth = rootFrame.width.toFloat()
        val rootHeight = rootFrame.height.toFloat()
        
        // Ensure dimensions are available (in case layout hasn't finished)
        val width = if (rootWidth > 0) rootWidth else resources.displayMetrics.widthPixels.toFloat()
        val height = if (rootHeight > 0) rootHeight else resources.displayMetrics.heightPixels.toFloat()

        val amplitudeX = (width / 2f) - 64f // Padding from edges
        val amplitudeY = (height / 4f)

        dotAnimator = ValueAnimator.ofFloat(0f, (2 * PI).toFloat()).apply {
            duration = 4000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val t = animation.animatedValue as Float
                // Figure-8 parametric equations (Lemniscate of Gerono)
                val x = amplitudeX * sin(t)
                val y = amplitudeY * sin(t) * cos(t)

                eyeDot.translationX = x
                eyeDot.translationY = y
            }
            start()
        }
    }

    private fun finishAllRounds() {
        btnStart.visibility = View.GONE
        progressBar.visibility = View.GONE

        tvTimer.apply {
            text = "🎉"
            textSize = 72f
        }

        tvMessage.apply {
            text = "Excellent!\n\nYou completed 3 rounds of the Eye Exercise.\nYour eyes are now well relaxed!"
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
        dotAnimator?.cancel()
    }
}
