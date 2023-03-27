package com.technobugsai.smartweather.appview.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.technobugsai.smartweather.MainActivity
import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import com.technobugsai.smartweather.databinding.FragmentAuthBinding
import com.technobugsai.smartweather.utils.KeyboardUtils
import com.technobugsai.smartweather.utils.extensions.showSnack
import kotlinx.coroutines.flow.*
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
        observeProgress()
        showSnackBar()
        observeLogin()
        handleBack()
    }

    private fun handleBack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).showHideProgress(false)
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun observeLogin() {
        lifecycleScope.launch {
            viewModel.logInSuccess.collectLatest {
                if (it) {
                    findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToUserProfileFragment())
                }
            }
        }
    }

    private fun showSnackBar() {
        lifecycleScope.launch {
            viewModel.snackBar.collectLatest {
                if (it.isNotEmpty()) {
                    requireView().showSnack(it)
                }
            }
        }
    }

    private fun observeProgress(){
        lifecycleScope.launch {
            viewModel.progressBar.collectLatest {
                (requireActivity() as MainActivity).showHideProgress(it)
            }
        }
    }

    private fun observeErrors() {
        viewModel.run {
            binding.run {
                lifecycleScope.launch {
                    emailError.collectLatest { email ->
                        if (email.isEmpty()) {
                            tilEmail.isErrorEnabled = false
                        } else {
                            tilEmail.error = email
                        }
                    }
                }
                lifecycleScope.launch {
                    pwdError.collectLatest { pwd ->
                        if (pwd.isEmpty()) {
                            tilPassword.isErrorEnabled = false
                        } else {
                            tilPassword.error = pwd
                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.btnSignup.setOnClickListener {
            KeyboardUtils.hideKeyboard(requireActivity())
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToSignUpFragment())
        }
        binding.btnLogin.setOnClickListener {
            KeyboardUtils.hideKeyboard(requireActivity())
            binding.run {
                viewModel.signInUser(
                    etEmail.text?.trim().toString(),
                    etPassword.text?.trim().toString()
                )
            }
        }
    }

}