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
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentGenerateNewDocumentBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.HomeFragmentDirections
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
        initViews()
    }

    private fun initViews() {
//        try {
//            Timber.i("inside")
//            val tempFile = File(requireContext().cacheDir, "temp_file")
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                .url("https://s3.amazonaws.com/hellofax_uploads/super_groups/2023/10/02/9becc3ac13e1c305ace71b15c329ebbb570caea8/merged-tamperproofed.pdf?response-content-disposition=attachment&response-content-type=application%2Fbinary&X-Amz-Content-Sha256=UNSIGNED-PAYLOAD&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAUMSXJYX53PEKO3SX%2F20231008%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20231008T180430Z&X-Amz-SignedHeaders=host&X-Amz-Expires=259200&X-Amz-Signature=9543593b559cca86c0ae1384effb8a10071828e5cbb7e234da54383f65fde45b")
//                .build()
//            client.newCall(request).enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        val inputStream = response.body?.byteStream()
//                        val cacheDir = requireContext().cacheDir
//                        val outputStream = FileOutputStream(tempFile)
//                        val buffer = ByteArray(1024)
//                        var bytesRead: Int
//                        while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
//                            Timber.i("happening")
//                            outputStream.write(buffer, 0, bytesRead)
//                        }
//                        uploadDocument(tempFile.absolutePath)
//                        outputStream.close()
//                        inputStream?.close()
//
//                    } else {
//                        // Handle the error
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//            })
//        } catch (e: Exception) {
//            Timber.e(e.message)
//        }
    }

//    private fun uploadDocument(docPath: String) {
//        viewModel.uploadDocumentToInfura(docPath)
//    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == Activity.RESULT_OK) {
//            val fileUri = data?.data
//            val contentResolver: ContentResolver = requireContext().contentResolver
//            val inputStream: InputStream? = fileUri?.let { contentResolver.openInputStream(it) }
//
//            inputStream?.let {
//                try {
//                    // Create a temporary file in your app's cache directory
//                    val tempFile = File(requireContext().cacheDir, "temp_file")
//                    val outputStream = FileOutputStream(tempFile)
//
//                    // Copy data from the input stream to the temporary file
//                    val bufferSize = 1024
//                    val buffer = ByteArray(bufferSize)
//                    var bytesRead: Int
//                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
//                        outputStream.write(buffer, 0, bytesRead)
//                    }
//
//                    // Close the streams
//                    inputStream.close()
//                    outputStream.close()
//
//                    uploadDocument(tempFile.absolutePath)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        binding.promptTextInput.setText("")
    }

    private fun initClickListeners() {
        binding.prepareDocBtn.setOnClickListener {
            if (binding.promptTextInput.text.toString().isNullOrEmpty())
                binding.root.showSnackBar("Describe your article")
            else {
                generateAgreement("create one funny joke in one line")
//                generateAgreement(Utility.appendCustomDocGenerationPropmt(binding.promptTextInput.text.toString()))
            }
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