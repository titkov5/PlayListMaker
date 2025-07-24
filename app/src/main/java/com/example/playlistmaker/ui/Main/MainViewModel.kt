package com.example.playlistmaker.ui.Main

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.Search.SearchActivity
import com.example.playlistmaker.ui.Settings.SettingsActivity

class MainViewModel: ViewModel() {
    fun settingsOnClick(context: Context) {
        val displayIntent = Intent(context, SettingsActivity::class.java)
        context.startActivity(displayIntent)
    }

    fun searchOnClick(context: Context) {
        val displaySearchIntent = Intent(context, SearchActivity::class.java)
        context.startActivity(displaySearchIntent)
    }
}