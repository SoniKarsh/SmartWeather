package com.technobugsai.smartweather.appview.auth

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import com.lassi.presentation.cropper.CropImageView
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.appview.auth.imgpicker.ImagePickerBottomSheet
import com.technobugsai.smartweather.databinding.FragmentSignupBinding

class SignUpFragment: Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var imagePickerBottomSheet: ImagePickerBottomSheet

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
    }

    private fun setClickListeners() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToAuthFragment())
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