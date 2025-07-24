package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.models.Track
import com.google.gson.reflect.TypeToken

object Creator {
    const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

    private fun getTracksRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            PrefsStorageClient<ArrayList<Track>>(
                context,
                SEARCH_HISTORY_KEY,
                object : TypeToken<ArrayList<Track>>() {}.type)
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
}