package com.teamdefine.legalvault.main.home.mydocs

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentMyDocumentsBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.HomeFragmentDirections
import com.teamdefine.legalvault.main.home.bottomsheet.ContractBottomSheet
import com.teamdefine.legalvault.main.home.generate.GenerateNewDocumentVM
import com.teamdefine.legalvault.main.home.generate.LinkedList
import com.teamdefine.legalvault.main.home.mydocs.adapter.MyDocsAdapter
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.CONSTANTS
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

class MyDocuments : Fragment() {
    private lateinit var binding: FragmentMyDocumentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var adapter: MyDocsAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewmodel: MyDocumentsVM by activityViewModels()
    private val contractGenVM: GenerateNewDocumentVM by viewModels()
    private val listOfUrls: ArrayList<String> = arrayListOf()
    private lateinit var requestUrls: ArrayList<Pair<String, String>>

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
        else {
            adapter.setData(filteredList)
            filteredList.forEach { request ->
                if (request.is_complete) {
                    firebaseFirestore.collection("linkedLists")
                        .document(request.signature_request_id).get().addOnCompleteListener {
                            if (it.result.exists()) {
                                val hash = it.result.getString("hash")
                                if (hash.equals("null")) {
                                    viewmodel.getFile(request.signature_request_id)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun getFilePath(url: String, signatureId: String) {
        val tempFile = File(requireContext().cacheDir, "temp_file")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val outputStream = FileOutputStream(tempFile)
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
                        Timber.i("happening")
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    uploadDocument(tempFile.absolutePath, signatureId)
                    outputStream.close()
                    inputStream?.close()

                } else {
                    // Handle the error
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }

    private fun uploadDocument(filePath: String, signatureId: String) {
        viewmodel.uploadDocumentToInfura(filePath, signatureId)
    }

    private fun initObservers() {
        viewmodel.infuraAddResponse.observe(viewLifecycleOwner, Observer { pair ->
            //we need signature id here
            val update = hashMapOf<String, Any>("hash" to pair.first.Hash)
            update["is_signed"] = true
            firebaseFirestore.collection("linkedLists").document(pair.second).update(update)
                .addOnCompleteListener {
                    Timber.i("hash value updated successfully")
                }
        })
        viewmodel.pdfUrl.observe(viewLifecycleOwner, Observer {
            getFilePath(it.first, it.second)
        })

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

        viewmodel.docText.observe(viewLifecycleOwner, EventObserver {
            summarizeUtility(it)
        })
        viewmodel.gptResponseSummary.observe(viewLifecycleOwner, EventObserver {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSummaryFragment(
                    it.results[0].generated_text
                )
            )
        })
    }

    private fun summarizeUtility(docText: String?) {
        docText?.let {
            val promptToSend =
                "Summarize this text in under 50 words. The text is: $docText. Make it crisp"
            viewmodel.summarizeDoc(promptToSend)
        }
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