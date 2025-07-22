package com.example.playlistmaker.ui.Search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.ui.Player.PlayerActivity

import com.google.gson.Gson

class SearchViewModel(): ViewModel(), TracksInteractor.TrackConsumer {
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private lateinit var trackRepository: TracksInteractor
    lateinit var searchHistory: SearchHistory
    lateinit var historyTracksAdapter: TrackAdapter
    lateinit var tracksAdapter: TrackAdapter

    private val searchTextLiveData = MutableLiveData("")
    fun observeSearchText(): LiveData<String> = searchTextLiveData

    private val searchStatusLiveData = MutableLiveData<SearchStatus>(SearchStatus.None)
    fun observeSearchStatus(): LiveData<SearchStatus> = searchStatusLiveData

    fun onCreate(sharedPrefs: SharedPreferences, context: Context) {
        trackRepository = Creator.provideTracksInteractor(context)
        searchHistory = SearchHistory(sharedPrefs)

        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            { track: Track ->
                if (clickDebounce()) {
                    searchHistory.addTrack(track)
                    val displayTrackIntent = Intent(context, PlayerActivity::class.java)
                    val trackAsString = Gson().toJson(track)
                    displayTrackIntent.putExtra("Track", trackAsString)
                    context.startActivity(displayTrackIntent)
                    historyTracksAdapter.notifyDataSetChanged()
                }
            }
        )

        historyTracksAdapter = TrackAdapter(
            searchHistory.tracks,
            { track: Track ->
                val displayTrackIntent = Intent(context, PlayerActivity::class.java)
                val trackAsString = Gson().toJson(track)
                displayTrackIntent.putExtra("Track", trackAsString)
                context.startActivity(displayTrackIntent)
            }
        )
    }

    fun onClear() {
        searchTextLiveData.postValue("")
        tracksAdapter.tracks = emptyList()
        searchStatusLiveData.postValue(SearchStatus.None)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun clearHistory() {
        searchHistory.remove()
        historyTracksAdapter.tracks = emptyList()
        searchStatusLiveData.postValue(SearchStatus.None)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_REQUEST, searchTextLiveData.value)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val savedText = savedInstanceState.getString(SEARCH_REQUEST).toString()
        searchTextLiveData.postValue(savedText)
    }

   private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun setSearchText(searchText: String) {
        if (searchText != searchTextLiveData.value) {
            searchTextLiveData.postValue(searchText)
            searchDebounce()
        }
    }

    private fun searchRequest() {
        if (searchTextLiveData.value?.length ?: 0 > 2) {
            searchStatusLiveData.postValue(SearchStatus.LoadingRequest)
            searchTextLiveData.value?.let { trackRepository.searchTracks(it,this) }
        }
    }

    override fun consumeTracks(foundedTracks: List<Track>?, errorMessage: String?) {
        if (foundedTracks.isNullOrEmpty()) {
            searchStatusLiveData.postValue(SearchStatus.Empty)
        } else {
            searchStatusLiveData.postValue(SearchStatus.Success)
            tracksAdapter.tracks = foundedTracks
        }

        if (errorMessage != null) {
            searchStatusLiveData.postValue(SearchStatus.Failed)
        }
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}

enum class SearchStatus {
    None, LoadingRequest, Empty, Success, Failed
}