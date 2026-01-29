package com.example.visionguard

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
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

            val obj = JSONObject()
            obj.put("app", appName)
            obj.put("time", time)

            array.put(obj)
            prefs.edit().putString(KEY, array.toString()).apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        val clearBtn = Button(this).apply {
            text = "Clear Reports"
            setOnClickListener {
                getSharedPreferences(PREF, MODE_PRIVATE)
                    .edit().clear().apply()
                recreate()
            }
        }
        layout.addView(clearBtn)

        val prefs = getSharedPreferences(PREF, MODE_PRIVATE)
        val array = JSONArray(prefs.getString(KEY, "[]"))

        if (array.length() == 0) {
            layout.addView(TextView(this).apply {
                text = "No unsafe usage recorded"
                textSize = 18f
            })
        } else {
            for (i in array.length() - 1 downTo 0) {
                val obj = array.getJSONObject(i)
                layout.addView(TextView(this).apply {
                    text =
                        "⚠️ Unsafe Usage\n" +
                        "App: ${obj.getString("app")}\n" +
                        "Time: ${obj.getString("time")}\n\n"
                })
            }
        }

        scroll.addView(layout)
        setContentView(scroll)
    }
}
