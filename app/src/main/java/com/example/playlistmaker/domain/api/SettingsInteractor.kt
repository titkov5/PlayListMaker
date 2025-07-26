package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun getDarkThemeValue(): Boolean
    fun saveAndApplyDarkThemeValue(isDark: Boolean)
}