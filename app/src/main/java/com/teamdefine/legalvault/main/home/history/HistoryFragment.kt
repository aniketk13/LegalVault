package com.teamdefine.legalvault.main.home.history

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamdefine.legalvault.databinding.FragmentHistoryBinding
import com.teamdefine.legalvault.main.home.generate.Node
import com.teamdefine.legalvault.main.home.history.epoxy.EpoxyHistoryController
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
        initViews()
        setupRecyclerView()
        setDataInEpoxy()
    }

    private fun initViews() {
        binding.topToolbar.setNavigationOnClickListener {
            try {
                findNavController().popBackStack()
            } catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
        binding.lastUpdated.text = "Last Updated On: ${historyDoc.nodePair.first.date}"
        binding.totalRequests.text = (1 + historyDoc.nodePair.second.size).toString()
        binding.signReqYouSent.text =
            "Modifications Done On: ${historyDoc.nodePair.second.lastOrNull()?.documentName}"
    }

    private fun setDataInEpoxy() {
        historyController.setData(historyDoc.nodePair, "")
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