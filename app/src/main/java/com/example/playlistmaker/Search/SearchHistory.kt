package com.example.playlistmaker.Search

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

class SearchHistory(
    val sharedPrefs: SharedPreferences
) {
    var tracks = mutableListOf<Track>()

    init {
        tracks = restore()
    }

    fun shouldDisplay(): Boolean {
        return tracks.count() > 0
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
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()

    }

    private fun restore(): MutableList<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return mutableListOf()
        return Gson().fromJson(json, Array<Track>::class.java).toMutableList()
    }

    fun remove() {
        tracks = mutableListOf()
        save()
    }
}