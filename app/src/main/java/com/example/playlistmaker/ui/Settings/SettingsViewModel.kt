package com.example.playlistmaker.ui.Settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SettingsInteractor


class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
): ViewModel() {
    private val stateLiveData = MutableLiveData(false)
    fun observeState(): LiveData<Boolean> = stateLiveData

    fun checkDarkTheme(checked: Boolean) {
        stateLiveData.postValue(checked)
        settingsInteractor.saveAndApplyDarkThemeValue(checked)
    }

    fun onCreate() {
        stateLiveData.postValue(settingsInteractor.getDarkThemeValue())
    }
}