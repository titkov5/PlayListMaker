package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PRACTICUM_EXAMPLE_PREFERENCES = "practicum_example_preferences"
const val DARK_THEME_KEY = "key_for_edit_text"

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}