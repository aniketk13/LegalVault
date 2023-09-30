package com.teamdefine.legalvault.main.signScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.evgenii.jsevaluator.JsEvaluator
import com.evgenii.jsevaluator.interfaces.JsCallback
import com.teamdefine.legalvault.databinding.FragmentSignScreenBinding


class SignScreenFragment : Fragment() {


    private lateinit var viewModel: SignScreenViewModel
    private lateinit var binding: FragmentSignScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSignScreenBinding.inflate(inflater, container, false).also {
        binding = it

    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Enable JavaScript
        val webSettings: WebSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true
        val jsEvaluator = JsEvaluator(requireContext())
        // Set WebViewClient to handle page navigation
        binding.webview.webViewClient = WebViewClient()

        // Set WebChromeClient for alert dialogs and other JavaScript interactions
        binding.webview.webChromeClient = WebChromeClient()

        jsEvaluator.evaluate("2 * 17", object : JsCallback {
            override fun onResult(result: String) {
                // Process result here.
                // This method is called in the UI thread.
            }

            override fun onError(errorMessage: String) {
                // Process JavaScript error here.
                // This method is called in the UI thread.
            }
        })
        // Load an empty HTML page with your JavaScript code
        val javascriptCode = """
            // Your JavaScript code with dependencies
            import HelloSign from "hellosign-embedded";

            const client = new HelloSign({
              clientId: "31bc2e280d7c0e7324b89bd3f3232a48"
            });

            client.open("https://app.hellosign.com/editor/embeddedSign?signature_id=86dd9f525358bf0c987205fc7005633b&token=01d1426d7214ccdfb1982da672b4cf6b", {
              testMode: true
            });
        """.trimIndent()
        binding.webview.loadUrl("javascript:callFunction()")

//        binding.webview.loadUrl("javascript:(function f() {import HelloSign from \"hellosign-embedded\";\n" +
//                "\n" +
//                "const client = new HelloSign({\n" +
//                "  clientId: \"31bc2e280d7c0e7324b89bd3f3232a48\"\n" +
//                "});\n" +
//                "\n" +
//                "client.open(\"https://app.hellosign.com/editor/embeddedSign?signature_id=86dd9f525358bf0c987205fc7005633b&token=01d1426d7214ccdfb1982da672b4cf6b\", {\n" +
//                "  testMode: true\n" +
//                "});} )()")
        binding.webview.loadDataWithBaseURL(null, javascriptCode, "text/html", "UTF-8", null)
        binding.webview.evaluateJavascript(javascriptCode) {

        }
    }

}
