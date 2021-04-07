package com.devhypercoder.securebrowser.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.devhypercoder.securebrowser.R
import com.devhypercoder.securebrowser.UserVIewModel

class LoginFragment : Fragment() {

    private val userView: UserVIewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val passwdEditText: EditText = requireView().findViewById(R.id.password_input)
        val passwdBtn: Button = requireView().findViewById(R.id.password_btn)
        passwdBtn.setOnClickListener {
            if (passwdEditText.text.toString() == "asdf") {
                userView.isLoggedIn.value = true
                Log.d("TAG", "onActivityCreated: reached here")
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }
        }

    }

}