package com.example.playlistmaker.ui.Player

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class PlayerFragment : Fragment() {
    private val viewModel by viewModel<PlayerViewModel>()

    private var _binding: FragmentPlayerBinding? = null
//    private val binding get() = _binding!!

    private val binding: FragmentPlayerBinding
        get() {
            return _binding!!
        }

    companion object {
        const val TRACK_KEY = "TRACK_KEY"

        fun newInstance(trackAsString: String) = PlayerFragment().apply {
            arguments = bundleOf(TRACK_KEY to trackAsString)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(layoutInflater)
        val trackAsJson = requireArguments().getString(TRACK_KEY)
        val track = Gson().fromJson(trackAsJson, Track::class.java)
        binding.apply {

            binding.playerToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            trackMainTitle.text = track.trackName
            trackSubtitle.text = track.artistName
            trackAlbumValue.text = track.collectionName
            trackTimeValue.text = track.trackTime

            val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
            val date = formatter.parse(track.releaseDate)
            val year = formatter.format(date)
            trackYearValue.text = year
            trackCountryTitleValue.text = track.country
            trackGanreValue.text = track.primaryGenreName
            iconPlay.setOnClickListener {
                viewModel.playPause()
            }
            val coverUrl = track.artworkUrl100.replaceAfterLast('/',getString(R.string.cover512))
            Glide
                .with(requireActivity().applicationContext)
                .load(coverUrl)
                .placeholder(R.drawable.track_placeholder)
                .transform(RoundedCorners(dpToPx(8.toFloat())))
                .into(cover)
        }

        viewModel.prepareMediaPlayer(track.previewUrl)

        viewModel.observePlaybackState().observe(viewLifecycleOwner) {
            when (it) {
                PlaybackState.Playing -> {
                    binding.apply {
                        if (isDarkMode()) {
                            iconPlay.setImageResource(R.drawable.pause_night)
                        } else {
                            iconPlay.setImageResource(R.drawable.pause)
                        }
                    }
                }
                else -> {
                    binding.apply {
                        if (isDarkMode()) {
                            iconPlay.setImageResource(R.drawable.play_night)
                        } else {
                            iconPlay.setImageResource(R.drawable.play)
                        }
                    }
                }
            }
        }

        viewModel.observerPlayerPosition().observe(viewLifecycleOwner) {
            binding.trackTimeCurrentValue.text = it
        }

        return binding.root
    }

    private fun isDarkMode(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        viewModel.onDestroy()
        super.onDestroyView()
        _binding = null
    }

    fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            requireActivity().applicationContext.resources.displayMetrics).toInt()
    }
}

