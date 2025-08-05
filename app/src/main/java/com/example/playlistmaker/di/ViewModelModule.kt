package com.example.playlistmaker.di

import com.example.playlistmaker.ui.Main.MainViewModel
import com.example.playlistmaker.ui.Player.PlayerViewModel
import com.example.playlistmaker.ui.Search.SearchViewModel
import com.example.playlistmaker.ui.Settings.SettingsViewModel
import com.example.playlistmaker.ui.media.MediaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val viewModelModule = module {
    viewModel{
        PlayerViewModel()
    }

    viewModel {
        SearchViewModel(get(),get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        MainViewModel()
    }

    viewModel{
        MediaViewModel()
    }
}