package com.example.playlistmaker.ui.Search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track

const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

class SearchViewModel(
): ViewModel(), TracksInteractor.TrackConsumer, SearchHistoryInteractor.HistoryConsumer {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private lateinit var trackRepository: TracksInteractor
    private lateinit var historyInteractor: SearchHistoryInteractor
    private val stateLiveData = MutableLiveData(
        SearchScreenState(
            "",SearchStatus.None, emptyList(), emptyList()
    ))
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    fun onCreate(context: Context) {
        historyInteractor = Creator.provideSearchHistoryInteractor(context)
        trackRepository = Creator.provideTracksInteractor(context)
        historyInteractor.getHistory(this)
    }

    fun onClear() {
        updateSate("",SearchStatus.None, emptyList(), null)
    }

    fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun clearHistory() {
        updateSate(null, null, null, emptyList())
        historyInteractor.removeHistory()
    }

    private fun updateSate(
        text: String?,
        status: SearchStatus?,
        tracks: List<Track>?,
        historyTracks: List<Track>?) {
        val currentState = stateLiveData.value
        val newState = if (currentState != null) {
            SearchScreenState(
                text ?: currentState.text,
                status ?: currentState.status,
                tracks ?: currentState.tracks,
                historyTracks ?: currentState.historyTracks
            )
        } else {
            SearchScreenState(
                text ?: "",
                status ?: SearchStatus.None,
                tracks ?: emptyList(),
                historyTracks ?: emptyList()
            )
        }
        stateLiveData.postValue(newState)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_REQUEST, stateLiveData.value?.text ?: "")
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val savedText = savedInstanceState.getString(SEARCH_REQUEST).toString()
        updateSate(savedText, null, null,null)
    }

   private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun setSearchText(searchText: String) {
        if (searchText != (stateLiveData.value?.text ?: "")) {
            updateSate(searchText, null, null, null)
            searchDebounce()
        }
    }

    private fun searchRequest() {
        val searchText = stateLiveData.value?.text ?: ""
        if (searchText.length > 2) {
            updateSate(null,SearchStatus.LoadingRequest, null, null)
            trackRepository.searchTracks(searchText,this)
        }
    }

    override fun consumeTracks(foundedTracks: List<Track>?, errorMessage: String?) {
        if (foundedTracks.isNullOrEmpty()) {
            updateSate(null,SearchStatus.Empty, null, null)
        } else {
            updateSate(null,SearchStatus.Success, foundedTracks, null)
        }

        if (errorMessage != null) {
            updateSate(null,SearchStatus.Failed, null, null)
        }
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun shouldDisplayHistory(): Boolean {
        return stateLiveData.value?.historyTracks?.isNotEmpty() ?: false
    }

    fun addTrack(selectedTrack: Track) {
        val tracks = (stateLiveData.value?.historyTracks ?: emptyList()).toMutableList()

        if (tracks.isEmpty()) {
            tracks.add(selectedTrack)
        }

        if (tracks.count() >= 10) {
            tracks.removeAt(tracks.lastIndex)
        }

        tracks.removeIf { it.trackId == selectedTrack.trackId}
        tracks.add(0, selectedTrack)

        val newTracks = tracks.toList()
        updateSate(null, null, null, newTracks)

        historyInteractor.removeHistory()
        newTracks.map { historyInteractor.saveToHistory(it) }
    }

    override fun consume(searchHistory: List<Track>?) {
        if (searchHistory != null) {
            updateSate(null,null,null, searchHistory)
        }
    }
}

enum class SearchStatus {
    None, LoadingRequest, Empty, Success, Failed
}