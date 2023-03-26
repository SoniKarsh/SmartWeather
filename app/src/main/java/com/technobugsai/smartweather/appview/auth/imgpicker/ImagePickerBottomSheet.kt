package com.technobugsai.smartweather.appview.auth.imgpicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.technobugsai.smartweather.databinding.BottomSheetImagePickerBinding

class ImagePickerBottomSheet(val callback: (Boolean) -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ImagePickerBottomSheetTag"
    }

    private var mBinding: BottomSheetImagePickerBinding? = null
    private var behavior: BottomSheetBehavior<FrameLayout>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = BottomSheetImagePickerBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        val dialog = dialog as BottomSheetDialog
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior?.skipCollapsed = false
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                @SuppressWarnings("EmptyFunctionBlock")
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            }
        )
    }

    private fun init() {
        mBinding?.tvCamera?.setOnClickListener {
            dismiss()
            callback(true)
        }
        mBinding?.tvPhotoLibrary?.setOnClickListener {
            dismiss()
            callback(false)
        }
    }
}
