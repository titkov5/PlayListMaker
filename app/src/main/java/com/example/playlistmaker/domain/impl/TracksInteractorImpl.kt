package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteract

class TracksInteractorImpl(private val trackRepository: TrackRepository): TracksInteract {
    override fun searchTracks(expression: String, consumer: TracksInteract.TrackConsumer) {
        val t = Thread {
            consumer.consumeTracks(trackRepository.searchTracks(expression))
        }

        t.start()
    }
}