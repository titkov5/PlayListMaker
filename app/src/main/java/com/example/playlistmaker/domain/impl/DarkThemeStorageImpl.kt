package com.example.playlistmaker.domain.impl

import android.content.Context
import com.example.playlistmaker.data.storage.PrefsStorageClient
import com.google.gson.reflect.TypeToken

const val DARK_THEME_KEY = "key_for_edit_text"

class DarkThemeStorageImpl(context: Context) : PrefsStorageClient<Boolean>(
    context,
    DARK_THEME_KEY,
    object : TypeToken<Boolean>() {}.type
)

