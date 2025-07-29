package com.example.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.StorageClient
import com.example.playlistmaker.ui.Search.SEARCH_HISTORY_KEY
import com.google.gson.Gson
import java.lang.reflect.Type

class PrefsStorageClient<T>(
    private val context: Context,
    private val dataKey: String,
    private val type: Type) : StorageClient<T> {

    private val prefs: SharedPreferences = context.getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun storeData(data: T) {
        prefs.edit().putString(dataKey, gson.toJson(data, type)).apply()
    }

    override fun getData(): T? {
        val dataJson = prefs.getString(dataKey, null)
        return if (dataJson == null) {
            null
        } else {
            gson.fromJson(dataJson, type)
        }
    }

    override fun removeData() {
        prefs.edit().remove(dataKey).apply()
    }
}