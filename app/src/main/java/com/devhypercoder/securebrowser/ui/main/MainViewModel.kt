package com.devhypercoder.securebrowser.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val url = MutableLiveData("https://google.com")
}