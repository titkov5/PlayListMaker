package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource

class SearchHistoryRepositoryImpl(
    private val storage: StorageClient<ArrayList<Track>>): SearchHistoryRepository {

    override fun saveToHistory(m: Track) {
        val track = storage.getData() ?: arrayListOf()
        track.add(m)
        storage.storeData(track)
    }

    override fun getHistory(): Resource<List<Track>> {
        val movies = storage.getData() ?: listOf()
        return Resource.Success(movies)
    }

    override fun removeHistory() {
       storage.removeData()
    }
}