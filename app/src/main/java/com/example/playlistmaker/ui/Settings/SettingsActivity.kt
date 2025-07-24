package com.example.playlistmaker.ui.Settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.DARK_THEME_KEY
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.userAgreement.setOnClickListener {
            viewModel.userAgreementOnClick(this)
        }

        binding.shareApp.setOnClickListener {
            viewModel.shareAppOnClick(this)
        }

        binding.contactSupport.setOnClickListener {
            viewModel.contactSupportOnClick(this)
        }

        binding.darkThemSwithcher.isChecked =  (applicationContext as App).darkTheme
        binding.darkThemSwithcher.setOnCheckedChangeListener { _, checked ->
            viewModel.checkDarkTheme(checked,this, (applicationContext as App))
        }
    }
}