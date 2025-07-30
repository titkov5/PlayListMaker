package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TrackApiService
import com.example.playlistmaker.domain.api.DarkThemSwitcher
import com.example.playlistmaker.domain.impl.DarkThemeStorageImpl
import com.example.playlistmaker.domain.impl.TrackStorageImpl
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module {
    single<TrackApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApiService::class.java) //?
    }

    factory { Gson() }

    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }


    single<DarkThemSwitcher> {
        App()
    }

    single<TrackStorageImpl> {
       TrackStorageImpl(get())
    }


    single<DarkThemeStorageImpl> {
        DarkThemeStorageImpl(get())
    }
}
