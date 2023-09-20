package com.teamdefine.legalvault.main.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentReviewAgreementBinding
import com.teamdefine.legalvault.main.review.model.Signer
import timber.log.Timber

class ReviewAgreement : Fragment() {
    private lateinit var viewModel: ReviewAgreementViewModel
    private lateinit var binding: FragmentReviewAgreementBinding
    private val args: ReviewAgreementArgs by navArgs()
    private var signerCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentReviewAgreementBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.documentEditText.setText(args.generatedText)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.addSignerButton.setOnClickListener {
            if (signerCount < 5) {
                signerCount++
                val cardView =
                    LayoutInflater.from(requireContext()).inflate(R.layout.signer_container, null)
                binding.container.addView(cardView)
            }

        }
        binding.compileContractButton.setOnClickListener {
            var signers: ArrayList<Signer> = arrayListOf()
            for (i in 1 until binding.container.childCount) {
                val signer = binding.container.getChildAt(i)
                val role = signer.findViewById<EditText>(R.id.inputRole)?.text.toString()
                val name = signer.findViewById<EditText>(R.id.inputName)?.text.toString()
                val email = signer.findViewById<EditText>(R.id.inputEmail)?.text.toString()
                val signerData = Signer(role, name, email)
                signers.add(signerData)
            }
            Timber.i(signers.toString())
        }
    }
}