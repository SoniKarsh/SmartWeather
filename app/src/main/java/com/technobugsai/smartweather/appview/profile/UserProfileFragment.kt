package com.technobugsai.smartweather.appview.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.auth.User
import com.technobugsai.smartweather.appview.viewmodel.UserProfileViewModel
import com.technobugsai.smartweather.databinding.FragmentUserProfileBinding
import com.technobugsai.smartweather.model.UserProfileModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class UserProfileFragment: Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userModel: UserProfileModel
    private val viewModel by activityViewModel<UserProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUser()
    }

    private fun observeUser() {
        lifecycleScope.launch {
            viewModel.userModel.collectLatest {
                it?.let { user ->
                    userModel = user
                    binding.run {
                        tvUserBio.text = user.shortBio
                        tvUserName.text = user.userName
                        Glide.with(requireContext())
                            .load(it.userProfile)
                            .apply(RequestOptions()
                                .circleCrop())
                            .into(ivImageDisplay)
                    }
                }
            }
        }
    }


}