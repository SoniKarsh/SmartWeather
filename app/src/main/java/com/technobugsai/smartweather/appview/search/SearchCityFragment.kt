package com.technobugsai.smartweather.appview.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.technobugsai.smartweather.MainActivity
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.appview.profile.adapter.WeatherListAdapter
import com.technobugsai.smartweather.appview.search.adapter.CityListAdapter
import com.technobugsai.smartweather.appview.viewmodel.UserProfileViewModel
import com.technobugsai.smartweather.databinding.FragmentSearchBinding
import com.technobugsai.smartweather.model.weather.ResCityModel
import com.technobugsai.smartweather.utils.AppUtils.toList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class SearchCityFragment: Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var cityListAdapter: CityListAdapter
    private val viewModel by activityViewModel<UserProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setBackArrow()
    }

    private fun setBackArrow(){
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
    }

    private fun readFromJsonRaw(): String {
        val inputStream = requireContext().resources.openRawResource(R.raw.city_list)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        inputStream.close()
        bufferedReader.close()
        return stringBuilder.toString()
    }

    private fun showHidePb(shouldShow: Boolean){
        (requireActivity() as MainActivity).run {
            if (shouldShow) {
                showHideProgress(true)
            } else {
                showHideProgress(false)
            }
        }
    }

    private suspend fun getListOfCities(): List<ResCityModel> {
        return readFromJsonRaw().toList<ResCityModel>() ?: arrayListOf()
    }

    private fun initAdapter(){
        showHidePb(true)
        lifecycleScope.launch(Dispatchers.IO) {
            var cities = arrayListOf<ResCityModel>()
            runBlocking {
                cities = getListOfCities() as ArrayList<ResCityModel>
            }
            withContext(Dispatchers.Main) {
                if (::cityListAdapter.isInitialized.not()) {
                    cityListAdapter = CityListAdapter(cities) { model ->
                        binding.searchView.setQuery(model.name, false)
                        viewModel.selectedModel.tryEmit(model)
                        findNavController().popBackStack()
                    }
                }
                binding.rvSearchResult.run {
                    adapter = cityListAdapter
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                }
                showHidePb(false)
            }
        }
        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::cityListAdapter.isInitialized) {
                    cityListAdapter.filter.filter(newText)
                }
                return false
            }
        })
    }

}