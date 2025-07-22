package com.example.playlistmaker.ui

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
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
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlaybackState.Default
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var binding: ActivityPlayerBinding

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
                    playPause()
                }
                val coverUrl = track.artworkUrl100.replaceAfterLast('/',getString(R.string.cover512))
                Glide
                    .with(applicationContext)
                    .load(coverUrl)
                    .placeholder(R.drawable.track_placeholder)
                    .transform(RoundedCorners(dpToPx(8.toFloat())))
                    .into(cover)
            }

            resetCurrentTime()
            prepareMediaPlayer(track.previewUrl)
        }
    }

    private fun resetCurrentTime() {
        binding.trackTimeValue.text = "00:00"
    }

    private fun updateTrackTime() {
        handler.postDelayed( {
            val currentPosition = mediaPlayer.currentPosition
            binding.trackTimeValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
            updateTrackTime()
        },
            REFRESH_TIME_DELAY
        )
    }

    private fun prepareMediaPlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = PlaybackState.Prepared
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacksAndMessages(null)
            resetCurrentTime()
            mediaPlayer.seekTo(0)
            playerState = PlaybackState.Prepared
            binding.iconPlay.setImageResource(R.drawable.play)
        }
    }

    private fun startPlayer() {
        updateTrackTime()
        mediaPlayer.start()
        playerState = PlaybackState.Playing
        binding.apply {
            if (isDarkMode()) {
                iconPlay.setImageResource(R.drawable.pause_night)
            } else {
                iconPlay.setImageResource(R.drawable.pause)
            }
        }
    }

    private fun pausePlayer() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.pause()
        playerState = PlaybackState.Paused
        binding.apply {
            if (isDarkMode()) {
                iconPlay.setImageResource(R.drawable.play_night)
            } else {
                iconPlay.setImageResource(R.drawable.play)
            }
        }
    }

    private fun isDarkMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun playPause() {
        when(playerState) {
            PlaybackState.Playing -> {
                pausePlayer()
            }
            PlaybackState.Prepared, PlaybackState.Paused -> {
                startPlayer()
            }

            PlaybackState.Default -> {

            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            applicationContext.resources.displayMetrics).toInt()
    }

    companion object {
        private const val REFRESH_TIME_DELAY = 300L
    }

}

enum class PlaybackState {
    Default, Prepared,Playing, Paused
}