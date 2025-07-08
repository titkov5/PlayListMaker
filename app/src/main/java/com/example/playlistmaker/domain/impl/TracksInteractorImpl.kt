package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteractor

class TracksInteractorImpl(private val trackRepository: TrackRepository): TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        val t = Thread {
            consumer.consumeTracks(trackRepository.searchTracks(expression))
        }

        t.start()
    }
}