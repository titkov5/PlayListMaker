package com.example.playlistmaker.Search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.MainActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.network.NetworkService
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private var currentText = ""

    private var tracksAdapter = TrackAdapter(
        tracks = TracksFactory.getTracks()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        val tracksView = findViewById<RecyclerView>(R.id.tracks_recycle_view)
        tracksView.visibility = View.GONE
        val noConnectionView = findViewById<LinearLayout>(R.id.tracks_no_connection)
        noConnectionView.visibility = View.GONE
        val nothingFoundedView = findViewById<LinearLayout>(R.id.tracks_nothing_founded)
        nothingFoundedView.visibility = View.GONE

        val searchTextEdit = findViewById<EditText>(R.id.search_edit_text)
        searchTextEdit.setText(currentText)
        searchTextEdit.setOnEditorActionListener { textView, actionId, _ ->
            true

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                NetworkService.findTracks(
                    textView.text.toString(),
                    {findedTracks: List<Track> ->
                        if (findedTracks.isNullOrEmpty()) {
                            tracksView.visibility = View.GONE
                            noConnectionView.visibility = View.GONE
                            nothingFoundedView.visibility = View.VISIBLE
                        } else {
                            tracksView.visibility = View.VISIBLE
                            noConnectionView.visibility = View.GONE
                            nothingFoundedView.visibility = View.GONE
                        }
                        tracksAdapter.tracks = findedTracks
                        tracksView.adapter?.notifyDataSetChanged()
                    },
                    failureCompletion = { error: String ->
                        tracksView.visibility = View.GONE
                        noConnectionView.visibility = View.VISIBLE
                        nothingFoundedView.visibility = View.GONE
                    }
                )
                true
            }
            false
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchTextEdit.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchTextEdit.windowToken, 0)
            tracksAdapter.tracks = emptyList()
            tracksView.adapter?.notifyDataSetChanged()
        }
        setupToolBar()

        val retryButton = findViewById<Button>(R.id.retry_button)
        retryButton.setOnClickListener {// TODO:
            NetworkService.findTracks(
                searchTextEdit.text.toString(),
                {findedTracks: List<Track> ->
                    if (findedTracks.isNullOrEmpty()) {
                        tracksView.visibility = View.GONE
                        noConnectionView.visibility = View.GONE
                        nothingFoundedView.visibility = View.VISIBLE
                    } else {
                        tracksView.visibility = View.VISIBLE
                        noConnectionView.visibility = View.GONE
                        nothingFoundedView.visibility = View.GONE
                    }
                    tracksAdapter.tracks = findedTracks
                    tracksView.adapter?.notifyDataSetChanged()
                },
                failureCompletion = { error: String ->
                    tracksView.visibility = View.GONE
                    noConnectionView.visibility = View.VISIBLE
                    nothingFoundedView.visibility = View.GONE
                }
            )
        }

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
        tracksView.layoutManager = LinearLayoutManager(this)
        tracksView.adapter = tracksAdapter
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
