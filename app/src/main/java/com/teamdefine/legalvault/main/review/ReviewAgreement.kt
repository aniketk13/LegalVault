package com.teamdefine.legalvault.main.review

import android.app.ProgressDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentReviewNewBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.generate.Node
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.review.model.Signers
import com.teamdefine.legalvault.main.utility.CONSTANTS
import com.teamdefine.legalvault.main.utility.Utility
import com.teamdefine.legalvault.main.utility.Utility.showProgressDialog
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import timber.log.Timber

class ReviewAgreement : Fragment() {
    private val viewModel: ReviewAgreementViewModel by viewModels()
    private lateinit var binding: FragmentReviewNewBinding
    private lateinit var firebaseInstance: FirebaseAuth
    private lateinit var firestoreInstance: FirebaseFirestore
    private var listOfSigner: ArrayList<Signers> = arrayListOf()
    private var publicURL: String = ""
    private val args: ReviewAgreementArgs by navArgs()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentReviewNewBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseInstance = FirebaseAuth.getInstance()
        firestoreInstance = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(requireContext())
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.documentEditText.setText(args.generatedText)
        initViews()
        initClickListeners()
        initObservers()
    }

    private fun initViews() {
        initEditTextCursor()
        binding.topToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                resetCursorPosition(binding.inputName)
                resetCursorPosition(binding.inputEmail)
                resetCursorPosition(binding.inputName2)
                resetCursorPosition(binding.inputEmail2)
            }
        }

        binding.inputName.onFocusChangeListener = focusChangeListener
        binding.inputEmail.onFocusChangeListener = focusChangeListener
        binding.inputName2.onFocusChangeListener = focusChangeListener
        binding.inputEmail2.onFocusChangeListener = focusChangeListener
    }

    private fun initEditTextCursor() {
        Handler().postDelayed({
            setCursorAtStartAndShowKeyboard(binding.documentEditText)
        }, 1000)
    }

    private fun resetCursorPosition(editText: EditText) {
        editText.setSelection(0)
        hideKeyboard(editText)
    }

    private fun initObservers() {

        viewModel.infuraDocDeleted.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                val update = hashMapOf<String, Any>("hash" to "null")
                firestoreInstance.collection("linkedLists").document(args.prevSignatureId!!)
                    .update(update).addOnCompleteListener {
                        Timber.i("Hash removed from firestore")
                        findNavController().popBackStack()
                    }
            }
        })

        viewModel.localFilePath.observe(viewLifecycleOwner, Observer { filePath ->
            Timber.e(filePath.toString())
            filePath?.let {
                viewModel.savePdfToStorage(
                    it,
                    args.documentName.replace("\\s".toRegex(), ""),
                    firebaseInstance.currentUser!!
                )
            }
        })

        viewModel.publicSavedFileUrl.observe(viewLifecycleOwner, Observer { publicUrlFromDB ->
            publicURL = publicUrlFromDB
            viewModel.sendDocForSignatures(
                EmbeddedSignRequestModel(
                    clientId = CONSTANTS.CLIENT_ID,
                    documentTitle = args.documentName,
                    documentSubject = "${args.documentName}-Subject",
                    documentMessage = "${args.documentName}-Message",
                    documentSigners = listOfSigner,
                    arrayListOf(publicURL)
                )
            )
        })

        viewModel.loadingModel.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadingModel.LOADING -> progressDialog.showProgressDialog("Finalizing your agreement")
                else -> if (progressDialog.isShowing) progressDialog.dismiss()
            }
        })

        viewModel.docSentForSignatures.observe(viewLifecycleOwner, Observer {
            val signersName = listOfSigner.joinToString(",") { it.signerName }
            val node = Node(
                it.signature_request.signature_request_id,
                null,
                binding.documentEditText.text.toString(),
                Utility.convertTimestampToDateInIST(it.signature_request.created_at),
                args.documentName,
                signersName,
                false
            )
            uploadNodeToFirestore(node)
        })
    }

    fun uploadNodeToFirestore(node: Node) {
        val nodeDocument = hashMapOf(
            "value" to node.value,
            "text" to node.text,
            "status" to "New",
            "documentName" to node.documentName,
            "signers" to node.signers,
            "date" to node.date,
            "hash" to "null",
            "is_signed" to false
        )

        if (args.prevSignatureId != null) {
            nodeDocument["nextNodeId"] = args.prevSignatureId!!
            firestoreInstance.collection("linkedLists").document("${node.value}").set(nodeDocument)
            changePrevDocStatus(args.prevSignatureId!!)
        } else {
            nodeDocument["nextNodeId"] = null
            firestoreInstance.collection("linkedLists").document("${node.value}").set(nodeDocument)
            findNavController().popBackStack()
        }
    }

    private fun changePrevDocStatus(prevSignatureId: String) {
        val update = hashMapOf<String, Any>("status" to "Old")
        firestoreInstance.collection("linkedLists").document(prevSignatureId).update(update)
            .addOnCompleteListener {
                removeHashFireStore(prevSignatureId)
            }
    }

    private fun removeHashFireStore(prevSignatureId: String) {
        firestoreInstance.collection("linkedLists").document(prevSignatureId).get()
            .addOnCompleteListener {
                val hash = it.result.getString("hash")
                if(hash.isNullOrEmpty()){
                    findNavController().popBackStack()
                }else{
                    viewModel.removeDocumentFromInfura(hash)
                }
            }
    }

    private fun initClickListeners() {
        binding.compileContractButton.setOnClickListener {
            val name1 = binding.inputName.text.toString()
            val email1 = binding.inputEmail.text.toString()
            val name2 = binding.inputName2.text.toString()
            val email2 = binding.inputEmail2.text.toString()

            if (name1.isEmpty() || name2.isEmpty() || email1.isEmpty() || email2.isEmpty()) {
                binding.root.showSnackBar("At least 2 signers are required")
            } else {
                listOfSigner.add(Signers(name1, email1, 0))
                listOfSigner.add(Signers(name2, email2, 1))
                viewModel.generatePdf(
                    requireContext(),
                    binding.documentEditText.text.toString(),
                    args.documentName,
                    binding.root
                )
            }
        }
    }

    private fun setCursorAtStartAndShowKeyboard(editText: EditText) {
        editText.requestFocus()
        editText.setSelection(0)
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
