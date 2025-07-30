package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.DarkThemSwitcher
import com.example.playlistmaker.domain.impl.DarkThemeStorageImpl
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val storage: DarkThemeStorageImpl,
    private val darkThemSwitcher: DarkThemSwitcher): SettingsRepository {

        override fun getDarkThemeValue(): Boolean {
        return storage.getData() ?: false
    }

    override fun saveAndApplyDarkThemeValue(isDark: Boolean) {
        storage.storeData(isDark)
        darkThemSwitcher.switchTheme(isDark)
    }
}