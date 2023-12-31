package com.teamdefine.legalvault.main.home.generate

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentGenerateNewDocumentBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.HomeFragmentDirections
import com.teamdefine.legalvault.main.utility.Utility
import com.teamdefine.legalvault.main.utility.Utility.showProgressDialog
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import timber.log.Timber


class GenerateNewDocument : Fragment() {
    private lateinit var binding: FragmentGenerateNewDocumentBinding
    private val viewModel: GenerateNewDocumentVM by viewModels()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentGenerateNewDocumentBinding.inflate(layoutInflater, container, false).also {
        binding = it
        progressDialog = ProgressDialog(requireContext())
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initObservers()
//// Create a message payload
//        val messagePayload = mapOf(
//            "notification" to mapOf(
//                "title" to "Notification Title",
//                "body" to "Notification Body"
//            ),
//            "to" to "/topics/allDevices" // Send to a topic to target all devices
//        )

// Send the message
//        val firebaseMessaging = FirebaseMessaging.getInstance()
//        firebaseMessaging.send(RemoteMessage(Bundle().apply {
//            putString("TEST", "test")
//        }))
        val remoteMessage = RemoteMessage.Builder("allDevices") // Use the topic as 'to'
            .setMessageId(java.lang.Integer.toString(1))
            .setData(
                mapOf(
                    "title" to "Notification Title",
                    "body" to "Notification Body"
                )
            )
            .build()

        FirebaseMessaging.getInstance().send(remoteMessage)

    }


    override fun onResume() {
        super.onResume()
        binding.promptTextInput.setText("")
    }

    private fun initClickListeners() {
        binding.prepareDocBtn.setOnClickListener {
            if (binding.promptTextInput.text.toString().isNullOrEmpty())
                binding.root.showSnackBar("Describe your article")
            else {
//                generateAgreement()
//                generateAgreement("create one funny joke in one line")
                Timber.i(Utility.appendCustomDocGenerationPropmt(binding.promptTextInput.text.toString()))
                generateAgreement(Utility.appendCustomDocGenerationPropmt(binding.promptTextInput.text.toString()))
            }
        }
        binding.mic.setOnClickListener {
            binding.root.showSnackBar("Coming soon")
        }
    }

    private fun initObservers() {
        viewModel.gptResponse.observe(viewLifecycleOwner, EventObserver { gptResponse ->
            Timber.i(gptResponse.results[0].generated_text)
            binding.root.showSnackBar("Agreement Generated")
            showAlert(gptResponse.results[0].generated_text)
        })
        viewModel.loadingModel.observe(viewLifecycleOwner, Observer { loadingState ->
            when (loadingState) {
                LoadingModel.LOADING -> progressDialog.showProgressDialog("Preparing your agreement, this may take a few minutes..")
                else -> if (progressDialog.isShowing) progressDialog.dismiss()
            }
        })
    }

    private fun showAlert(generatedText: String) {
        val reasonDialogView = LayoutInflater.from(activity)
            .inflate(R.layout.document_name, null)
        val dialogBuilder =
            AlertDialog.Builder(activity).setView(reasonDialogView).setTitle("Name Your Document")
        val alertDialog = dialogBuilder.show()
        alertDialog.setCancelable(false)

        val confirmButton = reasonDialogView.findViewById<Button>(R.id.prepareDocBtn)
        confirmButton.setOnClickListener {
            alertDialog.dismiss()
            var documentName = ""
            documentName =
                reasonDialogView.findViewById<TextInputEditText>(R.id.docInput).text.toString()
            navigateToEditDocument(generatedText, documentName)
        }
    }

    private fun generateAgreement(prompt: String) {
        viewModel.generateAgreement(prompt)
    }

    private fun navigateToEditDocument(generatedText: String, documentName: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToReviewAgreement(
                generatedText,
                documentName,
                null
            )
        )
    }
}