package com.devhypercoder.securebrowser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserVIewModel : ViewModel() {
    val isLoggedIn = MutableLiveData(false)
}