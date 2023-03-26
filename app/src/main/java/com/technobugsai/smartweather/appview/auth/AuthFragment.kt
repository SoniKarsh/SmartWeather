package com.technobugsai.smartweather.appview.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import com.technobugsai.smartweather.databinding.FragmentAuthBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AuthFragment: Fragment() {

    private lateinit var binding: FragmentAuthBinding
    private val viewModel by activityViewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeErrors()
    }

    private fun observeErrors() {
        lifecycleScope.launch {
            viewModel.run {
                combine(emailError, pwdError) { email, pwd ->
                    binding.run {
                        if (email.isEmpty()) {
                            tilEmail.isErrorEnabled = false
                        } else {
                            binding.tilEmail.error = email
                        }
                        if (pwd.isEmpty()) {
                            tilPassword.isErrorEnabled = false
                        } else {
                            binding.tilPassword.error = pwd
                        }
                    }
                }.collectLatest { }
            }
        }
    }

    private fun setClickListeners() {
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToSignUpFragment())
        }
        binding.btnLogin.setOnClickListener {
            binding.run {
                viewModel.isValidationSuccessful(
                    etEmail.text?.trim().toString(),
                    etPassword.text?.trim().toString()
                )
            }
        }
    }

}