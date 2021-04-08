package com.devhypercoder.securebrowser.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.devhypercoder.securebrowser.R
import com.devhypercoder.securebrowser.UserVIewModel

class MainFragment : Fragment() {

    private val userViewModel: UserVIewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        userViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }

        val webView: WebView = requireView().findViewById(R.id.message)
        val urlEditText: EditText = requireView().findViewById(R.id.editText)
        val goToURLBtn: Button = requireView().findViewById(R.id.url_go_bt)
        val backBtn: Button = requireView().findViewById(R.id.back_btn)

        goToURLBtn.setOnClickListener {
            webView.loadUrl(urlEditText.text.toString())
        }

        backBtn.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.loadUrl(urlEditText.text.toString())
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }

    }

    override fun onPause() {
        super.onPause()
        userViewModel.isLoggedIn.removeObservers(viewLifecycleOwner)
        userViewModel.isLoggedIn.value = false
    }

    override fun onResume() {
        super.onResume()
        userViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }
    }
}