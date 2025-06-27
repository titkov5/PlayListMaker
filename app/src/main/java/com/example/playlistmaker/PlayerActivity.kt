package com.example.playlistmaker

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
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        playImageView = findViewById<ImageView>(R.id.icon_play)

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

    fun prepareMediaPlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacksAndMessages(null)
            resetCurrentTime()
            mediaPlayer.seekTo(0)
            playerState = STATE_PREPARED
            playImageView.setImageResource(R.drawable.play )
        }
    }

    fun startPlayer() {
        updateTrackTime()
        mediaPlayer.start()
        playerState = STATE_PLAYING
        playImageView.setImageResource(R.drawable.pause)
    }

    fun pausePlayer() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        playImageView.setImageResource(R.drawable.play)
    }

    fun playPause() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
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
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_TIME_DELAY = 300L
    }
}