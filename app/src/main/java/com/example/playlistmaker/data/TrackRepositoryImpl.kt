package com.example.playlistmaker.data

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TrackSearchResponse
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient): TrackRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        if (response.resultCode == 200) { //TODO: HTTP codes
            return (response as TrackSearchResponse).results.map {
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
        } else {
            return emptyList()
        }
    }

}