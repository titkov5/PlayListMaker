package com.example.playlistmaker.ui.Player

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel: ViewModel() {
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playbackStateLiveData = MutableLiveData(PlaybackState.Default)
    fun observePlaybackState(): LiveData<PlaybackState> = playbackStateLiveData

    private val playerPositionLiveData = MutableLiveData("")
    fun observerPlayerPosition(): MutableLiveData<String> = playerPositionLiveData

    fun playPause() {
        when(playbackStateLiveData.value) {
            PlaybackState.Playing -> {
                pausePlayer()
            }
            else -> {
                startPlayer()
            }
        }
    }

    private fun updateTrackTime() {
        handler.postDelayed( {
            val currentPosition = mediaPlayer.currentPosition
            val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
            playerPositionLiveData.postValue(time)
            updateTrackTime()
        },
            REFRESH_TIME_DELAY
        )
    }

    fun prepareMediaPlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playbackStateLiveData.postValue(PlaybackState.Prepared)
        }

        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacksAndMessages(null)
            playerPositionLiveData.postValue("00:00")
            mediaPlayer.seekTo(0)
            playbackStateLiveData.postValue(PlaybackState.Prepared)
        }
    }

    private fun startPlayer() {
        updateTrackTime()
        mediaPlayer.start()
        playbackStateLiveData.postValue(PlaybackState.Playing)

    }
fun onDestroy() {
    mediaPlayer.release()
    handler.removeCallbacksAndMessages(null)
}
    fun pausePlayer() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.pause()
        playbackStateLiveData.postValue(PlaybackState.Paused)
    }

    companion object {
        private const val REFRESH_TIME_DELAY = 300L
    }
}

enum class PlaybackState {
    Default, Prepared, Playing, Paused
}