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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.ui.MainActivity
import com.example.playlistmaker.PRACTICUM_EXAMPLE_PREFERENCES
import com.example.playlistmaker.ui.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson

class SearchActivity : AppCompatActivity()  {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        setupClearIcon()
        setupToolBar()
        setupHistoryOfSearch()
        searchEdit()
        setupTracksView()
        setupHistoryTracksView()
    }

    private fun setupTracksView() {
        binding.tracksRecycleView.layoutManager = LinearLayoutManager(this)
        binding.tracksRecycleView.adapter = viewModel.tracksAdapter
    }

    private fun setupHistoryOfSearch() {
        binding.apply {
            retryButton.setOnClickListener {
                historyOfSearch.isVisible = viewModel.searchHistory.shouldDisplay()
            }
        }
    }

    private fun setupHistoryTracksView() {
        binding.apply {
            clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                binding.historyTracksRecycleView.adapter?.notifyDataSetChanged()
            }
            historyTracksRecycleView.adapter = viewModel.historyTracksAdapter
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
                historyTracksRecycleView.isVisible = viewModel.searchHistory.shouldDisplay()
            }
        }
    }

    private fun setupViewModel() {
        val sharedPrefs = getSharedPreferences(PRACTICUM_EXAMPLE_PREFERENCES, MODE_PRIVATE)
        viewModel.onCreate(sharedPrefs, this)

        viewModel.observeSearchText().observe(this) {
            binding.searchEditText.setText(it)
        }

        viewModel.observeSearchStatus().observe(this) {
            setUpViewWith(it)
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
                historyOfSearch.isVisible = viewModel.searchHistory.shouldDisplay()
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

    private fun setUpViewWith(status: SearchStatus) {
        runOnUiThread {
            binding.apply {
                progressBar.isVisible = status == SearchStatus.LoadingRequest
                tracksRecycleView.isVisible = status == SearchStatus.Success
                tracksNoConnection.isVisible = status == SearchStatus.Failed
                tracksNothingFounded.isVisible = status == SearchStatus.Empty

                if (status == SearchStatus.None) {
                    clearHistoryButton.isVisible = viewModel.searchHistory.shouldDisplay()
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
