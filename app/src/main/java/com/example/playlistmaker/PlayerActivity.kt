package com.example.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val extras = intent.extras
        if (extras != null) {
            val trackAsJson = extras.getString("Track")
            val track = Gson().fromJson(trackAsJson, Track::class.java)

            val trackNameTextView = findViewById<TextView>(R.id.track_main_title)
            trackNameTextView.text = track.trackName

            val trackArtistTextView = findViewById<TextView>(R.id.track_subtitle)
            trackArtistTextView.text = track.artistName

            val trackTimeTextView = findViewById<TextView>(R.id.trackTimeValue)
            val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            trackTimeTextView.text = trackTime

            val trackAlumTextView = findViewById<TextView>(R.id.trackAlbumValue)
            trackAlumTextView.text = track.collectionName

            val trackYearTextView = findViewById<TextView>(R.id.trackYearValue)
            trackYearTextView.text = track.releaseDate

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
        }
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            applicationContext.resources.displayMetrics).toInt()
    }
}