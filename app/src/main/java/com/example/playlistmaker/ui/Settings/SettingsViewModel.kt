package com.example.playlistmaker.ui.Settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.Creator


class SettingsViewModel: ViewModel() {
    private val stateLiveData = MutableLiveData(false)
    fun observeState(): LiveData<Boolean> = stateLiveData

    fun checkDarkTheme(checked: Boolean, context: Context, app: App) {
        stateLiveData.postValue(checked)
        app.switchTheme(checked)
        Creator.provideSettingsInteractor(context).saveDarkThemeValue(checked)
    }

    fun onCreate(context: Context) {
        stateLiveData.postValue(Creator.provideSettingsInteractor(context).getDarkThemeValue())
    }
}