package com.example.playlistmaker.ui.Search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.ui.Main.MainActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.ui.Player.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity()  {
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var binding: ActivitySearchBinding
    lateinit var historyTracksAdapter: TrackAdapter
    lateinit var tracksAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClearIcon()
        setupToolBar()
        setupHistoryOfSearch()
        searchEdit()
        setupTracksView()
        setupHistoryTracksView()
        setupViewModel()
    }

    private fun setupTracksView() {
        binding.tracksRecycleView.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            { track: Track ->
                if (viewModel.clickDebounce()) {
                    viewModel.addTrack(track)
                    val displayTrackIntent = Intent(this, PlayerActivity::class.java)
                    val trackAsString = Gson().toJson(track)
                    displayTrackIntent.putExtra("Track", trackAsString)
                    startActivity(displayTrackIntent)
                    historyTracksAdapter.notifyDataSetChanged()
                }
            }
        )
        binding.tracksRecycleView.adapter = tracksAdapter
    }

    private fun setupHistoryOfSearch() {
            binding.retryButton.setOnClickListener {
                binding.historyTracksRecycleView.adapter?.notifyDataSetChanged()
                binding.historyOfSearch.isVisible = viewModel.shouldDisplayHistory()
            }
    }

    private fun setupHistoryTracksView() {
        historyTracksAdapter = TrackAdapter(
            emptyList(),//tracks
            { track: Track ->
                val displayTrackIntent = Intent(this, PlayerActivity::class.java)
                val trackAsString = Gson().toJson(track)
                displayTrackIntent.putExtra("Track", trackAsString)
                startActivity(displayTrackIntent)
            }
        )

        binding.apply {
            clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                binding.historyTracksRecycleView.adapter?.notifyDataSetChanged()
            }
            historyTracksRecycleView.adapter = historyTracksAdapter
        }
        binding.historyTracksRecycleView.layoutManager = LinearLayoutManager(this)
    }

    private fun searchEdit() {
        val searchTextWatcher = object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setSearchText(p0.toString())
                binding.apply {
                    clearIcon.isVisible = !p0.isNullOrEmpty()
                    historyOfSearch.isVisible = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        }
        binding.apply {
            searchEditText.addTextChangedListener(searchTextWatcher)
            searchEditText.setOnFocusChangeListener { view, b ->
                historyOfSearch.isVisible = viewModel.shouldDisplayHistory()
                historyTracksAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupViewModel() {
        viewModel.onCreate()

        viewModel.observeState().observe(this) {
            binding.searchEditText.setText(it.text)
            render(it.status)
            tracksAdapter.tracks = it.tracks
            tracksAdapter.notifyDataSetChanged()
            historyTracksAdapter.tracks = it.historyTracks
            historyTracksAdapter.notifyDataSetChanged()
        }
    }

    private fun setupClearIcon() {
        binding.apply {
            clearIcon.setOnClickListener {
                viewModel.onClear()
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                tracksRecycleView.adapter?.notifyDataSetChanged()
            }
        }
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
        viewModel.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.onRestoreInstanceState(savedInstanceState)
    }

    private fun render(status: SearchStatus) {
        runOnUiThread {
            binding.apply {
                progressBar.isVisible = status == SearchStatus.LoadingRequest
                tracksRecycleView.isVisible = status == SearchStatus.Success
                tracksNoConnection.isVisible = status == SearchStatus.Failed
                tracksNothingFounded.isVisible = status == SearchStatus.Empty

                if (status == SearchStatus.None) {
                    clearHistoryButton.isVisible = viewModel.shouldDisplayHistory()
                }

                if (status == SearchStatus.Success ||
                    status == SearchStatus.Failed ||
                    status == SearchStatus.Empty) {
                    tracksRecycleView.adapter?.notifyDataSetChanged()
                }
            }
        }
    }
}
