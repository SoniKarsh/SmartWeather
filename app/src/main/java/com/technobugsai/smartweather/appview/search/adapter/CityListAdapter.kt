package com.technobugsai.smartweather.appview.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.technobugsai.smartweather.databinding.ItemCityListBinding
import com.technobugsai.smartweather.model.weather.ResCityModel

class CityListAdapter(
    private val cities: List<ResCityModel>,
    private val onClickListener: (ResCityModel) -> Unit
) : RecyclerView.Adapter<CityListAdapter.ViewHolder>(), Filterable {

    private var filteredCities: List<ResCityModel> = cities

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemCityListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = filteredCities[position]
        holder.bind(filteredCities[position])
        holder.itemView.setOnClickListener { onClickListener(city) }
    }

    override fun getItemCount() = filteredCities.size

    inner class ViewHolder(val binding: ItemCityListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(city: ResCityModel) {
            binding.tvCityName.text = city.name
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = if (constraint.isNullOrEmpty()) {
                    cities
                } else {
                    cities.filter { it.name.contains(constraint, true) }
                }
                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredCities = results?.values as List<ResCityModel>
                notifyDataSetChanged()
            }
        }
    }
}
