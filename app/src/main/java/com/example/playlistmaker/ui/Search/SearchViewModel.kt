package com.example.playlistmaker.ui.Search

import android.content.Context
import android.content.Intent
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
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.ui.Player.PlayerActivity

import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

class SearchViewModel(
): ViewModel(), TracksInteractor.TrackConsumer, SearchHistoryInteractor.HistoryConsumer {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private lateinit var trackRepository: TracksInteractor
    lateinit var historyTracksAdapter: TrackAdapter
    lateinit var tracksAdapter: TrackAdapter
    private var tracks = mutableListOf<Track>()
    lateinit var historyInteractor: SearchHistoryInteractor
    private val searchScreenStateLiveData = MutableLiveData(SearchScreenState("",SearchStatus.None))
    fun observeSearchScreenState(): LiveData<SearchScreenState> = searchScreenStateLiveData

    fun onCreate(context: Context) {
        historyInteractor = Creator.provideSearchHistoryInteractor(context)
        trackRepository = Creator.provideTracksInteractor(context)

        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            { track: Track ->
                if (clickDebounce()) {
                    addTrack(track)
                    val displayTrackIntent = Intent(context, PlayerActivity::class.java)
                    val trackAsString = Gson().toJson(track)
                    displayTrackIntent.putExtra("Track", trackAsString)
                    context.startActivity(displayTrackIntent)
                    historyTracksAdapter.notifyDataSetChanged()
                }
            }
        )
        historyTracksAdapter = TrackAdapter(
            tracks,
            { track: Track ->
                val displayTrackIntent = Intent(context, PlayerActivity::class.java)
                val trackAsString = Gson().toJson(track)
                displayTrackIntent.putExtra("Track", trackAsString)
                context.startActivity(displayTrackIntent)
            }
        )

        historyInteractor.getHistory(this)
    }


    fun onClear() {
        val defaultState = SearchScreenState("",SearchStatus.None)
        searchScreenStateLiveData.postValue(defaultState)
        tracksAdapter.tracks = emptyList()
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    fun onFocusChanged() {
        historyTracksAdapter.tracks = tracks
        historyTracksAdapter.notifyDataSetChanged()
    }

    fun clearHistory() {
        remove()
        historyTracksAdapter.tracks = emptyList()
        updateSate(null,SearchStatus.None )
    }

    private fun updateSate(text: String?, status: SearchStatus?) {
        val currentState = searchScreenStateLiveData.value
        val newState = if (currentState != null) {
            SearchScreenState(
                text ?: currentState.text,
                status ?: currentState.status
            )
        } else {
            SearchScreenState(
                text ?: "",
                status ?: SearchStatus.None
            )
        }
        searchScreenStateLiveData.postValue(newState)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_REQUEST, searchScreenStateLiveData.value?.text ?: "")
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val savedText = savedInstanceState.getString(SEARCH_REQUEST).toString()
        updateSate(savedText, null)
    }

   private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun setSearchText(searchText: String) {
        if (searchText != (searchScreenStateLiveData.value?.text ?: "")) {
            updateSate(searchText, null)
            searchDebounce()
        }
    }

    private fun searchRequest() {
        val searchText = searchScreenStateLiveData.value?.text ?: ""
        if (searchText.length > 2) {
            updateSate(null,SearchStatus.LoadingRequest)
            trackRepository.searchTracks(searchText,this)
        }
    }

    override fun consumeTracks(foundedTracks: List<Track>?, errorMessage: String?) {
        if (foundedTracks.isNullOrEmpty()) {
            updateSate(null,SearchStatus.Empty)
        } else {
            updateSate(null,SearchStatus.Success)
            tracksAdapter.tracks = foundedTracks
        }

        if (errorMessage != null) {
            updateSate(null,SearchStatus.Failed)
        }
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    fun shouldDisplayHistory(): Boolean {
        val diff = tracks.isNotEmpty()
        return diff
    }

    fun addTrack(selectedTrack: Track) {
        if (tracks.isEmpty()) {
            tracks.add(selectedTrack)
        }

        if (tracks.count() >= 10) {
            tracks.removeAt(tracks.lastIndex)
        }

        tracks.removeIf { it.trackId == selectedTrack.trackId}
        tracks.add(0, selectedTrack)

        save()
    }

    private fun save() {
        historyInteractor.removeHistory()
        tracks.map { historyInteractor.saveToHistory(it) }
    }

    fun remove() {
        tracks = mutableListOf()
        historyInteractor.removeHistory()
    }

    override fun consume(searchHistory: List<Track>?) {
        if (searchHistory != null) {
            tracks = searchHistory.toMutableList()
            historyTracksAdapter.tracks = tracks
            historyTracksAdapter.notifyDataSetChanged()
        }
    }
}

enum class SearchStatus {
    None, LoadingRequest, Empty, Success, Failed
}