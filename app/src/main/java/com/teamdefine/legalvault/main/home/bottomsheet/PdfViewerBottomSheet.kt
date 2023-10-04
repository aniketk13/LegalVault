package com.teamdefine.legalvault.main.home.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamdefine.legalvault.databinding.LayoutPdfViewerBinding
import com.teamdefine.legalvault.main.home.mydocs.MyDocumentsVM

class PdfViewerBottomSheet : BottomSheetDialogFragment() {
    private val bottomSheetVM: MyDocumentsVM by activityViewModels()
    private lateinit var binding: LayoutPdfViewerBinding
    private lateinit var pdfUrl: String

    companion object {
        fun newInstance(
            pdfUrl: String
        ): PdfViewerBottomSheet {
            val fragment = PdfViewerBottomSheet()
            val args = Bundle().apply {
                putString("PDF_URL", pdfUrl)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutPdfViewerBinding.inflate(layoutInflater, container, false).also {
        binding = it
        pdfUrl = arguments?.getString("PDF_URL")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bottomSheetVM.getFile(pdfUrl)
        loadWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        binding.apply {
            loadingModel.progressBar.visibility = View.VISIBLE
            pdfWebView.settings.javaScriptEnabled = true

            // Set up Basic Authentication credentials
            val username = "9263a4bc250926733b7a066634f918fe44ecd02d08c54d08ccad7c485d05bfdb"
            val password = ""
            val authHeader = "Basic " + Base64.encodeToString(
                "$username:$password".toByteArray(),
                Base64.NO_WRAP
            )

            pdfWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    // Load the URL with Basic Authentication header
                    view?.loadUrl(pdfUrl, mapOf("Authorization" to authHeader))

                    loadingModel.progressBar.visibility = View.GONE
                }
            }

            // Load the URL in the WebView
            pdfWebView.loadUrl(pdfUrl)
        }
    }

}