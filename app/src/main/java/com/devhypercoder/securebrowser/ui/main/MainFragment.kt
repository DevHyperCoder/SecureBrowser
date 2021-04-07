package com.devhypercoder.securebrowser.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
            Log.d("TAG", "onActivityCreated: $it")
            if (!it) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }

        val webView: WebView = requireView().findViewById(R.id.message)

        webView.loadUrl("https://youtube.com")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }
    }

}