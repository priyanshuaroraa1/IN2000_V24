package no.uio.ifi.in2000.martirhe.appsolution.ui.home

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.martirhe.appsolution.model.farevarsler.SimpleMetAlert
import no.uio.ifi.in2000.martirhe.appsolution.util.UiEvent

//import no.uio.ifi.in2000.martirhe.appsolution.ui.PocLocationForecast.LocationForecastUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {


    val oslo = LatLng(59.911491, 10.757933)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(homeViewModel.customMarkerLocation, 11f)
    }

    // Obtain a coroutine scope tied to the lifecycle of this composable
    val coroutineScope = rememberCoroutineScope()



    Box(modifier = Modifier.fillMaxSize()) {
        // The map goes here - it could be a composable that displays the map
        // Replace "MapComposable()" with your map


        GoogleMap(
            modifier = Modifier,
            cameraPositionState = cameraPositionState,
            onMapClick = {
                homeViewModel.onMapBackroundClick(it, coroutineScope, cameraPositionState)
            }
        ) {

            homeViewModel.badeplasser.forEach { badeplass ->
                Marker(
                    state = MarkerState(position = LatLng(badeplass.lat, badeplass.lon)),
                    onClick = {
                        homeViewModel.onBadeplassPinClick(badeplass)
                        false
                    }
                )
            }

            if (homeViewModel.showCustomMarker) {
                Marker(
                    state = MarkerState(position = homeViewModel.customMarkerLocation),

                    )
            }

        }

        if (homeViewModel.showBadeplassCard) {

            BadeplassInfoCard(
                homeViewModel = homeViewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
            )


        }


    }
}

@Composable
fun BadeplassInfoCard(
    homeViewModel: HomeViewModel,
    modifier: Modifier,
) {


    Card(
        modifier = modifier
//            .padding(32.dp)
//            .padding(bottom = 32.dp)
            .fillMaxWidth()
            .height(360.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )

    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = homeViewModel.selectedBadeplass.navn,
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                fontSize = 18.sp
            )

            val farevarselUiState by homeViewModel.farevarselUiState.collectAsState()

            farevarselUiState.farevarselUiState.let { state ->
                when (state) {
                    is FarevarselUiState.Success -> {

                        val koordinater = if (homeViewModel.showCustomMarker) {
                            LatLng(homeViewModel.customBadeplass.lat, homeViewModel.customBadeplass.lon)
                        } else {
                            LatLng(homeViewModel.selectedBadeplass.lat, homeViewModel.selectedBadeplass.lon)

                        }

                        FarevarselCard(simpleMetAlertList = state.simpleMetAlertList.filter {
                            it.isRelevantForCoordinate(koordinater)
                        })


                    }

                    is FarevarselUiState.Loading -> {
                        Text(text = "Loading")
                        Log.i("TestAlerts", "Loading")
                    }

                    is FarevarselUiState.Error -> {
                        Text(text = "Error")
                        Log.i("TestAlerts", "Error")
                    }
                }
            }


            val locationForecastUiState by homeViewModel.locationForecastUiState.collectAsState()

            locationForecastUiState.locationForecastUiState.let { state ->
                when (state) {
                    is LocationForecastUiState.Success -> {

                        WeatherCard(
                            temperature = state.locationForecast.properties.timeseries[0].data.instant.details.air_temperature,
                            windFromDirection = state.locationForecast.properties.timeseries[0].data.instant.details.windFromDirection,
                            windSpeed = state.locationForecast.properties.timeseries[0].data.instant.details.wind_speed
                        )


                    }

                    is LocationForecastUiState.Loading -> {
                        Text(text = "Loading")
                    }

                    is LocationForecastUiState.Error -> {
                        Text(text = "Error")
                    }
                }


                val oceanForecastUiState by homeViewModel.oceanForecastUiState.collectAsState()

                oceanForecastUiState.oceanForecastUiState.let { state ->
                    when (state) {
                        is OceanForecastUiState.Success -> {
                            WaterCard(
                                temperature = state.oceanForecast.properties.timeseries[0].data.instant.details.sea_water_temperature,
                                waveHeight = state.oceanForecast.properties.timeseries[0].data.instant.details.sea_surface_wave_height,
                                waveToDirection = state.oceanForecast.properties.timeseries[0].data.instant.details.sea_water_to_direction
                            )
                        }

                        is OceanForecastUiState.Loading -> {
                            Text(text = "Loading")
                        }

                        is OceanForecastUiState.Error -> {
                            Text(text = "Error")
                        }
                    }
                }


            }
        }


    }
}


@Composable
fun FarevarselCard(
    simpleMetAlertList: List<SimpleMetAlert>
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {

            Text(text = "${simpleMetAlertList.size} aktive farevarsler")
            if (simpleMetAlertList.size > 0) {
                simpleMetAlertList.forEach {
                    Text(text = "- " + it.awarenessType.split(';')[1] + ", " + it.awarenessLevel.split(';')[1].capitalize())
                }
            }
        }
    }

}

@Composable
fun WeatherCard(
    temperature: Double,
    windFromDirection: Double,
    windSpeed: Double,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(text = "Vær", fontWeight = FontWeight.ExtraBold)
            Text(text = "Temp: $temperature")
            Text(text = "Vind: $windSpeed m/s fra $windFromDirection")
        }

    }
}

@Composable
fun WaterCard(
    temperature: Double,
    waveHeight: Double,
    waveToDirection: Double,

    ) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = "Vann", fontWeight = FontWeight.ExtraBold)
            Text(text = "Temp: $temperature")
            Text(text = "Bølgehøyde: ${waveHeight}m fra $waveToDirection")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MapSearchBar(
    homeViewModel: HomeViewModel = viewModel()
) {
    SearchBar(
        query = homeViewModel.searchBarText,
        onQueryChange = { homeViewModel.searchBarText = it },
        onSearch = { homeViewModel.onSearch() },
        active = homeViewModel.searchBarActive,
        onActiveChange = { homeViewModel.searchBarActive = it },
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text(text = "Finn badeplass")
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            ) // TODO: Description?
        },
        trailingIcon = {
            if (homeViewModel.searchBarActive) {
                Icon(
                    modifier = Modifier.clickable {
                        if (homeViewModel.searchBarText.isNotEmpty()) {
                            homeViewModel.searchBarText = ""
                        } else {
                            homeViewModel.searchBarActive = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon"
                )
            }

        },

        ) {
        homeViewModel.searchBarHistory.forEach {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        homeViewModel.searchBarText = it
                        homeViewModel.onSearch()
                    }
            ) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Default.History,
                    contentDescription = "History Icon"
                )
                Text(text = it)

            }
        }
    }
}


// Her er den store TODO-lista
// TODO: Lagre tidligere søk i en lokal eller ekstern database
