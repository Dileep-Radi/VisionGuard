package com.example.visionguard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class MazeView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // Using an odd dimension for a true Perfect DFS Maze. 9x9 is small, guaranteeing big visual blocks.
    private val MAZE_SIZE = 9
    private val maze = Array(MAZE_SIZE) { IntArray(MAZE_SIZE) }
    private var playerX = 1
    private var playerY = 1
    private var starX = MAZE_SIZE - 2
    private var starY = MAZE_SIZE - 2
    private var level = 1

    private val wallPaint = Paint().apply {
        color = 0xFF475569.toInt()  // Dark slate gray
        style = Paint.Style.FILL
    }

    private val pathPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()  // White
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = 0xFF94A3B8.toInt()  // Light gray border
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val playerPaint = Paint().apply {
        color = 0xFF38BDF8.toInt()  // Cyan
        style = Paint.Style.FILL
    }

    private val starPaint = Paint().apply {
        color = 0xFF000000.toInt()
        textAlign = Paint.Align.CENTER
    }

    private val playerBorder = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private var cellSize = 40f

    init {
        generateMaze(1)
    }

    fun generateMaze(newLevel: Int) {
        level = newLevel

        // Initialize maze with all walls
        for (i in 0 until MAZE_SIZE) {
            for (j in 0 until MAZE_SIZE) {
                maze[i][j] = 1  // 1 = wall
            }
        }

        // Start properties
        playerX = 1
        playerY = 1
        starX = MAZE_SIZE - 2
        starY = MAZE_SIZE - 2

        val random = Random(System.currentTimeMillis())
        carveMazeDFS(1, 1, random)

        // Double check start and end are open
        maze[1][1] = 0
        maze[MAZE_SIZE - 2][MAZE_SIZE - 2] = 0

        invalidate()
    }

    private fun carveMazeDFS(x: Int, y: Int, random: Random) {
        maze[x][y] = 0
        val dirs = mutableListOf(Pair(0, -2), Pair(0, 2), Pair(-2, 0), Pair(2, 0))
        dirs.shuffle(random)

        for (dir in dirs) {
            val nx = x + dir.first
            val ny = y + dir.second
            if (nx > 0 && nx < MAZE_SIZE && ny > 0 && ny < MAZE_SIZE && maze[nx][ny] == 1) {
                // Carve wall between
                maze[x + dir.first / 2][y + dir.second / 2] = 0
                carveMazeDFS(nx, ny, random)
            }
        }
    }

    fun movePlayer(dx: Int, dy: Int): Boolean {
        val newX = (playerX + dx).coerceIn(0, MAZE_SIZE - 1)
        val newY = (playerY + dy).coerceIn(0, MAZE_SIZE - 1)

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
        playerX = 1
        playerY = 1
        invalidate()
    }

    fun getLevel(): Int = level

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width == 0 || height == 0) return

        // The layout calculates cell size by dividing total available view width by maze size.
        // A small MAZE_SIZE (9) automatically guarantees huge visual blocks!
        cellSize = (width - paddingLeft - paddingRight) / MAZE_SIZE.toFloat()
        starPaint.textSize = cellSize * 0.7f

        // Draw grid
        for (i in 0 until MAZE_SIZE) {
            for (j in 0 until MAZE_SIZE) {
                val left = paddingLeft + i * cellSize
                val top = paddingTop + j * cellSize
                val right = left + cellSize
                val bottom = top + cellSize

                if (maze[i][j] == 1) {
                    canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, wallPaint)
                    canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, borderPaint)
                } else {
                    canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, pathPaint)
                    canvas.drawRect(left + 1, top + 1, right - 1, bottom - 1, borderPaint)
                }
            }
        }

        // Draw star
        val starLeft = paddingLeft + starX * cellSize + cellSize / 2
        val starTop = paddingTop + starY * cellSize + cellSize / 2 + cellSize * 0.25f
        canvas.drawText("⭐", starLeft, starTop, starPaint)

        // Draw player
        val playerLeft = paddingLeft + playerX * cellSize + cellSize / 2
        val playerTop = paddingTop + playerY * cellSize + cellSize / 2
        canvas.drawCircle(playerLeft, playerTop, cellSize / 3, playerPaint)
        canvas.drawCircle(playerLeft, playerTop, cellSize / 3, playerBorder)
    }
}
