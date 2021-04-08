package com.devhypercoder.securebrowser

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Remove screenshots and prevent from appearing in Recent App list
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.main_activity)
    }
}