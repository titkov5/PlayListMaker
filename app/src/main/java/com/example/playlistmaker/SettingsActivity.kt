package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
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
    }
}