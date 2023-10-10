package com.teamdefine.legalvault.main.legalassistance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.teamdefine.legalvault.databinding.FragmentLegalAssistanceBinding

class LegalAssistance : Fragment() {

    private lateinit var viewModel: LegalAssistanceViewModel
    private lateinit var binding: FragmentLegalAssistanceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentLegalAssistanceBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}