package com.example.gasolinerias.viewModel
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gasolinerias.model.Gasolinera
import com.example.gasolinerias.model.Calculate
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapaViewModel : ViewModel() {

    @SuppressLint("MissingPermission")
    suspend fun getGasolineras(context: Context, selectedDistance: Int): List<Gasolinera> = suspendCoroutine { continuation ->
        val busqueda = BusquedaViewModel()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        busqueda.getAllgasolineras { gasolineras ->
            val listaGasolinera = gasolineras?.ListaEESSPrecio ?: return@getAllgasolineras

            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        obtenerGasolinerasCercanas(location, listaGasolinera, selectedDistance, continuation)
                    } else {
                        Log.e("com.example.gasolinerias.viewModels.MapaViewModel", "Ubicación no disponible")
                        continuation.resume(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("com.example.gasolinerias.viewModels.MapaViewModel", "Error al obtener la ubicación: ${exception.message}")
                    continuation.resume(emptyList())
                }
        }
    }

    private fun obtenerGasolinerasCercanas(
        location: Location,
        listaGasolinera: List<Gasolinera>,
        selectedDistance: Int,
        continuation: Continuation<List<Gasolinera>>
    ) {
        val latitud = location.latitude
        val longitud = location.longitude
        Log.d("com.example.gasolinerias.viewModels.MapaViewModel", "Ubicación obtenida: latitud=$latitud, longitud=$longitud")

        val cd = Calculate()
        val boundingBox = cd.getBoundingBox(latitud, longitud, selectedDistance.toDouble())
        Log.d("com.example.gasolinerias.viewModels.MapaViewModel", "Bounding box: $boundingBox")

        val gasolinerasFiltradas = listaGasolinera.filter { gasolinera ->
            val lat = gasolinera.latitud.replace(",", ".").toDouble()
            val lon = gasolinera.longitudWGS84.replace(",", ".").toDouble()
            lat <= boundingBox.first.first && lat >= boundingBox.second.first &&
                    lon >= boundingBox.first.second && lon <= boundingBox.second.second
        }

        viewModelScope.launch {
            val gasolinerasCercanas = mutableListOf<Gasolinera>()

            val deferred = gasolinerasFiltradas.map { gasolinera ->
                async {
                    val distancia = cd.calculateDistance(
                        latitud, longitud,
                        gasolinera.latitud.replace(",", ".").toDouble(),
                        gasolinera.longitudWGS84.replace(",", ".").toDouble()
                    )
                    if (distancia < selectedDistance &&
                        gasolinera.precioGasolina95E5.isNotEmpty() &&
                        gasolinera.precioGasoleoA.isNotEmpty()
                    ) {
                        gasolinerasCercanas.add(gasolinera)
                    }
                }
            }
            deferred.awaitAll()
            val sortedList = gasolinerasCercanas.sortedBy { it.precioGasolina95E5 }
            continuation.resume(sortedList)
        }
    }
}
