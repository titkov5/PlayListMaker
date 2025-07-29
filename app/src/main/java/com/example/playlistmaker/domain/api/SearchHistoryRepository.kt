package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun saveToHistory(m: Track)
    fun getHistory(): Resource<List<Track>>
    fun removeHistory()
}