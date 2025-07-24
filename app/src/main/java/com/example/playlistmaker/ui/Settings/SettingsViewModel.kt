package com.example.playlistmaker.ui.Settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.DARK_THEME_KEY
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.R

class SettingsViewModel: ViewModel() {

    fun checkDarkTheme(checked: Boolean, context: Context, app: App) {
        app.switchTheme(checked)
        val sharedPrefs = context.getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, checked).apply()
    }

    fun contactSupportOnClick(context: Context) {
        val message = context.getString(R.string.thanksAll)
        val subject = context.getString(R.string.messageToAll)
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.myMail))
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject)
        context.startActivity(shareIntent)
    }

    fun shareAppOnClick(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.androidRazrab))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun userAgreementOnClick(context: Context) {
        val uri = Uri.parse(context.getString(R.string.offerString))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }
}