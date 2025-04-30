package com.example.playlistmaker.Search

import android.app.Activity
import android.content.SharedPreferences
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

class SearchHistory(
    val sharedPrefs: SharedPreferences,
    val activity: Activity
) {
    private var tracks = mutableListOf<Track>()
    private var searchHistoryView: LinearLayout
    private var historyTracksView: RecyclerView
    private var tracksAdapter:TrackAdapter
    private var clearHistoryButton: Button

    init {
        searchHistoryView = activity.findViewById(R.id.history_of_search)

        historyTracksView = activity.findViewById(R.id.history_tracks_recycle_view)
        historyTracksView.layoutManager = LinearLayoutManager(activity)
        tracks = restore()
        tracksAdapter = TrackAdapter(
            tracks,
            {}
        )
        historyTracksView.adapter = tracksAdapter

        clearHistoryButton = activity.findViewById(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            remove()
        }
    }

    fun display() {
        if (tracks.count() > 0) {
            searchHistoryView.isVisible = true
        }
    }

    fun hide() {
        searchHistoryView.isVisible = false
    }

    fun addTrack(selectedTrack: Track) {
        if (tracks.count() == 0) {
            tracks.add(selectedTrack)
        } else if (tracks.count() > 10) {
            tracks.removeAt(tracks.lastIndex)
        } else {
            tracks.removeIf { it.trackId == selectedTrack.trackId}
            tracks.add(0, selectedTrack)
        }
        save()
    }

    private fun save() {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()

    }

    private fun restore(): MutableList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    private fun remove() {
        tracks = mutableListOf()
        tracksAdapter.tracks = emptyList()
        historyTracksView.adapter?.notifyDataSetChanged()
        save()
        hide()
    }
}