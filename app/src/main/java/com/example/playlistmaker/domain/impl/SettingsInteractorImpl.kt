package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
): SettingsInteractor {
    override fun getDarkThemeValue(): Boolean {
       return repository.getDarkThemeValue()
    }

    override fun saveAndApplyDarkThemeValue(isDark: Boolean) {
        repository.saveAndApplyDarkThemeValue(isDark)
    }
}