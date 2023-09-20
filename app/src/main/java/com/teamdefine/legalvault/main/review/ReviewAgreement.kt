package com.teamdefine.legalvault.main.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.teamdefine.legalvault.databinding.FragmentReviewAgreementBinding

class ReviewAgreement : Fragment() {
    private lateinit var viewModel: ReviewAgreementViewModel
    private lateinit var binding: FragmentReviewAgreementBinding
    private val args: ReviewAgreementArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentReviewAgreementBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.documentEditText.setText(args.generatedText)
    }
}