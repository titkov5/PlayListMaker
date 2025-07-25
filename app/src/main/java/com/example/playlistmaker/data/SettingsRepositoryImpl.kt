package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val storage: StorageClient<Boolean>): SettingsRepository {
    override fun getDarkThemeValue(): Boolean {
        return storage.getData() ?: false
    }

    override fun saveDarkThemeValue(isDark: Boolean) {
        storage.storeData(isDark)
    }
}