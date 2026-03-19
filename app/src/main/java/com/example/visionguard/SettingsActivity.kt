package com.example.visionguard

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        // Settings
        const val PREFS = "vision_guard_settings"
        const val KEY_PARENTAL = "parental_mode"
        const val KEY_DISTANCE = "safe_distance"
        const val KEY_SCREEN_LIMIT = "screen_limit"

        // PIN
        private const val PIN_PREF = "vision_guard_pin"
        private const val KEY_PIN = "pin_code"
    }

    private lateinit var pinLayout: LinearLayout
    private lateinit var settingsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val pinPrefs = getSharedPreferences(PIN_PREF, MODE_PRIVATE)
        val settingsPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var savedPin = pinPrefs.getString(KEY_PIN, null)

        // ===== UI ELEMENTS =====
        pinLayout = findViewById(R.id.pin_layout)
        val pinTitle = findViewById<TextView>(R.id.pin_title)
        val pinInput = findViewById<EditText>(R.id.pin_input)
        val pinButton = findViewById<Button>(R.id.pin_button)
        val pinMessage = findViewById<TextView>(R.id.pin_message)
        settingsLayout = findViewById(R.id.settings_layout)

        val parentalSwitch = findViewById<Switch>(R.id.parental_switch)
        val distanceSpinner = findViewById<Spinner>(R.id.distance_spinner)
        val screenTimeSpinner = findViewById<Spinner>(R.id.screen_time_spinner)
        val changePinButton = findViewById<Button>(R.id.change_pin_button)
        val saveButton = findViewById<Button>(R.id.save_settings_button)

        // ===== SETUP SPINNERS =====
        val distances = arrayOf("25 cm", "30 cm (Recommended)", "35 cm")
        distanceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distances)

        val times = arrayOf("No Limit", "15 minutes", "30 minutes", "1 hour")
        screenTimeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times)

        // ===== SETUP PIN UI =====
        pinTitle.text = if (savedPin == null) "Create 4-Digit PIN" else "Enter PIN to Access Settings"
        pinButton.text = if (savedPin == null) "Create PIN" else "Unlock"

        // ===== TEMP VALUES (saved only on Save) =====
        var tempParental = settingsPrefs.getBoolean(KEY_PARENTAL, false)
        var tempDistance = settingsPrefs.getInt(KEY_DISTANCE, 30)
        var tempScreenLimit = settingsPrefs.getInt(KEY_SCREEN_LIMIT, 0)

        parentalSwitch.isChecked = tempParental
        distanceSpinner.setSelection(when (tempDistance) {
            25 -> 0
            35 -> 2
            else -> 1
        })
        screenTimeSpinner.setSelection(when (tempScreenLimit) {
            15 -> 1
            30 -> 2
            60 -> 3
            else -> 0
        })

        // ===== LISTENERS =====
        parentalSwitch.setOnCheckedChangeListener { _, v ->
            tempParental = v
        }

        distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tempDistance = when (position) {
                    0 -> 25
                    2 -> 35
                    else -> 30
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        screenTimeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tempScreenLimit = when (position) {
                    1 -> 15
                    2 -> 30
                    3 -> 60
                    else -> 0
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ===== PIN BUTTON =====
        pinButton.setOnClickListener {
            val enteredPin = pinInput.text.toString()

            if (enteredPin.length != 4) {
                pinMessage.text = "PIN must be 4 digits"
                return@setOnClickListener
            }

            if (savedPin == null) {
                pinPrefs.edit().putString(KEY_PIN, enteredPin).apply()
                savedPin = enteredPin
                unlockSettings()
            } else if (enteredPin == savedPin) {
                unlockSettings()
            } else {
                pinMessage.text = "Incorrect PIN"
            }
        }

        // ===== CHANGE PIN BUTTON =====
        changePinButton.setOnClickListener {
            showChangePinDialog(pinPrefs) {
                savedPin = it
            }
        }

        // ===== SAVE SETTINGS =====
        saveButton.setOnClickListener {
            settingsPrefs.edit()
                .putBoolean(KEY_PARENTAL, tempParental)
                .putInt(KEY_DISTANCE, tempDistance)
                .putInt(KEY_SCREEN_LIMIT, tempScreenLimit)
                .apply()

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun unlockSettings() {
        pinLayout.visibility = View.GONE
        settingsLayout.visibility = View.VISIBLE
    }

    private fun showChangePinDialog(
        prefs: android.content.SharedPreferences,
        onPinChanged: (String) -> Unit
    ) {
        val dialogLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val oldPin = EditText(this).apply {
            hint = "Old PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        val newPin = EditText(this).apply {
            hint = "New PIN"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        dialogLayout.addView(oldPin)
        dialogLayout.addView(newPin)

        android.app.AlertDialog.Builder(this)
            .setTitle("Change PIN")
            .setView(dialogLayout)
            .setPositiveButton("Change") { _, _ ->
                val savedPin = prefs.getString(KEY_PIN, "")
                if (oldPin.text.toString() == savedPin && newPin.text.toString().length == 4) {
                    prefs.edit().putString(KEY_PIN, newPin.text.toString()).apply()
                    onPinChanged(newPin.text.toString())
                    Toast.makeText(this, "PIN Updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "PIN Change Failed", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
