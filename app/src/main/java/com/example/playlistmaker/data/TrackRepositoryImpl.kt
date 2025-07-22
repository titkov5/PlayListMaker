package com.example.playlistmaker.data

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.Resource
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                return Resource.Error("Проверьте подключение к серверу")
            }

            200 -> {
                val result = (response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.artistName,
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis),
                        it.artworkUrl100,
                        it.previewUrl,
                    )
                }
                return Resource.Success(result)
            }

            else -> {
                return Resource.Error("Ошибка сервера")
            }

        }

    }
}