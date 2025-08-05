package com.example.playlistmaker.ui.Main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media.MediaActivity
import com.example.playlistmaker.ui.Search.SearchActivity
import com.example.playlistmaker.ui.Settings.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.settingsButton.setOnClickListener {
            viewModel.settingsOnClick()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            this.startActivity(displayIntent)
        }

        binding.searchButton.setOnClickListener {
            viewModel.searchOnClick()
            val displaySearchIntent = Intent(this, SearchActivity::class.java)
            this.startActivity(displaySearchIntent)
        }

        binding.libraryButton.setOnClickListener {
            val displaySearchIntent = Intent(this, MediaActivity::class.java)
            this.startActivity(displaySearchIntent)
        }
    }
}