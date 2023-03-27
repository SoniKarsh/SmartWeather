package com.technobugsai.smartweather.appview.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.lassi.presentation.cropper.CropImageView
import com.technobugsai.smartweather.MainActivity
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.appview.auth.imgpicker.ImagePickerBottomSheet
import com.technobugsai.smartweather.appview.viewmodel.AuthViewModel
import com.technobugsai.smartweather.databinding.FragmentSignupBinding
import com.technobugsai.smartweather.model.UserProfileModel
import com.technobugsai.smartweather.utils.AppUtils
import com.technobugsai.smartweather.utils.KeyboardUtils
import com.technobugsai.smartweather.utils.extensions.showSnack
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SignUpFragment: Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var imagePickerBottomSheet: ImagePickerBottomSheet
    private var userModel: UserProfileModel? = null
    private val viewModel by activityViewModel<AuthViewModel>()
    private var imgPath = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        observeErrors()
        observeProgress()
        showSnackBar()
        observeLogin()
    }

    private fun observeLogin() {
        lifecycleScope.launch {
            viewModel.logInSuccess.collectLatest {
                if (it) {
                    findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToUserProfileFragment())
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
                    uNameError.collectLatest { uNErr ->
                        if (uNErr.isEmpty()) {
                            tilUsername.isErrorEnabled = false
                        } else {
                            tilUsername.error = uNErr
                        }
                    }
                }
                lifecycleScope.launch {
                    conPwdError.collectLatest { conErr ->
                        if (conErr.isEmpty()) {
                            tilConfirmPassword.isErrorEnabled = false
                        } else {
                            tilConfirmPassword.error = conErr
                        }
                    }
                }
                lifecycleScope.launch {
                    aBioError.collectLatest { bErr ->
                        if (bErr.isEmpty()) {
                            tilBio.isErrorEnabled = false
                        } else {
                            tilBio.error = bErr
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
        binding.btnLogin.setOnClickListener {
            KeyboardUtils.hideKeyboard(requireActivity())
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToAuthFragment())
        }
        binding.btnSignup.setOnClickListener {
            KeyboardUtils.hideKeyboard(requireActivity())
            if (userModel == null) {
                userModel = UserProfileModel()
            }
            binding.run {
                userModel?.emailId = etEmail.text.toString()
                userModel?.password = etPassword.text.toString()
                userModel?.confirmPassword = etConfirmPassword.text.toString()
                userModel?.shortBio = etBio.text.toString()
                userModel?.userName = etUsername.text.toString()
                userModel?.userProfile = imgPath
            }
            viewModel.signUpUser(userModel!!)
        }
        binding.layoutProfilePicture.cardCamera.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        if (this::imagePickerBottomSheet.isInitialized && imagePickerBottomSheet.isAdded) {
            imagePickerBottomSheet.dismiss()
        }
        imagePickerBottomSheet = ImagePickerBottomSheet {
            if (it) {
                openCamera()
            } else {
                openGallery()
            }
        }
        imagePickerBottomSheet.show(childFragmentManager, ImagePickerBottomSheet.TAG)
    }

    private val receiveData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedMedia =
                    it.data?.getParcelableArrayListExtra<MiMedia>(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                if (selectedMedia.isNotEmpty()) {
                    selectedMedia.first().let { media ->
                        media.path?.let {
                            imgPath = it
                            binding.layoutProfilePicture.ivImageDisplay.visibility = View.VISIBLE
                            binding.layoutProfilePicture.ivImageDisplay.setImageDrawable(
                                AppUtils.returnCropped(resources,
                                    AppUtils.byteArrayToBitmap(
                                        AppUtils.getBytes(media.path!!)
                                    )!!
                                )
                            )
                        }
                    }
                }
            }
        }

    private fun openCamera() {
        requireActivity().let {
            val cameraIntent = Lassi(it)
                .with(LassiOption.CAMERA)
                .setMaxCount(1)
                .setMediaType(MediaType.IMAGE)
                .setStatusBarColor(R.color.black)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.toolbar_bg)
                .setErrorDrawable(R.drawable.ic_camera)
                .setCropType(CropImageView.CropShape.OVAL)
                .setCropAspectRatio(1, 1)
                .build()
            receiveData.launch(cameraIntent)
        }
    }

    private fun openGallery() {
        requireActivity().let {
            val galleryIntent = Lassi(it)
                .with(LassiOption.CAMERA_AND_GALLERY)
                .setMaxCount(1)
                .setMediaType(MediaType.IMAGE)
                .setStatusBarColor(R.color.black)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.toolbar_bg)
                .setErrorDrawable(R.drawable.ic_camera)
                .setCropType(CropImageView.CropShape.OVAL)
                .setCropAspectRatio(1, 1)
                .build()
            receiveData.launch(galleryIntent)
        }
    }

}