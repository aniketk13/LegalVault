package com.teamdefine.legalvault.main.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentReviewAgreementBinding
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.review.model.Signers
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel
import timber.log.Timber

const val MY_CLIENT_ID = "d0b0258b7a737cda807b5b996f31a765"

class ReviewAgreement : Fragment() {
    private val viewModel: ReviewAgreementViewModel by viewModels()
    private lateinit var binding: FragmentReviewAgreementBinding
    private lateinit var firebaseInstance: FirebaseAuth
    var listOfSigner: List<Signers> = listOf()
    private val args: ReviewAgreementArgs by navArgs()
    private var signerCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentReviewAgreementBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseInstance = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.documentEditText.setText(args.generatedText)
        initViews()
        initClickListeners()
        initObservers()
    }

    private fun initViews() {
        binding.topToolbar.title = "Review Agreement (${args.documentName})"
    }

    private fun initObservers() {
        viewModel.localFilePath.observe(viewLifecycleOwner, Observer { filePath ->
            Timber.e(filePath.toString())
            filePath?.let {
                viewModel.savePdfToStorage(it, args.documentName, firebaseInstance.currentUser!!)
            }
        })
        viewModel.publicSavedFileUrl.observe(viewLifecycleOwner, Observer { publicUrl ->
            viewModel.sendDocForSignatures(
                EmbeddedSignRequestModel(
                    clientId = MY_CLIENT_ID,
                    documentTitle = "${args.documentName}-Title",
                    documentSubject = "${args.documentName}-Subject",
                    documentMessage = "${args.documentName}-Message",
                    documentSigners = listOfSigner as ArrayList<Signers>,
                    arrayListOf(publicUrl)
                )
            )
        })
        viewModel.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.root.setVisibilityBasedOnLoadingModel(it)
        })
        viewModel.docSentForSignatures.observe(viewLifecycleOwner, EventObserver {
            //to do: navigate to 2nd fragment of viewpager to view the request
        })
    }

    private fun initClickListeners() {
        binding.addSignerButton.setOnClickListener {
            if (signerCount < 5) {
                signerCount++
                val cardView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.signer_container, null)
                binding.container.addView(cardView)
            }
        }

        binding.compileContractButton.setOnClickListener {
            listOfSigner = (1 until binding.container.childCount).map { index ->
                val signer = binding.container.getChildAt(index)
                val name = signer.findViewById<EditText>(R.id.inputName)?.text.toString()
                val email = signer.findViewById<EditText>(R.id.inputEmail)?.text.toString()
                Signers(name, email)
            }
            Timber.i(listOfSigner.toString())
            viewModel.generatePdf(
                requireContext(),
                args.generatedText,
                args.documentName,
                binding.root
            )
        }
    }

}