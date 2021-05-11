package com.devhypercoder.securebrowser.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.devhypercoder.securebrowser.Constants
import com.devhypercoder.securebrowser.R
import com.devhypercoder.securebrowser.UserVIewModel
import org.mindrot.jbcrypt.BCrypt

class LoginFragment : Fragment() {

    private val userView: UserVIewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    private fun setPassword(password: String) {
        val salt = BCrypt.gensalt()
        val hashedPw = BCrypt.hashpw(password, salt)

        activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
            ?.putString(Constants.PASSWORD_KEY, hashedPw)
            ?.apply()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val hashPw =
            activity?.getPreferences(Context.MODE_PRIVATE)?.getString(Constants.PASSWORD_KEY, "")
        if (hashPw == "") {
            setPassword(Constants.DEFAULT_PASSWORD)
        }

        val passwdEditText: EditText = requireView().findViewById(R.id.password_input)
        val passwdBtn: Button = requireView().findViewById(R.id.password_btn)

        passwdBtn.setOnClickListener {
            if (BCrypt.checkpw(passwdEditText.text.toString(), hashPw)) {
                userView.isLoggedIn.value = true
                findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
            }
        }

        val changePasswordText: TextView = requireView().findViewById(R.id.change_pass_text)
        changePasswordText.setOnClickListener {
            loginViewModel.isChangePassword.value = !loginViewModel.isChangePassword.value!!
        }

        requireView().findViewById<Button>(R.id.change_pass_btn).setOnClickListener {
            val origPasswd: EditText = requireView().findViewById(R.id.original_password)
            val newPasswd: EditText = requireView().findViewById(R.id.new_password)
            val confirmNewPasswd: EditText = requireView().findViewById(R.id.new_confirm_password)

            val message = if (BCrypt.checkpw(origPasswd.text.toString(), hashPw)) {
                if (newPasswd.text.toString() == confirmNewPasswd.text.toString()) {
                    setPassword(newPasswd.text.toString())
                    Constants.PASSWORD_CHANGE_SUCCESS
                } else {
                    Constants.PASSWORD_NO_MATCH
                }
            } else {
                Constants.ORIGINAL_PASSWORD_WRONG
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isChangePassword.observe(viewLifecycleOwner) {
            val changePasswordLayout: RelativeLayout =
                requireView().findViewById(R.id.change_pass_screen)
            changePasswordLayout.visibility = if (it) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

    }

}

class LoginViewModel : ViewModel() {
    val isChangePassword = MutableLiveData(false)

}