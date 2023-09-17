package com.teamdefine.legalvault.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.teamdefine.legalvault.databinding.FragmentHomeBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private val viewModel: HomeVM by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHomeBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.gptResponse.observe(viewLifecycleOwner) { gptResponse ->
            Timber.tag("helloabc").i(gptResponse.toString())
        }
    }

    private fun initClickListeners() {
        binding.submitButton.setOnClickListener {
            viewModel.updateLoadingModel(LoadingModel.LOADING)
            generateAgreement(binding.promptTextInput.text.toString())
        }
    }

    private fun generateAgreement(prompt: String) {
        viewModel.generateAgreement(prompt)

    }

}