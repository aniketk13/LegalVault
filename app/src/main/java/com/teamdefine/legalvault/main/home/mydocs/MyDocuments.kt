package com.teamdefine.legalvault.main.home.mydocs

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentMyDocumentsBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.bottomsheet.ContractBottomSheet
import com.teamdefine.legalvault.main.home.generate.LinkedList
import com.teamdefine.legalvault.main.home.mydocs.adapter.MyDocsAdapter
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.CONSTANTS
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel
import timber.log.Timber
import java.util.concurrent.atomic.AtomicReference

class MyDocuments : Fragment() {
    private lateinit var binding: FragmentMyDocumentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var adapter: MyDocsAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewmodel: MyDocumentsVM by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentMyDocumentsBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        viewmodel.getAllDocs()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initClickListeners()
        initObservers()
    }

    private fun initAdapter() {
        adapter = MyDocsAdapter(object : MyDocsAdapter.ItemClicks {
            override fun onItemClick(signature: SignatureRequest) {
                Timber.e("Clicked: $signature\n")
                showBottomSheet(signature)
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun showBottomSheet(signature: SignatureRequest) {
        ContractBottomSheet.newInstance(signature = signature)
            .show(childFragmentManager, "CONTRACT_OPTIONS_BOTTOM_SHEET")
    }

    private fun initClickListeners() {
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                viewmodel.getAllDocs()
            }
        }
    }

    private fun setDataInRecycler(filteredList: ArrayList<SignatureRequest>) {
        if (binding.swipeRefresh.isRefreshing) binding.swipeRefresh.isRefreshing = false
        if (filteredList.isEmpty())
            binding.noSignLayout.noSignIv.visibility = View.VISIBLE
        else
            adapter.setData(filteredList)
    }

    private fun initObservers() {
        viewmodel.myDocs.observe(viewLifecycleOwner, Observer {
            val documents =
                it.signature_requests.filter { it.client_id == CONSTANTS.CLIENT_ID } as ArrayList<SignatureRequest>
            val finalDocuments = AtomicReference(ArrayList<SignatureRequest>())
            Timber.i(documents.size.toString())
            for (i in 0 until documents.size) {
                firebaseFirestore.collection("linkedLists")
                    .document(documents[i].signature_request_id).get().addOnCompleteListener {
                        if (it.result.exists()) {
                            if (it.result.getString("status") == "New") {
                                Timber.i("exists")
                                val currentList = finalDocuments.get()
                                currentList.add(documents[i])
                                finalDocuments.set(currentList)
                            }
                        }
                        viewmodel.updateLoadingModel(LoadingModel.LOADING)
                        Handler().postDelayed({
                            if (i == documents.size - 1) {
                                Timber.i(finalDocuments.get().toString())
                                setDataInRecycler(finalDocuments.get())
                            }
                            viewmodel.updateLoadingModel(LoadingModel.COMPLETED)
                        }, 3000)

                    }
            }
//            var list:MutableList<Node> = mutableListOf()
//            for(i in documents){
//                val node= Node(i.signature_request_id,null)
//                list.add(node)
//            }
//            uploadLinkedListToFirestore(LinkedList(list))
        })
        viewmodel.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.loadingModel.progressBar.setVisibilityBasedOnLoadingModel(it)
        })
    }

    fun uploadLinkedListToFirestore(linkedList: LinkedList) {
        val db = FirebaseFirestore.getInstance()

        // Create a Firestore document for each node
        val nodeDocuments = linkedList.nodes.mapIndexed { index, node ->
            val nodeDocument = hashMapOf(
                "value" to node.value
            )

            // If it's not the last node, add a reference to the next node
            if (index <= linkedList.nodes.size - 1) {
                nodeDocument["status"] = "New"
                nodeDocument["nextNodeId"] = null
            }

            db.collection("linkedLists").document("${node.value}").set(nodeDocument)
        }

        // Note: This is a simplified example and does not handle errors or document references completely.
    }
}