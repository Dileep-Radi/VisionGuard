package com.example.visionguard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StarCatchActivity : AppCompatActivity() {

    private lateinit var mazeView: MazeView
    private lateinit var tvLevel: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvMoves: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button
    private lateinit var btnUp: Button
    private lateinit var btnLeft: Button
    private lateinit var btnDown: Button
    private lateinit var btnRight: Button

    private var isGameRunning = false
    private var currentLevel = 1
    private var moves = 0
    private var elapsedSeconds = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_catch)

        mazeView = findViewById(R.id.mazeView)
        tvLevel = findViewById(R.id.tvLevel)
        tvTime = findViewById(R.id.tvTime)
        tvMoves = findViewById(R.id.tvMoves)
        tvStatus = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStart)
        btnUp = findViewById(R.id.btnUp)
        btnLeft = findViewById(R.id.btnLeft)
        btnDown = findViewById(R.id.btnDown)
        btnRight = findViewById(R.id.btnRight)

        btnStart.setOnClickListener { startGame() }
        btnUp.setOnClickListener { movePlayer(0, -1) }
        btnLeft.setOnClickListener { movePlayer(-1, 0) }
        btnDown.setOnClickListener { movePlayer(0, 1) }
        btnRight.setOnClickListener { movePlayer(1, 0) }

        updateUI()
    }

    private fun startGame() {
        if (isGameRunning) return

        isGameRunning = true
        moves = 0
        elapsedSeconds = 0
        btnStart.isEnabled = false
        btnStart.text = "Level $currentLevel"

        mazeView.generateMaze(currentLevel)
        mazeView.reset()
        tvStatus.text = "Find the star!"
        tvMoves.text = "0"
        tvTime.text = "0s"

        startTimer()
    }

    private fun movePlayer(dx: Int, dy: Int) {
        if (!isGameRunning) return

        if (mazeView.movePlayer(dx, dy)) {
            moves++
            tvMoves.text = moves.toString()

            // Check if star is found
            if (mazeView.isStarFound()) {
                finishLevel()
            }
        }
    }

    private fun startTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameRunning) {
                    elapsedSeconds++
                    tvTime.text = "${elapsedSeconds}s"
                    handler.postDelayed(this, 1000)
                }
            }
        }, 1000)
    }

    private fun finishLevel() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)

        val rating = when {
            currentLevel == 1 && moves <= 15 -> "Perfect! 🌟"
            currentLevel <= 3 && moves <= 20 -> "Great! ✓"
            currentLevel <= 5 && moves <= 30 -> "Good!"
            else -> "Nice!"
        }

        tvStatus.text = "$rating Next Level →"

        // Auto progress to next level after delay
        handler.postDelayed({
            currentLevel++
            tvLevel.text = currentLevel.toString()
            btnStart.text = "Next Level"
            btnStart.isEnabled = true
            startGame()
        }, 1500)
    }

    private fun updateUI() {
        tvLevel.text = currentLevel.toString()
        tvTime.text = "0s"
        tvMoves.text = "0"
        tvStatus.text = "Ready"
        btnStart.text = "Start Level $currentLevel"
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
