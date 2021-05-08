package com.devhypercoder.securebrowser.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.util.Log
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
import com.devhypercoder.securebrowser.VideoWebChromeClient
import com.devhypercoder.securebrowser.data.AppDatabase
import com.devhypercoder.securebrowser.data.History
import com.devhypercoder.securebrowser.data.HistoryDao
import com.devhypercoder.securebrowser.helper.handleCommand
import com.devhypercoder.securebrowser.helper.isCommand
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class MainFragment : Fragment() {

    private val userViewModel: UserVIewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private lateinit var webView: WebView;
    lateinit var historyDao: HistoryDao;

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = AppDatabase.getAppDatabase(requireContext())
        historyDao = db?.historyDao()!!

        userViewModel.isLoggedIn.observe(viewLifecycleOwner) {
            if (!it) {
                findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            }
        }

        webView = requireView().findViewById(R.id.message)
        val urlEditText: EditText = requireView().findViewById(R.id.editText)
        val goToURLBtn: Button = requireView().findViewById(R.id.url_go_bt)
        val backBtn: Button = requireView().findViewById(R.id.back_btn)

        goToURLBtn.setOnClickListener {
            val url = urlEditText.text.toString()
            if (isCommand(url)) {
                MainScope().launch {
                    val cmd = url.substring(1)
                    val html = handleCommand(db, historyDao, cmd)
                    webView.loadData(
                        Base64.encodeToString(
                            html.toByteArray(StandardCharsets.UTF_8),
                            Base64.DEFAULT
                        ), // encode in Base64 encoded
                        "text/html; charset=utf-8", // utf-8 html content (personal recommendation)
                        "base64"
                    ); // always use Base64 encoded data: NEVER PUT "utf-8" here (using base64 or not): This is wrong!
                }
                return@setOnClickListener
            }
            webView.loadUrl(url)
        }

        backBtn.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.domStorageEnabled = true

        webView.loadUrl(urlEditText.text.toString())
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val title = view?.title
                val history = History(title!!, url!!)
                Log.d("SecureBrowser", "onPause: $history")
                MainScope().launch {
                    historyDao.insertHistory(history)
                }
            }
        }

        val mainView = requireView().findViewById<ViewGroup>(R.id.main)
        val videoView = requireView().findViewById<ViewGroup>(R.id.video_layout)

        val videoWebChromeClient = VideoWebChromeClient(
            mainView, videoView
        )

        webView.webChromeClient = videoWebChromeClient
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

