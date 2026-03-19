package com.example.visionguard

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    companion object {
        private const val PREF = "vision_guard_reports"
        private const val KEY = "reports"

        fun saveUnsafeEvent(context: Context, appName: String) {
            val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            val old = prefs.getString(KEY, "[]")
            val array = JSONArray(old)

            val time = SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss",
                Locale.getDefault()
            ).format(Date())

            val obj = org.json.JSONObject()
            obj.put("app", appName)
            obj.put("time", time)

            array.put(obj)
            prefs.edit().putString(KEY, array.toString()).apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val clearBtn = findViewById<Button>(R.id.clear_btn)
        val reportsContainer = findViewById<LinearLayout>(R.id.reports_container)
        val emptyMessage = findViewById<TextView>(R.id.empty_message)

        // Clear button click listener
        clearBtn.setOnClickListener {
            getSharedPreferences(PREF, MODE_PRIVATE).edit().clear().apply()
            reportsContainer.removeAllViews()
            emptyMessage.visibility = android.view.View.VISIBLE
        }

        // Load and display reports
        val prefs = getSharedPreferences(PREF, MODE_PRIVATE)
        val array = JSONArray(prefs.getString(KEY, "[]"))

        if (array.length() == 0) {
            emptyMessage.visibility = android.view.View.VISIBLE
        } else {
            emptyMessage.visibility = android.view.View.GONE
            // Load reports in reverse order (newest first)
            for (i in array.length() - 1 downTo 0) {
                val obj = array.getJSONObject(i)
                val reportCard = createReportCard(obj.getString("app"), obj.getString("time"))
                reportsContainer.addView(reportCard)
            }
        }
    }

    private fun createReportCard(appName: String, time: String): LinearLayout {
        val card = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.card_background)
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(this).apply {
            text = "⚠️ Unsafe Usage Detected"
            textSize = 16f
            setTextColor(resources.getColor(R.color.warning, null))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val appText = TextView(this).apply {
            text = "App: $appName"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
        }

        val timeText = TextView(this).apply {
            text = "Time: $time"
            textSize = 14f
            setTextColor(resources.getColor(R.color.text_secondary, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 4
            }
        }

        card.addView(title)
        card.addView(appText)
        card.addView(timeText)

        return card
    }
}
