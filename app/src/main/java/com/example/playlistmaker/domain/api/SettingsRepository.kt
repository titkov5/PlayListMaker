package com.example.playlistmaker.domain.api

import android.content.Context

interface SettingsRepository {
    fun getDarkThemeValue(): Boolean
    fun saveAndApplyDarkThemeValue(isDark: Boolean)
}