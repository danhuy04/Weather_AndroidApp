package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HourlyForecastAdapter(private val forecastItems: List<ForecastItem>) : 
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder>() {

    class HourlyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
        val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val item = forecastItems[position]
        
        // Format thời gian
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = sdf.format(Date(item.dt * 1000))
        holder.tvTime.text = time
        
        // Hiển thị nhiệt độ
        holder.tvTemp.text = "${item.main.temp.toInt()}°C"
        
        // Load icon thời tiết
        val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
        val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
        Glide.with(holder.itemView.context)
            .load(iconUrl)
            .into(holder.ivWeatherIcon)
    }

    override fun getItemCount() = forecastItems.size
} 