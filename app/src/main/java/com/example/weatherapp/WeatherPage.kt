package com.example.weatherapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weatherapp.Api.NetworkResponse
import com.example.weatherapp.Api.WeatherModel

@Composable
fun WeatherPage(viewModel: WeatherViewModel) {

    var city by remember {
        mutableStateOf("")
    }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundResource = remember ( weatherResult.value){
       when (val result  = weatherResult.value){
           is NetworkResponse.Success -> getBackgroundResource(result.data.current.condition.text)
           else -> R.drawable.background // Default background if no data or in loading/error state
       }
    }


//    val backgroundResource = remember(weatherResult.value) {
//        when (val result = weatherResult.value) {
//            is NetworkResponse.Success -> getBackgroundResource(result.data.current.condition.text)
//            else -> R.drawable.default_background // Default background if no data or in loading/error state
//        }
//    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = backgroundResource),
            contentDescription = "Sunny",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Optional: Adjust how the image scales
        )


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = city,
                    onValueChange = {
                        city = it
                    },
                    label = { Text(text = "Search For Location") }
                )
                IconButton(onClick = {
                    viewModel.getData(city)
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search For Location"
                    )

                }
            }

            when (val result = weatherResult.value) {
                is NetworkResponse.Error -> {
                    Text(text = result.message)
                }

                NetworkResponse.Loading -> {
                    CircularProgressIndicator()
                }

                is NetworkResponse.Success -> {
                    WeatherUi(data = result.data)
                }

                null -> {}
            }
        }
    }
}

@Composable
fun WeatherUi(data: WeatherModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = data.location.name, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.location.country, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = " ${data.current.temp_c} ° c",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center

        )
        AsyncImage(
            modifier = Modifier.size(160.dp),
            model = "https:${data.current.condition.icon}".replace("64*64", "128*128"),
            contentDescription = "Condition Icon"
        )
        Text(
            text = data.current.condition.text,
            fontSize = 24.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center

        )
        Spacer(modifier = Modifier.height(16.dp))

            Card (
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ){
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal(
                            key = "Local Date",
                            value = data.location.localtime.split(" ")[0]
                        )
                        WeatherKeyVal(
                            key = "Local Time",
                            value = data.location.localtime.split(" ")[1]
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherKeyVal(key = "Humidity", value = data.current.humidity)
                        WeatherKeyVal(key = "Wind Speed", value = data.current.wind_kph)
                    }
                }
            }
        }
    }


@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column (modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
        Text(text = key, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

fun getBackgroundResource(condition: String): Int {
    return when (condition) {
        "Sunny" -> R.drawable.sunny
        "Rain" -> R.drawable.rain
        "Cloudy" -> R.drawable.cloudy
        "Torrential rain shower" -> R.drawable.rain
        "Moderate or heavy rain with thunder"  -> R.drawable.heavyrain
        "Partly cloudy"  -> R.drawable.cloudy
        "Overcast"  -> R.drawable.cloudy
        "Patchy light rain with thunder"  -> R.drawable.heavyrain
        "Clear"  -> R.drawable.clear
        "Mist"  -> R.drawable.mist
        "Moderate rain" -> R.drawable.rain
        // Add more conditions as needed
        else -> R.drawable.background
    }
}
