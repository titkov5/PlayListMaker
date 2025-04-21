package com.example.playlistmaker.Search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.MainActivity
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var currentText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        val searchTextEdit = findViewById<EditText>(R.id.search_edit_text)
        searchTextEdit.setText(currentText)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchTextEdit.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchTextEdit.windowToken, 0)
        }
        setupToolBar()

        val searchTextWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                //Empty
            }
        }
        searchTextEdit.addTextChangedListener(searchTextWatcher)

       val tracksView = findViewById<RecyclerView>(R.id.tracks_recycle_view)
        tracksView.layoutManager = LinearLayoutManager(this)
        tracksView.adapter = TrackAdapter(
            tracks = TracksFactory.getTracks()
        )
    }

    private fun setupToolBar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
        toolbar.setNavigationOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST,currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(SEARCH_REQUEST)
        val searchTextEdit = findViewById<EditText>(R.id.search_edit_text)
        searchTextEdit.setText(currentText)
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_TEXT"
    }
}

class TrackViewHolder(item: View): RecyclerView.ViewHolder(item) {
    private val title: TextView
    private val subTitle: TextView
    private val image: ImageView

    init {
        title = item.findViewById<TextView>(R.id.track_cell_title)
        subTitle = item.findViewById<TextView>(R.id.track_cell_sub_title)
        image = item.findViewById<ImageView>(R.id.track_cell_image)
    }

    fun bind(model: Track) {
        title.text = model.artistName
        subTitle.text = model.trackName + " \\u2022 " +  model.trackTime
        //сделать загрузку имаджа
    }
}

class TrackAdapter(
    private val tracks: List<Track>
):RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

}