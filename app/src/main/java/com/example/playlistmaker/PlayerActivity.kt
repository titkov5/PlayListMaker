package com.example.playlistmaker

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Search.Track
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()
    private lateinit var playImageView: ImageView
    private lateinit var trackTimeTextView: TextView
    private var playerState = PlaybackState.Default
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        playImageView = findViewById(R.id.icon_play)
        playImageView.setOnClickListener {
            playPause()
        }

        val extras = intent.extras
        if (extras != null) {
            val trackAsJson = extras.getString("Track")
            val track = Gson().fromJson(trackAsJson, Track::class.java)

            val trackNameTextView = findViewById<TextView>(R.id.track_main_title)
            trackNameTextView.text = track.trackName

            val trackArtistTextView = findViewById<TextView>(R.id.track_subtitle)
            trackArtistTextView.text = track.artistName

            trackTimeTextView = findViewById<TextView>(R.id.trackTimeCurrentValue)
            resetCurrentTime()
            val trackAlumTextView = findViewById<TextView>(R.id.trackAlbumValue)
            trackAlumTextView.text = track.collectionName

            val trackYearTextView = findViewById<TextView>(R.id.trackYearValue)

            val release = SimpleDateFormat("yyyy", Locale.getDefault()).parse(track.releaseDate)

            trackYearTextView.text = release.toString()

            val trackGanreTextView = findViewById<TextView>(R.id.trackGanreValue)
            trackGanreTextView.text = track.primaryGenreName

            val trackCountryTextView = findViewById<TextView>(R.id.trackCountryTitleValue)
            trackCountryTextView.text = track.country

            val cover = findViewById<ImageView>(R.id.cover)
            val coverUrl = track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
            Glide
                .with(applicationContext)
                .load(coverUrl)
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(dpToPx(8.toFloat())))
                .into(cover)

            prepareMediaPlayer(track.previewUrl)
        }
    }

    private fun resetCurrentTime() {
        trackTimeTextView.text = "00:00"
    }

    private fun updateTrackTime() {
        handler.postDelayed( {
            val currentPosition = mediaPlayer.currentPosition
            trackTimeTextView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
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
            playImageView.setImageResource(R.drawable.play )
        }
    }

    private fun startPlayer() {
        updateTrackTime()
        mediaPlayer.start()
        playerState =  PlaybackState.Playing
        if (isDarkMode()) {
            playImageView.setImageResource(R.drawable.pause_night)
        } else {
            playImageView.setImageResource(R.drawable.pause)
        }
    }

    private fun pausePlayer() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.pause()
        playerState = PlaybackState.Paused
        if (isDarkMode()) {
            playImageView.setImageResource(R.drawable.play_night)
        } else {
            playImageView.setImageResource(R.drawable.play)
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