package com.example.playlistmaker.ui.Search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.ui.Player.PlayerFragment
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment()  {
    private val viewModel by viewModel<SearchViewModel>()
    lateinit var historyTracksAdapter: TrackAdapter
    lateinit var tracksAdapter: TrackAdapter

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        setupClearIcon()
        setupHistoryOfSearch()
        searchEdit()
        setupTracksView()
        setupHistoryTracksView()
        setupViewModel()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTracksView() {
        binding.tracksRecycleView.layoutManager = LinearLayoutManager(requireActivity())
        tracksAdapter = TrackAdapter(
            tracks = emptyList(),
            { track: Track ->
                if (viewModel.clickDebounce()) {
                    viewModel.addTrack(track)
                    val trackAsString = Gson().toJson(track)
                    historyTracksAdapter.notifyDataSetChanged()
                    val fragment = PlayerFragment.newInstance(trackAsString)
                    findNavController().navigate(R.id.playerFragment2, fragment.arguments)
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
            emptyList(),
            { track: Track ->
                val trackAsString = Gson().toJson(track)
                val fragment = PlayerFragment.newInstance(trackAsString)
                findNavController().navigate(R.id.playerFragment2, fragment.arguments)

//                parentFragmentManager.commit {
//                    replace(R.id.rootFragmentContainerView, PlayerFragment.newInstance(trackAsString))
//                    addToBackStack(null)
//                }
            }
        )

        binding.apply {
            clearHistoryButton.setOnClickListener {
                viewModel.clearHistory()
                binding.historyTracksRecycleView.adapter?.notifyDataSetChanged()
            }
            historyTracksRecycleView.adapter = historyTracksAdapter
        }
        binding.historyTracksRecycleView.layoutManager = LinearLayoutManager(requireActivity())
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

        viewModel.observeState().observe(viewLifecycleOwner) {
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
                    requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
                tracksRecycleView.adapter?.notifyDataSetChanged()
            }
        }
    }

//    private fun setupToolBar() {
//        val toolbar = findViewById<MaterialToolbar>(R.id.search_toolbar)
//        toolbar.setNavigationOnClickListener {
//            val displayIntent = Intent(this, MainActivity::class.java)
//            startActivity(displayIntent)
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.onSaveInstanceState(outState)
    }

    private fun render(status: SearchStatus) {
        requireActivity().runOnUiThread {
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
