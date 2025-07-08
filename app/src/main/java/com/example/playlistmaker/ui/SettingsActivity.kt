package com.example.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.DARK_THEME_KEY
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val agreement = findViewById<TextView>(R.id.userAgreement)
        agreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.offerString))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val share = findViewById<TextView>(R.id.shareApp)
        share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.androidRazrab))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        val contactSupport = findViewById<TextView>(R.id.contactSupport)
        contactSupport.setOnClickListener {
            val message = getString(R.string.thanksAll)
            val subject = getString(R.string.messageToAll)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.myMail))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject)
            startActivity(shareIntent)
        }

        val darkThemeSwithcer = findViewById<SwitchMaterial>(R.id.dark_them_swithcher)
        darkThemeSwithcer.isChecked =  (applicationContext as App).darkTheme
        darkThemeSwithcer.setOnCheckedChangeListener { swicher, checked ->
            (applicationContext as App).switchTheme(checked)
            val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
            sharedPrefs.edit().putBoolean(DARK_THEME_KEY, checked).apply()
        }
    }
}