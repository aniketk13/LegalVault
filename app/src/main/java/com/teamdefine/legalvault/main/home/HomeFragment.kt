package com.teamdefine.legalvault.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.teamdefine.legalvault.databinding.FragmentHomeBinding
import com.teamdefine.legalvault.main.home.adapter.HomePageViewPagerAdapter
import com.teamdefine.legalvault.main.home.generate.GenerateNewDocumentVM
import com.teamdefine.legalvault.main.utility.Utility

class HomeFragment : Fragment() {
    private val viewModel: GenerateNewDocumentVM by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val titleList = arrayListOf("Generate Docs", "My Docs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHomeBinding.inflate(layoutInflater, container, false).also {
        binding = it
        binding.apply {
            fragmentViewpager.adapter = HomePageViewPagerAdapter(
                this@HomeFragment.childFragmentManager,
                this@HomeFragment.viewLifecycleOwner
            )
        }
        TabLayoutMediator(binding.fragmentTopicsTabs, binding.fragmentViewpager) { tab, index ->
            tab.text = titleList[index]
        }.attach()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initBackPressListener()
    }

    private fun initViews() {
        Utility.loadImage(
            requireContext(),
            viewModel.firebaseAuth.currentUser?.photoUrl.toString(),
            binding.profileImg
        )
    }

    private fun initBackPressListener() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }
}