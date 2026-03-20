package com.example.visionguard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MazeView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val maze = Array(10) { IntArray(10) }
    private var playerX = 0
    private var playerY = 0
    private var starX = 9
    private var starY = 9

    private val wallPaint = Paint().apply {
        color = 0xFF64748B.toInt()
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val pathPaint = Paint().apply {
        color = 0xFFFAFAFA.toInt()
        style = Paint.Style.FILL
    }

    private val playerPaint = Paint().apply {
        color = 0xFF38BDF8.toInt()
        style = Paint.Style.FILL
    }

    private val starPaint = Paint().apply {
        color = 0xFF000000.toInt()
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    private var cellSize = 40f

    init {
        generateMaze()
    }

    private fun generateMaze() {
        // Create a simple maze pattern
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                maze[i][j] = if ((i + j) % 3 == 0) 1 else 0 // 1 = wall, 0 = path
            }
        }

        // Ensure start and end are paths
        maze[0][0] = 0
        maze[9][9] = 0
        playerX = 0
        playerY = 0
        starX = 9
        starY = 9

        // Create a few open paths
        for (i in 0..9) {
            maze[i][1] = 0
            maze[1][i] = 0
            maze[i][5] = 0
            maze[5][i] = 0
        }
    }

    fun movePlayer(dx: Int, dy: Int): Boolean {
        val newX = (playerX + dx).coerceIn(0, 9)
        val newY = (playerY + dy).coerceIn(0, 9)

        if (maze[newX][newY] == 0) {
            playerX = newX
            playerY = newY
            invalidate()
            return true
        }
        return false
    }

    fun isStarFound(): Boolean {
        return playerX == starX && playerY == starY
    }

    fun reset() {
        playerX = 0
        playerY = 0
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        cellSize = (width - paddingLeft - paddingRight) / 10f

        // Draw grid
        for (i in 0 until 10) {
            for (j in 0 until 10) {
                val left = paddingLeft + i * cellSize
                val top = paddingTop + j * cellSize

                if (maze[i][j] == 1) {
                    // Draw wall
                    canvas.drawRect(left, top, left + cellSize, top + cellSize, wallPaint)
                } else {
                    // Draw path
                    canvas.drawRect(left + 2, top + 2, left + cellSize - 2, top + cellSize - 2, pathPaint)
                }
            }
        }

        // Draw star
        val starLeft = paddingLeft + starX * cellSize + cellSize / 2
        val starTop = paddingTop + starY * cellSize + cellSize / 2
        canvas.drawText("⭐", starLeft, starTop + 8, starPaint)

        // Draw player
        val playerLeft = paddingLeft + playerX * cellSize + cellSize / 2
        val playerTop = paddingTop + playerY * cellSize + cellSize / 2
        canvas.drawCircle(playerLeft, playerTop, cellSize / 4, playerPaint)
    }
}
