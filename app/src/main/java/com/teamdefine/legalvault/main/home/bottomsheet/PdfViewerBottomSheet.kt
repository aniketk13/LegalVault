package com.teamdefine.legalvault.main.home.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamdefine.legalvault.databinding.LayoutPdfViewerBinding

class PdfViewerBottomSheet : BottomSheetDialogFragment() {
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
        loadWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView() {
        binding.apply {
            loadingModel.progressBar.visibility = View.VISIBLE
            pdfWebView.settings.javaScriptEnabled = true
            pdfWebView.loadUrl(pdfUrl)
            pdfWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadingModel.progressBar.visibility = View.GONE
                }
            }
        }
    }
}