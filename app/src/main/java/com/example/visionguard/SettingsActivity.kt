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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pinPrefs = getSharedPreferences(PIN_PREF, MODE_PRIVATE)
        val settingsPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var savedPin = pinPrefs.getString(KEY_PIN, null)

        // ================= ROOT =================
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        // ================= PIN UI =================
        val pinTitle = TextView(this).apply {
            text = if (savedPin == null)
                "Create 4-Digit PIN"
            else
                "Enter PIN to Access Settings"
            textSize = 20f
        }

        val pinInput = EditText(this).apply {
            hint = "Enter PIN"
            inputType =
                InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        val pinButton = Button(this).apply {
            text = if (savedPin == null) "Create PIN" else "Unlock"
        }

        val pinMessage = TextView(this)

        // ================= SETTINGS UI =================
        val settingsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }

        // Temp values (saved only on Save)
        var tempParental = settingsPrefs.getBoolean(KEY_PARENTAL, false)
        var tempDistance = settingsPrefs.getInt(KEY_DISTANCE, 30)
        var tempScreenLimit = settingsPrefs.getInt(KEY_SCREEN_LIMIT, 0)

        // ---- Parental Mode ----
        val parentalSwitch = Switch(this).apply {
            text = "Enable Parental Mode"
            isChecked = tempParental
        }
        parentalSwitch.setOnCheckedChangeListener { _, v ->
            tempParental = v
        }

        // ---- Safe Distance ----
        val distanceSpinner = Spinner(this)
        val distances = arrayOf("25 cm", "30 cm (Recommended)", "35 cm")
        distanceSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distances)

        distanceSpinner.setSelection(
            when (tempDistance) {
                25 -> 0
                35 -> 2
                else -> 1
            }
        )

        distanceSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    tempDistance = when (position) {
                        0 -> 25
                        2 -> 35
                        else -> 30
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // ---- Screen Time ----
        val timeSpinner = Spinner(this)
        val times = arrayOf("No Limit", "15 minutes", "30 minutes", "1 hour")
        timeSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, times)

        timeSpinner.setSelection(
            when (tempScreenLimit) {
                15 -> 1
                30 -> 2
                60 -> 3
                else -> 0
            }
        )

        timeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    tempScreenLimit = when (position) {
                        1 -> 15
                        2 -> 30
                        3 -> 60
                        else -> 0
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // ---- CHANGE PIN ----
        val changePinButton = Button(this).apply {
            text = "Change PIN"
        }

        changePinButton.setOnClickListener {
            showChangePinDialog(pinPrefs) {
                savedPin = it
            }
        }

        // ---- SAVE SETTINGS ----
        val saveButton = Button(this).apply {
            text = "Save Settings"
        }

        saveButton.setOnClickListener {
            settingsPrefs.edit()
                .putBoolean(KEY_PARENTAL, tempParental)
                .putInt(KEY_DISTANCE, tempDistance)
                .putInt(KEY_SCREEN_LIMIT, tempScreenLimit)
                .apply()

            Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Add views
        settingsLayout.addView(parentalSwitch)
        settingsLayout.addView(TextView(this).apply { text = "Safe Distance" })
        settingsLayout.addView(distanceSpinner)
        settingsLayout.addView(TextView(this).apply { text = "Screen Time Limit" })
        settingsLayout.addView(timeSpinner)
        settingsLayout.addView(changePinButton)
        settingsLayout.addView(saveButton)

        // ================= PIN LOGIC =================
        pinButton.setOnClickListener {
            val enteredPin = pinInput.text.toString()

            if (enteredPin.length != 4) {
                pinMessage.text = "PIN must be 4 digits"
                return@setOnClickListener
            }

            if (savedPin == null) {
                pinPrefs.edit().putString(KEY_PIN, enteredPin).apply()
                savedPin = enteredPin
                unlockSettings(pinTitle, pinInput, pinButton, settingsLayout)
            } else if (enteredPin == savedPin) {
                unlockSettings(pinTitle, pinInput, pinButton, settingsLayout)
            } else {
                pinMessage.text = "Incorrect PIN"
            }
        }

        root.addView(pinTitle)
        root.addView(pinInput)
        root.addView(pinButton)
        root.addView(pinMessage)
        root.addView(settingsLayout)

        setContentView(root)
    }

    // ================= HELPERS =================

    private fun unlockSettings(
        title: View,
        input: View,
        button: View,
        settings: View
    ) {
        title.visibility = View.GONE
        input.visibility = View.GONE
        button.visibility = View.GONE
        settings.visibility = View.VISIBLE
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
            inputType =
                InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        val newPin = EditText(this).apply {
            hint = "New PIN"
            inputType =
                InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        dialogLayout.addView(oldPin)
        dialogLayout.addView(newPin)

        android.app.AlertDialog.Builder(this)
            .setTitle("Change PIN")
            .setView(dialogLayout)
            .setPositiveButton("Change") { _, _ ->
                val savedPin = prefs.getString(KEY_PIN, "")
                if (oldPin.text.toString() == savedPin &&
                    newPin.text.toString().length == 4
                ) {
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
