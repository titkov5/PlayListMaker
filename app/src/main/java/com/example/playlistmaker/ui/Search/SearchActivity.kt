package com.example.playlistmaker.ui.Search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.MainActivity
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.ui.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class SearchActivity : AppCompatActivity(), TracksInteractor.TrackConsumer {
    private var currentText = ""
    private lateinit var tracksView: RecyclerView
    private lateinit var noConnectionView: LinearLayout
    private lateinit var nothingFoundedView: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var searchTextEdit: EditText
    private lateinit var clearButton: ImageView
    private lateinit var retryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var tracksAdapter: TrackAdapter
    private lateinit var searchHistoryView: LinearLayout
    private lateinit var historyClearButton: Button
    private lateinit var historyTracksView: RecyclerView
    private lateinit var historyTracksAdapter: TrackAdapter
    private var isClickAllowed = true
    private var trackRepository = Creator.provideTracksInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        tracksView = findViewById(R.id.tracks_recycle_view)
        tracksView.isVisible = false

        noConnectionView = findViewById(R.id.tracks_no_connection)
        noConnectionView.isVisible = false

        nothingFoundedView = findViewById(R.id.tracks_nothing_founded)
        nothingFoundedView.isVisible = false
        searchHistoryView = findViewById(R.id.history_of_search)

        searchTextEdit = findViewById(R.id.search_edit_text)
        searchTextEdit.setText(currentText)

        progressBar = findViewById(R.id.progressBar)

        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            { track: Track ->
                if (clickDebounce()) {
                    searchHistory.addTrack(track)
                    historyTracksView.adapter?.notifyDataSetChanged()
                    val displayTrackIntent = Intent(this, PlayerActivity::class.java)
                    val trackAsString = Gson().toJson(track)
                    displayTrackIntent.putExtra("Track", trackAsString)
                    startActivity(displayTrackIntent)
                }
            }
        )

        clearButton = findViewById(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchTextEdit.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(searchTextEdit.windowToken, 0)
            tracksAdapter.tracks = emptyList()
            tracksView.adapter?.notifyDataSetChanged()
            searchHistoryView.isVisible = searchHistory.shouldDisplay()
        }
        setupToolBar()

        retryButton = findViewById(R.id.retry_button)
        retryButton.setOnClickListener {
            searchRequest()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.isVisible = !p0.isNullOrEmpty()
                currentText = p0.toString()
                searchHistoryView.isVisible = false

                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {
                //Empty
            }
        }
        searchTextEdit.addTextChangedListener(searchTextWatcher)
        tracksView.layoutManager = LinearLayoutManager(this)
        tracksView.adapter = tracksAdapter

        // History
        searchHistory = SearchHistory(
            getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        )
        searchTextEdit.setOnFocusChangeListener { view, b ->
            searchHistoryView.isVisible = searchHistory.shouldDisplay()
        }

        historyClearButton = findViewById(R.id.clear_history_button)
        historyClearButton.setOnClickListener {
            searchHistory.remove()
            historyTracksAdapter.tracks = emptyList()
            historyTracksView.adapter?.notifyDataSetChanged()
            searchHistoryView.isVisible = false
        }

        historyTracksView = findViewById(R.id.history_tracks_recycle_view)
        historyTracksView.layoutManager = LinearLayoutManager(this)
        historyTracksAdapter = TrackAdapter(
            searchHistory.tracks,
            { track: Track ->
                val displayTrackIntent = Intent(this, PlayerActivity::class.java)
                val trackAsString = Gson().toJson(track)
                displayTrackIntent.putExtra("Track", trackAsString)
                startActivity(displayTrackIntent)
            }
        )
        historyTracksView.adapter = historyTracksAdapter
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
        outState.putString(SEARCH_REQUEST, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(SEARCH_REQUEST)
        val searchTextEdit = findViewById<EditText>(R.id.search_edit_text)
        searchTextEdit.setText(currentText)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchRequest() {
        val searchText = searchTextEdit.text.toString()
        if (searchText.length > 2) {
            progressBar.isVisible = true
            trackRepository.searchTracks(searchText,this)

        }
    }

    private fun setUpViewWith(status: SearchStatus) {
        progressBar.isVisible = false
        tracksView.isVisible = status == SearchStatus.Success
        noConnectionView.isVisible = status == SearchStatus.Failed
        nothingFoundedView.isVisible = status == SearchStatus.Empty
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun consumeTracks(foundedTracks: List<Track>) {
        runOnUiThread {
            if (foundedTracks.isNullOrEmpty()) {
                setUpViewWith(SearchStatus.Empty)
            } else {
                setUpViewWith(SearchStatus.Success)
            }
            tracksAdapter.tracks = foundedTracks
            tracksView.adapter?.notifyDataSetChanged()
            //setUpViewWith(SearchStatus.Failed) жду обработки ошибок
        }
    }
}

enum class SearchStatus {
    Empty, Success,Failed
}