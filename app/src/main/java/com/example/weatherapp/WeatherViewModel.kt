package com.example.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Api.Constant
import com.example.weatherapp.Api.NetworkResponse
import com.example.weatherapp.Api.RetrofitInstance
import com.example.weatherapp.Api.WeatherModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class WeatherViewModel:ViewModel() {

  private  val weatherApi = RetrofitInstance.weatherApi
    private val weatherResults = MutableLiveData<NetworkResponse<WeatherModel>>()
     val weatherResult : LiveData<NetworkResponse<WeatherModel>> = weatherResults

    fun getData (city : String){
//        Log.d("City name", city)

                    weatherResults.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
     val response = weatherApi.getWeather(Constant.apiKey,city)
            if (response.isSuccessful){
//                Log.d("Response :" , response.body().toString())
                response.body()?.let {
                    weatherResults.value = NetworkResponse.Success(it)
                }

            }else{
                    weatherResults.value = NetworkResponse.Error("Failed to load data")
//                Log.d("Error :" , response.message())

            }

            }catch (e: Exception){
                    weatherResults.value = NetworkResponse.Error("Failed to load data")

            }

        }

    }

}