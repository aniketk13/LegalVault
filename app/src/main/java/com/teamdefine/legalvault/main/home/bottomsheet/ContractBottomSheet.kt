package com.teamdefine.legalvault.main.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamdefine.legalvault.databinding.LayoutMenuBottomSheetBinding
import com.teamdefine.legalvault.main.home.mydocs.MyDocumentsVM
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest

class ContractBottomSheet : BottomSheetDialogFragment() {
    private val bottomSheetVM: MyDocumentsVM by activityViewModels()
    private lateinit var binding: LayoutMenuBottomSheetBinding
    private lateinit var signature: SignatureRequest

    companion object {
        fun newInstance(
            signature: SignatureRequest
        ): ContractBottomSheet {
            val fragment = ContractBottomSheet()
            val args = Bundle().apply {
                putParcelable("SIGNATURE", signature)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutMenuBottomSheetBinding.inflate(inflater, container, false).also {
        binding = it
        signature = arguments?.getParcelable("SIGNATURE")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
    }

    private fun initViews() {
        binding.contractName.text = signature.subject
    }

    private fun initClickListeners() {
        binding.apply {
        }
    }
}