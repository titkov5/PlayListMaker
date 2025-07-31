package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.util.Resource

class TracksInteractorImpl(private val trackRepository: TrackRepository): TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        val t = Thread {
            when(val resource = trackRepository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consumeTracks(resource.data, null) }
                is Resource.Error -> { consumer.consumeTracks(null, resource.message) }
            }
        }

        t.start()
    }
}