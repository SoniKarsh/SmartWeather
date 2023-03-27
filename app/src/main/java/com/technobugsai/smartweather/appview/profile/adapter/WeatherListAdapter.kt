package com.technobugsai.smartweather.appview.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technobugsai.smartweather.R
import com.technobugsai.smartweather.databinding.ItemWeatherBinding
import com.technobugsai.smartweather.model.weather.ResCurrentWeatherModel

class WeatherListAdapter(
    private val list: List<ResCurrentWeatherModel.WeatherItem>,
    private val onClickListener: (ResCurrentWeatherModel.WeatherItem) -> Unit
) : RecyclerView.Adapter<WeatherListAdapter.ViewHolder>() {

    private var weathers: ArrayList<ResCurrentWeatherModel.WeatherItem> = list as ArrayList<ResCurrentWeatherModel.WeatherItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemWeatherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    fun updateList(list: List<ResCurrentWeatherModel.WeatherItem>) {
        weathers.clear()
        weathers.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weathers[position]
        holder.bind(weathers[position])
        holder.itemView.setOnClickListener { onClickListener(weather) }
    }

    override fun getItemCount() = weathers.size

    inner class ViewHolder(val binding: ItemWeatherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: ResCurrentWeatherModel.WeatherItem) {
            binding.run {
                val date = weather.dt_txt?.split(" ")
                tvDateVal.text = date?.get(0)
                tvTimeVal.text = date?.get(1)
                tvHumidityVal.text = root.context.getString(R.string.humidity_val, weather.main.humidity.toString())
                tvTempVal.text = root.context.getString(R.string.temperature_val, weather.main.temp.toString())
            }
        }
    }

}