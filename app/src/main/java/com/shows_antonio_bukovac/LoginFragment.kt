package com.shows_antonio_bukovac

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shows_antonio_bukovac.databinding.FragmentLoginBinding
import com.shows_antonio_bukovac.model.PrefsConstants
import com.shows_antonio_bukovac.view_models.LoginViewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = activity?.getPreferences(Context.MODE_PRIVATE)
        val rememberMe = prefs?.getBoolean(PrefsConstants.REMEMBER_ME, false)
        val cameFromRegister = prefs?.getBoolean(PrefsConstants.CAME_FROM_REGISTRATION, false)

        if(cameFromRegister == true){
            binding.apply {
                goToRegistrationButton.isVisible = false
                loginTextView.text = getString(R.string.label_registration_successful)
            }
            prefs?.edit()?.putBoolean(PrefsConstants.CAME_FROM_REGISTRATION, false)?.apply()
        }

        if (rememberMe == true) {
            navigateToShowsFragment()
        }

        mailAndPasswordCheck()
        initLoginButton()
        initRegistrationButton()
    }

    private fun initRegistrationButton() {
        binding.goToRegistrationButton.setOnClickListener {
            LoginFragmentDirections.loginToRegistration().also {
                findNavController().navigate(it)
            }
        }
    }

    private fun navigateToShowsFragment() {
        LoginFragmentDirections.actionLoginToShows().also {
            findNavController().navigate(it)
        }
    }

    private fun mailAndPasswordCheck() {
        //Error message handling
        binding.emailTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (isMailValid(s.toString())) {
                    binding.emailTextInput.error = null
                } else {
                    binding.emailTextInput.error = "Invalid email address"
                }

                validateButton()
            }
        })

        //Password handling
        binding.passwordTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (isPasswordValid(s.toString())) {
                    binding.passwordTextInput.error = null
                } else {
                    binding.passwordTextInput.error = "Password too short"
                }

                validateButton()
            }
        })
    }

    private fun validateButton() {
        val password = binding.passwordTextInput.text.toString()
        val mail = binding.emailTextInput.text.toString()

        if (isPasswordValid(password) && isMailValid(mail)) {
            binding.loginButton.isEnabled = true
            binding.loginButton.isClickable = true
            binding.loginButton.setBackgroundColor(Color.parseColor("#FFFFFF"))
            binding.loginButton.setTextColor(Color.parseColor("#52368C"))
        } else {
            binding.loginButton.isEnabled = false
            binding.loginButton.isClickable = false
            binding.loginButton.setBackgroundColor(Color.parseColor("#BBBBBB"))
            binding.loginButton.setTextColor(Color.parseColor("#FFFFFF"))
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun isMailValid(mail: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail.trim()).matches()
    }

    private fun initLoginButton() {
        viewModel.getLoginResultLiveData().observe(this.viewLifecycleOwner) { isLoginSuccessful ->
            if (isLoginSuccessful) {
                navigateToShowsFragment()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.toast_unsuccessful_login),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.loginButton.setOnClickListener {
            // TODO: Add shared prefs to method
            prefs?.edit()?.putString(PrefsConstants.USER_EMAIL, getEmail())?.apply()
            viewModel.signIn(
                binding.emailTextInput.text.toString(),
                binding.passwordTextInput.text.toString(),
                prefs
            )
        }

        if (binding.rememberMeCheckbox.isChecked) {
            prefs?.edit()?.putBoolean(PrefsConstants.REMEMBER_ME, true)?.apply()
        }
    }

    private fun getEmail(): String {
        return binding.emailTextInput.text.toString().trim()
    }
}