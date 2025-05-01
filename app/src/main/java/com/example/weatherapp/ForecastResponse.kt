package com.example.weatherapp

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: ForecastCity
)

data class ForecastItem(
    val dt: Long,
    val main: WeatherMain,
    val weather: List<WeatherInfo>,
    val clouds: CloudsInfo,
    val wind: WindInfo,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String
)

data class ForecastCity(
    val id: Int,
    val name: String,
    val coord: CoordInfo,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class CoordInfo(
    val lat: Double,
    val lon: Double
)

data class WeatherMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Int,
    val temp_kf: Double
)

data class WeatherInfo(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class CloudsInfo(
    val all: Int
)

data class WindInfo(
    val speed: Double,
    val deg: Int,
    val gust: Double
)

data class ForecastSys(
    val pod: String
) 