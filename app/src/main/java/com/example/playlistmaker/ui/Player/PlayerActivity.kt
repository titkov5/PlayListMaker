package com.example.playlistmaker.ui.Player

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val extras = intent.extras
        if (extras != null) {
            val trackAsJson = extras.getString("Track")
            val track = Gson().fromJson(trackAsJson, Track::class.java)
            binding.apply {
                trackMainTitle.text = track.trackName
                trackSubtitle.text = track.artistName
                trackAlbumValue.text = track.collectionName
                trackYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).parse(track.releaseDate).toString()
                trackCountryTitleValue.text = track.country
                trackGanreValue.text = track.primaryGenreName
                iconPlay.setOnClickListener {
                   viewModel.playPause()
                }
                val coverUrl = track.artworkUrl100.replaceAfterLast('/',getString(R.string.cover512))
                Glide
                    .with(applicationContext)
                    .load(coverUrl)
                    .placeholder(R.drawable.track_placeholder)
                    .transform(RoundedCorners(dpToPx(8.toFloat())))
                    .into(cover)
            }

            viewModel.prepareMediaPlayer(track.previewUrl)
        }

        viewModel.observePlaybackState().observe(this) {
            when (it) {
                PlaybackState.Playing -> {
                    binding.apply {
                        if (isDarkMode()) {
                            iconPlay.setImageResource(R.drawable.pause_night)
                        } else {
                            iconPlay.setImageResource(R.drawable.pause)
                        }
                    }
                }
                else -> {
                    binding.apply {
                        if (isDarkMode()) {
                            iconPlay.setImageResource(R.drawable.play_night)
                        } else {
                            iconPlay.setImageResource(R.drawable.play)
                        }
                    }
                }
            }
        }

        viewModel.observerPlayerPosition().observe(this) {
            binding.trackTimeValue.text = it
        }
    }

    private fun isDarkMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            applicationContext.resources.displayMetrics).toInt()
    }
}

