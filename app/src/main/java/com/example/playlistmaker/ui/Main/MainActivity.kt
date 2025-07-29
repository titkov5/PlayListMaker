package com.example.playlistmaker.ui.Main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.settingsButton.setOnClickListener {
            viewModel.settingsOnClick(this)
        }

        binding.searchButton.setOnClickListener {
            viewModel.searchOnClick(this)
        }
    }
}