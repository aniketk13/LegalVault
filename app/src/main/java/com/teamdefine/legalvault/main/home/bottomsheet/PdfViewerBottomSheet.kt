package com.teamdefine.legalvault.main.home.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamdefine.legalvault.databinding.LayoutPdfViewerBinding
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel

class PdfViewerBottomSheet : BottomSheetDialogFragment() {
    private val pdfViewerBottomSheet: PdfViewerVM by viewModels()
    private lateinit var binding: LayoutPdfViewerBinding
    private lateinit var signatureId: String
    private var downloadId: Long = 0

    companion object {
        fun newInstance(
            signatureId: String
        ): PdfViewerBottomSheet {
            val fragment = PdfViewerBottomSheet()
            val args = Bundle().apply {
                putString("SIGNATURE_ID", signatureId)
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
        signatureId = arguments?.getString("SIGNATURE_ID")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserser()
        pdfViewerBottomSheet.getDocHashFromFirebase(signatureId)
    }

    private fun initObserser() {
        pdfViewerBottomSheet.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.root.setVisibilityBasedOnLoadingModel(it)
        })
        pdfViewerBottomSheet.docHash.observe(viewLifecycleOwner, EventObserver {
            it?.let { hash ->
                setDataInWebView(hash)
            }
        })
    }

    private fun setDataInWebView(hashValue: String) {
        binding.pdfWebView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl("$hashValue.ipfs.dweb.link")
        }
    }

//    private fun downloadPdf(pdfUrl: String) {
//        val downloadManager =
//            requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//        val request = DownloadManager.Request(Uri.parse(pdfUrl))
//            .setTitle("PDF Download")
//            .setDescription("Downloading PDF")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            .setDestinationInExternalPublicDir(
//                "PDFs", // Change this to your desired directory
//                "my_sign_pdf.pdf"
//            )
//
//        downloadId = downloadManager.enqueue(request)
//
//        // Register a BroadcastReceiver to receive download completion
//        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        requireContext().registerReceiver(downloadReceiver, filter)
//    }

//    private val downloadReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) == downloadId) {
//                // Download completed
//                openPdf()
//            }
//        }
//    }

//    private fun openPdf() {
//        val pdfFile = Uri.parse("file:///storage/emulated/0/PDFs/my_sign_pdf.pdf")
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(pdfFile, "application/pdf")
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        try {
//            startActivity(intent)
//        } catch (e: Exception) {
//            // Handle exceptions (e.g., PDF viewer not found)
//        }
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        requireContext().unregisterReceiver(downloadReceiver)
//    }

//    private fun initWebView(fileUrl: String?) {
//        fileUrl?.let {
//            binding.pdfWebView.apply {
//                settings.javaScriptEnabled = true
//                webViewClient = WebViewClient()
//                webChromeClient = WebChromeClient()
//                loadUrl(it)
//            }
//        }
//    }
}