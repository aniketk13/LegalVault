package com.teamdefine.legalvault.main.review

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentReviewAgreementBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.review.model.Signers
import com.teamdefine.legalvault.main.utility.CONSTANTS
import com.teamdefine.legalvault.main.utility.Utility.showProgressDialog
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import timber.log.Timber

class ReviewAgreement : Fragment() {
    private val viewModel: ReviewAgreementViewModel by viewModels()
    private lateinit var binding: FragmentReviewAgreementBinding
    private lateinit var firebaseInstance: FirebaseAuth
    private lateinit var firestoreInstance: FirebaseFirestore
    var listOfSigner: List<Signers> = listOf()
    var publicURL: String = ""
    private val args: ReviewAgreementArgs by navArgs()
    private var signerCount = 0
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentReviewAgreementBinding.inflate(layoutInflater, container, false).also {
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
        binding.topToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initObservers() {
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
                    documentSigners = listOfSigner as ArrayList<Signers>,
                    arrayListOf(publicURL)
                )
            )
//            viewModel.getDataFromFirestore()
        })
        viewModel.loadingModel.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoadingModel.LOADING -> progressDialog.showProgressDialog("Finalizing your agreement")
                else -> if (progressDialog.isShowing) progressDialog.dismiss()
            }
        })
        viewModel.docSentForSignatures.observe(viewLifecycleOwner, EventObserver {
            //save linked list mai doc ko, along with pointers on to the next (null), and value will be text
            findNavController().popBackStack()
        })
//        viewModel.firestoreSnapshot.observe(viewLifecycleOwner, Observer {
//
//        })
    }

    private fun initClickListeners() {
        binding.addSignerButton.setOnClickListener {
            if (signerCount < 2) {
                signerCount++
                val cardView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.signer_container, null)
                binding.container.addView(cardView)
            } else {
                binding.root.showSnackBar("Max signers limit reached")
            }
        }

        binding.compileContractButton.setOnClickListener {
            //args.genText = binding.edittext
            listOfSigner = (1 until binding.container.childCount).map { index ->
                val signer = binding.container.getChildAt(index)
                val name = signer.findViewById<EditText>(R.id.inputName)?.text.toString()
                val email = signer.findViewById<EditText>(R.id.inputEmail)?.text.toString()
                Signers(name, email, index - 1)
            }
            Timber.i(listOfSigner.toString())
            viewModel.generatePdf(
                requireContext(),
                args.generatedText, //modify this
                args.documentName,
                binding.root
            )
        }
    }
}