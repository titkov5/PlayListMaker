package com.example.playlistmaker.ui.Search

import com.example.playlistmaker.domain.models.Track

data class SearchScreenState(
    val text: String,
    val status: SearchStatus,
    val tracks: List<Track>,
    val historyTracks: List<Track>
)