package com.teamdefine.legalvault.main.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.LayoutMenuBottomSheetBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.BiometricAuthListener
import com.teamdefine.legalvault.main.home.HomeFragmentDirections
import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubRequestModel
import com.teamdefine.legalvault.main.home.bottomsheet.model.Input
import com.teamdefine.legalvault.main.home.generate.Node
import com.teamdefine.legalvault.main.home.history.MyArgs
import com.teamdefine.legalvault.main.home.mydocs.MyDocumentsVM
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.BiometricUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber

class ContractBottomSheet : BottomSheetDialogFragment(), BiometricAuthListener {
    private val bottomSheetVM: MyDocumentsVM by activityViewModels()
    private lateinit var binding: LayoutMenuBottomSheetBinding
    private lateinit var signature: SignatureRequest
    private lateinit var firebaseInstance: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var flag: Int = 0

    companion object {
        fun newInstance(signature: SignatureRequest): ContractBottomSheet {
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
        firebaseInstance = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        binding = it
        signature = arguments?.getParcelable("SIGNATURE")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
        initObserver()
    }

    private fun initObserver() {
        bottomSheetVM.signingUrl.observe(viewLifecycleOwner, Observer {
            showBiometricLoginOption()
        })
        bottomSheetVM.updateRequestSuccess.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                bottomSheetVM.getUpdateFileStatus()
            }
        })
        bottomSheetVM.updateFileSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                bottomSheetVM.getPageDeploymentStatus()
            }
        })
        bottomSheetVM.pageDeployedSuccess.observe(viewLifecycleOwner, Observer {
            if (it) {
                Timber.i("Page Deployed Successfully")
                dismiss()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTest())
            }
        })
    }

    private fun initViews() {
        binding.contractName.text = signature.subject
    }

    private fun initClickListeners() {
        binding.apply {
            this.signContract.setOnClickListener {
                flag = 2
                val signers = signature.signatures
                val filter =
                    signers.indexOfFirst { it?.signer_email_address == firebaseInstance.currentUser?.email }
                signers[filter]?.let { it1 -> bottomSheetVM.getDocSignUrl(it1.signature_id) }
            }
            modifyContract.setOnClickListener {
                flag = 3
                showBiometricLoginOption()
            }
            modificationHistory.setOnClickListener {
                flag = 4
                showBiometricLoginOption()
            }
            summarizeDoc.setOnClickListener {
                flag = 5
                bottomSheetVM.getDocTextFromFirebase(signature.signature_request_id)
                dismiss()
            }
        }
    }

    private fun showPdfViewerBottomSheet() {
        PdfViewerBottomSheet.newInstance(signatureId = signature.signature_request_id)
            .show(childFragmentManager, "CONTRACT_OPTIONS_BOTTOM_SHEET")
    }

    private fun showBiometricLoginOption() {
        BiometricUtil.showBiometricPrompt(this, this)
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        Timber.i("Success Biometric")
        when (flag) {
            1 -> Timber.i("Success with Previewing Contract")
            2 -> signContract()
            3 -> modifyContract()
            4 -> modificationHistory()
            else -> Timber.i("Error/Select an Option")
        }
    }

    private fun modificationHistory() {
        bottomSheetVM.updateLoadingModel(LoadingModel.LOADING)
        firebaseFirestore.collection("linkedLists").document(signature.signature_request_id).get()
            .addOnCompleteListener {
                if (it.result.exists()) {
                    GlobalScope.launch {
                        generateHistory(it.result.toObject(Node::class.java))
                    }
                }
            }
    }

    private suspend fun generateHistory(result: Node?) {
        bottomSheetVM.updateLoadingModel(LoadingModel.LOADING)
        val headNode = result
        val tempHistory: ArrayList<Node> = arrayListOf()
        var finalHistory: Pair<Node, ArrayList<Node>>
        var nextNodeId: Any? = result?.nextNodeId
        while (nextNodeId != null) {
            val it =
                firebaseFirestore.collection("linkedLists").document(nextNodeId.toString()).get()
                    .await()
            if (it.exists()) {
                it.toObject(Node::class.java)?.let { it1 -> tempHistory.add(it1) }
                nextNodeId = it.get("nextNodeId")
                Timber.i(nextNodeId.toString())
            }
        }
        finalHistory = Pair(headNode!!, tempHistory)
        bottomSheetVM.updateLoadingModel(LoadingModel.COMPLETED)
        withContext(Dispatchers.Main) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToHistoryFragment(
                    MyArgs(finalHistory)
                )
            )
        }
        dismiss()
    }

    private fun modifyContract() {
        bottomSheetVM.updateLoadingModel(LoadingModel.LOADING)
        var documentText: String? = ""
        firebaseFirestore.collection("linkedLists").document(signature.signature_request_id).get()
            .addOnCompleteListener {
                bottomSheetVM.updateLoadingModel(LoadingModel.COMPLETED)
                if (it.result.exists()) {
                    documentText = it.result.getString("text")
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToReviewAgreement(
                            documentText!!,
                            signature.subject,
                            signature.signature_request_id
                        )
                    )
                    dismiss()
                }
            }
    }

    private fun signContract() {
        bottomSheetVM.updateLoadingModel(LoadingModel.LOADING)
        Timber.i("Success with Signing Contract")
        var newContent: String? = null
        try {
            val inputStream = requireContext().assets.open("scratch.html")
            newContent = inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (newContent != null) {
            newContent = newContent.replace(
                "__SIGN_URL__",
                "${bottomSheetVM.signingUrl.value?.embedded?.sign_url}"
            )
            val body = GitHubRequestModel(inputs = Input(new_content = "$newContent"))
            bottomSheetVM.updateFileGithub(body)
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
    }
}
