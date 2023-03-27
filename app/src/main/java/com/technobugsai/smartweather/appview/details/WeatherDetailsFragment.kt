package com.technobugsai.smartweather.appview.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.appview.viewmodel.UserProfileViewModel
import com.technobugsai.smartweather.databinding.FragmentWeatherDetailsBinding
import com.technobugsai.smartweather.model.UserProfileModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class WeatherDetailsFragment: Fragment() {

    private lateinit var binding: FragmentWeatherDetailsBinding
    private lateinit var userModel: UserProfileModel
    private val viewModel by activityViewModel<UserProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeatherDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeWeatherItem()
        setBackArrow()
    }

    private fun setBackArrow(){
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun observeWeatherItem() {
        lifecycleScope.launch {
            viewModel.weatherItem.collectLatest {
                binding.run {
                    it?.run {
                        val date = dt_txt?.split(" ")
                        tvDateVal.text = date?.get(0)
                        tvTimeVal.text = date?.get(1)
                        tvHumidityVal.text = getString(R.string.humidity_val, main.humidity.toString())
                        tvTempVal.text = getString(R.string.temperature_val, main.temp.toString())
                        tvWindVal.text = wind.speed.toString()
                        if(weather.isNotEmpty()){
                            tvTitleVal.text = weather[0].main
                            tvDescVal.text = weather[0].description
                        }
                    }
                }
            }
        }
    }

}