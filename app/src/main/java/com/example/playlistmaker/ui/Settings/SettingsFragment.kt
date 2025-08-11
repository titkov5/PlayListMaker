package com.example.playlistmaker.ui.Settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.media.MediaPageViewAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.userAgreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.offerString))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        binding.shareApp.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.androidRazrab))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.contactSupport.setOnClickListener {
            val message = getString(R.string.thanksAll)
            val subject = getString(R.string.messageToAll)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(R.string.myMail))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,subject)
            startActivity(shareIntent)
        }

        viewModel.observeState().observe(viewLifecycleOwner) {
            binding.darkThemSwithcher.isChecked = it
        }
        viewModel.onCreate()
        binding.darkThemSwithcher.setOnCheckedChangeListener { _, checked ->
            viewModel.checkDarkTheme(checked)
        }

        return binding.root
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}