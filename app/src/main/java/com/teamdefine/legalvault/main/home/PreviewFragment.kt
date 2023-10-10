package com.teamdefine.legalvault.main.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.teamdefine.legalvault.databinding.FragmentPreviewBinding
import com.teamdefine.legalvault.main.home.bottomsheet.PdfViewerVM
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel


class PreviewFragment : Fragment() {
    private val viewmodel: PdfViewerVM by viewModels()
    private val args: PreviewFragmentArgs by navArgs()
    private lateinit var binding: FragmentPreviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentPreviewBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        viewmodel.getDocHashFromFirebase(args.signId)
    }

    private fun initObservers() {
        viewmodel.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.root.setVisibilityBasedOnLoadingModel(it)
        })
        viewmodel.docHash.observe(viewLifecycleOwner, EventObserver {
            it?.let { hash ->
                openPdf(hash)
            }
        })
    }

    private fun openPdf(hashValue: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://$hashValue.ipfs.dweb.link/"))
        startActivity(browserIntent)
    }

    private fun setDataInWebView(hashValue: String) {
        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
        binding.pdfWebView.webViewClient = WebViewClient()

        // this will load the url of the website
        binding.pdfWebView.loadUrl("https://$hashValue.ipfs.dweb.link/")

        // this will enable the javascript settings, it can also allow xss vulnerabilities
        binding.pdfWebView.settings.javaScriptEnabled = true

        // if you want to enable zoom feature
        binding.pdfWebView.settings.setSupportZoom(true)
    }
}