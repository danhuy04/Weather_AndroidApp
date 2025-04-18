package com.example.weatherapp


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.color.utilities.ViewingConditions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//45b7b0593a960a0af811bda6622784a2
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fetchWeatherData("Ho Chi Minh") // Đã bỏ tham số name "cityName" vì không hợp lệ trong Kotlin
        searchCity() // Sửa thành chữ thường theo convention Kotlin
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Có thể thêm logic filter real-time ở đây nếu cần
                return true  // Không xử lý sự kiện text change
            }
        })
    }


    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
            .create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(
            cityName,
            "45b7b0593a960a0af811bda6622784a2",
            "metric"
        )

        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "Unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val timezoneOffset = responseBody.timezone

                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunSet.text = time(sunSet,timezoneOffset)
                    binding.sunRise.text = time(sunRise,timezoneOffset)
                    binding.seaLevel.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = cityName

                    // ✅ gọi hàm đã sửa để thay đổi background chính xác theo múi giờ
                    changeImageAccordingToWeatherCondition(condition, sunRise, sunSet, timezoneOffset)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                Log.e("WeatherApp", "API call failed: ${t.message}")
            }
        })
    }



    private fun changeImageAccordingToWeatherCondition(condition: String, sunrise: Long, sunset: Long, timezoneOffset: Int) {
        val currentUtc = System.currentTimeMillis() / 1000
        val localTime = currentUtc + timezoneOffset
        val isNight = localTime < sunrise || localTime > sunset


        if (isNight) {
            when (condition) {
                "Clear Sky", "Sunny", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.night_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night)
                }
                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.nightfog_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloudnight)
                }
                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.nightrain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rainnight)
                }
                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.night_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snownight)
                }
                else -> {
                    binding.root.setBackgroundResource(R.drawable.night_background)
                    binding.lottieAnimationView.setAnimation(R.raw.night)
                }
            }
        } else {
            when (condition) {
                "Clear Sky", "Sunny", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.cloud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }
                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
                }
                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                }
                else -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
            }
        }

        binding.lottieAnimationView.playAnimation()
    }





    private fun date(): String {
        val sdf = SimpleDateFormat("dd-MMMM-yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long, offset: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("GMT")
        return sdf.format(Date((timestamp + offset) * 1000L))
    }


    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}




//ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets