package com.teamdefine.legalvault.main.home.history

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.navArgs
import com.teamdefine.legalvault.databinding.FragmentHistoryBinding
import com.teamdefine.legalvault.main.home.history.epoxy.EpoxyHistoryController
import com.teamdefine.legalvault.main.home.generate.Node
import kotlinx.parcelize.Parcelize
import timber.log.Timber

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private val historyController: EpoxyHistoryController by lazy {
        EpoxyHistoryController()
    }

    private val args: HistoryFragmentArgs by navArgs()
    private lateinit var historyDoc: MyArgs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHistoryBinding.inflate(layoutInflater, container, false).also {
        binding = it
        historyDoc = args.history
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyController.adapter
        }
    }


}

@Parcelize
data class MyArgs(val nodePair: Pair<Node, ArrayList<Node>>) : Parcelable