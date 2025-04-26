package com.example.playlistmaker.Search

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import java.util.Locale

class TrackViewHolder(item: View): RecyclerView.ViewHolder(item) {
    private val title: TextView
    private val subTitle: TextView
    private val image: ImageView
    private val applicationContext: View

    init {
        title = item.findViewById<TextView>(R.id.track_cell_title)
        subTitle = item.findViewById<TextView>(R.id.track_cell_sub_title)
        image = item.findViewById<ImageView>(R.id.track_cell_image)
        applicationContext = item
    }

    fun bind(model: Track) {
        title.text = model.artistName
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        subTitle.text = model.trackName + " \u2022 " + trackTime
        Glide
            .with(applicationContext)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2.toFloat(), applicationContext.context)))
            .into(image)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}