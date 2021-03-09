package com.kontur.myapplication.ui.detailed

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.kontur.myapplication.MainActivity
import com.kontur.myapplication.R
import com.kontur.myapplication.databinding.DetailedContactFragmentBinding

class DetailedContactFragment : Fragment() {


    private val args: DetailedContactFragmentArgs by navArgs()
    private lateinit var binding: DetailedContactFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailedContactFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniGUI()


    }

    private fun iniGUI() {

        binding.nameTextView.text = args.contact.name
        binding.phoneNumberTextView.text = args.contact.phone
        binding.temperamentTextView.text = args.contact.temperament.name
        binding.dateTextView.text = String.format(
            resources.getString(R.string.date),
            args.contact.period.startDate,
            args.contact.period.endDate
        )
        binding.biographyTextView.text = args.contact.biography

        binding.phoneNumberTextView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${binding.phoneNumberTextView.text}" )))
        }

        binding.toolbar.setupWithNavController(
            findNavController(),
            AppBarConfiguration(findNavController().graph)
        )


    }


}