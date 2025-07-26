package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.DarkThemSwitcher

const val DARK_THEME_KEY = "key_for_edit_text"

class App : Application(), DarkThemSwitcher {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        darkTheme = Creator.provideSettingsInteractor(this).getDarkThemeValue()
        switchTheme(darkTheme)
    }

    override fun switchTheme(darkThemeEnabled: Boolean) {
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