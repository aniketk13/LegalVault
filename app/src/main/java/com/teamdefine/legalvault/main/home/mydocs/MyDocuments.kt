package com.teamdefine.legalvault.main.home.mydocs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentMyDocumentsBinding
import com.teamdefine.legalvault.main.home.mydocs.adapter.MyDocsAdapter
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.CONSTANTS
import timber.log.Timber

class MyDocuments : Fragment() {
    private lateinit var binding: FragmentMyDocumentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var adapter: MyDocsAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewmodel: MyDocumentsVM by viewModels()
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
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun initClickListeners() {
    }

    private fun setDataInRecycler(filteredList: ArrayList<SignatureRequest>) {
        if (filteredList.isEmpty())
            binding.noSignLayout.noSignIv.visibility = View.VISIBLE
        else
            adapter.setData(filteredList)
    }

    private fun initObservers() {
        viewmodel.myDocs.observe(viewLifecycleOwner, Observer {
            setDataInRecycler(it.signature_requests.filter { it.client_id == CONSTANTS.CLIENT_ID } as ArrayList<SignatureRequest>)
        })
    }
}