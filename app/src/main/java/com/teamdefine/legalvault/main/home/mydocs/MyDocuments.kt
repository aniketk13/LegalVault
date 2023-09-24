package com.teamdefine.legalvault.main.home.mydocs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentMyDocumentsBinding

class MyDocuments : Fragment() {
    private lateinit var binding: FragmentMyDocumentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
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
        initClickListeners()
        initObservers()
    }

    private fun initClickListeners() {
        TODO("Not yet implemented")
    }

    private fun initObservers() {
        viewmodel.myDocs.observe(viewLifecycleOwner, Observer {
            viewmodel.getDataFromFirestore()
        })
        viewmodel.firestoreSnapshot.observe(viewLifecycleOwner, Observer { snapshot ->
            val clientId = snapshot.getValue("clientId") as String
            viewmodel.myDocs.value?.signature_requests?.let { requests ->
                val filteredList = requests.filter { it.client_id == clientId }
                //send data to recycler view
            }
        })
    }
}