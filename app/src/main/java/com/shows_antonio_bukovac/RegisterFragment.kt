package com.shows_antonio_bukovac

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shows_antonio_bukovac.databinding.FragmentRegisterBinding
import com.shows_antonio_bukovac.model.PrefsConstants
import com.shows_antonio_bukovac.view_models.RegistrationViewModel

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var prefs: SharedPreferences? = null

    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = activity?.getPreferences(Context.MODE_PRIVATE)

        mailAndPasswordCheck()
        initRegistrationButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRegistrationButton() {
        viewModel.getRegistrationResultLiveData()
            .observe(this.viewLifecycleOwner) { isRegisterSuccessful ->
                if (isRegisterSuccessful) {
                    navigateToLogin()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.toast_registration_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        binding.apply {
            registerButton.setOnClickListener {
                viewModel.register(
                    emailTextInput.text.toString(),
                    passwordTextInput.text.toString(),
                    repeatPasswordTextInput.text.toString()
                )
            }
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

        binding.repeatPasswordTextInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (isPasswordValid(s.toString())) {
                    if (getPassword() == getRepeatedPassword()) {
                        binding.repeatPasswordTextInput.error = null
                    } else {
                        binding.repeatPasswordTextInput.error = "Passwords don't match"
                    }
                } else {
                    binding.repeatPasswordTextInput.error = "Password too short"
                }

                validateButton()
            }
        })
    }

    private fun getPassword(): String {
        return binding.passwordTextInput.text.toString()
    }

    private fun getRepeatedPassword(): String {
        return binding.repeatPasswordTextInput.text.toString()
    }

    private fun validateButton() {
        val mail = binding.emailTextInput.text.toString()
        val password = getPassword()
        val repeatedPassword = getRepeatedPassword()

        if (isPasswordValid(password) && isMailValid(mail) && passwordsMatch(
                password,
                repeatedPassword
            )
        ) {
            binding.registerButton.apply {
                isEnabled = true
                isClickable = true
                setBackgroundColor(Color.parseColor("#FFFFFF"))
                setTextColor(Color.parseColor("#52368C"))
            }
        } else {
            binding.registerButton.apply {
                isEnabled = false
                isClickable = false
                setBackgroundColor(Color.parseColor("#BBBBBB"))
                setTextColor(Color.parseColor("#FFFFFF"))
            }
        }
    }

    private fun passwordsMatch(password: String, repeatedPassword: String): Boolean {
        Log.v("Istina******", password.equals(repeatedPassword).toString())
        return password.equals(repeatedPassword)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun isMailValid(mail: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail.trim()).matches()
    }


    private fun navigateToLogin() {
        prefs?.edit()?.putBoolean(PrefsConstants.CAME_FROM_REGISTRATION, true)?.apply()
        RegisterFragmentDirections.actionRegistrationToLogin().also {
            findNavController().navigate(it)
        }
    }

}
