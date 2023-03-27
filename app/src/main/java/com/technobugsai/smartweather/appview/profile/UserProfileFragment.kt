package com.technobugsai.smartweather.appview.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.technobugsai.smartweather.appview.profile.adapter.WeatherListAdapter
import com.technobugsai.smartweather.appview.viewmodel.UserProfileViewModel
import com.technobugsai.smartweather.databinding.FragmentUserProfileBinding
import com.technobugsai.smartweather.model.UserProfileModel
import com.technobugsai.smartweather.model.weather.ResCurrentWeatherModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class UserProfileFragment: Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var userModel: UserProfileModel
    private lateinit var weatherListAdapter: WeatherListAdapter
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
        initAdapter()
        observeUser()
        observeCityModel()
        setClickListeners()
        observeWeatherModel()
        setBackArrow()
    }

    private fun setBackArrow() {
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
    }

    private fun initAdapter(){
        if (::weatherListAdapter.isInitialized.not()) {
            weatherListAdapter = WeatherListAdapter(arrayListOf()) { model ->
                viewModel.emitWeatherItem(model)
                findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToWeatherDetailsFragment())
            }
        }
        binding.rvList.run {
            adapter = weatherListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeCityModel() {
        lifecycleScope.launch {
            viewModel.selectedModel.collectLatest {
                it?.run {
                    if (binding.cityName.text != name) {
                        binding.cityName.text = name
                        viewModel.getWeatherData(this)
                    }
                }
            }
        }
    }

    private fun observeWeatherModel() {
        lifecycleScope.launch {
            viewModel.weatherModel.collectLatest {
                it?.run {
                    val uniqueDates = mutableListOf<String>()
                    val finalList = mutableListOf<ResCurrentWeatherModel.WeatherItem>()
                    list.forEach {
                        val date = it.dt_txt?.split(" ")?.get(0)
                        if (!uniqueDates.contains(date)) {
                            uniqueDates.add(date ?: "")
                            finalList.add(it)
                        }
                    }
                    weatherListAdapter.updateList(finalList)
                }
            }
        }
    }

    private fun setClickListeners(){
        binding.cityName.setOnClickListener {
            findNavController().navigate(UserProfileFragmentDirections.actionUserProfileFragmentToSearchCityFragment())
        }
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