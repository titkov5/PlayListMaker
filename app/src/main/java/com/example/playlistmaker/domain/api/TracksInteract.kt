package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteract {
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
       fun consumeTracks(foundedTracks: List<Track>)
    }
}