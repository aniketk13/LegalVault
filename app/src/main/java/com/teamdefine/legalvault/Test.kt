package com.teamdefine.legalvault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.teamdefine.legalvault.databinding.FragmentTestBinding


class Test : Fragment() {
    private lateinit var binding: FragmentTestBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentTestBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.webView.webChromeClient = WebChromeClient()
//        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl("https://app.hellosign.com/editor/embeddedSign?signature_id=ca4affe89cf8406b1259f431baeea2c3&token=e628dddcb19bd34d70c2267bcdd80c8f")
//        val hellosignEmbedded = HelloSignEmbedded(this,abc)
//        val helloSignClient = HelloSignClient(this, "YOUR_CLIENT_ID", "YOUR_API_KEY")
//        binding.webView.setWebViewClient(object : WebViewClient() {
        //        file:///android_asset/scratch.html
//            override fun onPageFinished(view: WebView, weburl: String) {
//                binding.webView.loadUrl("javascript:initializeHelloSign()")
//            }
//        })
    }
}