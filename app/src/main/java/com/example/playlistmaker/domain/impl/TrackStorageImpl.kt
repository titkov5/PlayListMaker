package com.example.playlistmaker.domain.impl

import android.content.Context
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.example.playlistmaker.domain.models.Track
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY_KEY = "key_for_search_tracks"

class TrackStorageImpl(context: Context) : PrefsStorageClient<ArrayList<Track>>(
context,
SEARCH_HISTORY_KEY,
object : TypeToken<ArrayList<Track>>() {}.type
)