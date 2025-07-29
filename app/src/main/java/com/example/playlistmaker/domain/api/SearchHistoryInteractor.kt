package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(consumer: HistoryConsumer)
    fun saveToHistory(m: Track)
    fun removeHistory()

    interface HistoryConsumer {
        fun consume(searchHistory: List<Track>?)
    }
}