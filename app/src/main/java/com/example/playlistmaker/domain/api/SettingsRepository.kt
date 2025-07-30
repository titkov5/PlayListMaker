package com.example.playlistmaker.domain.api


interface SettingsRepository {
    fun getDarkThemeValue(): Boolean
    fun saveAndApplyDarkThemeValue(isDark: Boolean)
}