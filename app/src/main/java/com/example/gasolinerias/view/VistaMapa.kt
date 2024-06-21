@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gasolinerias.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.gasolinerias.R
import com.example.gasolinerias.model.ComunidadAutonoma
import com.example.gasolinerias.model.Gasolinera
import com.example.gasolinerias.model.GasolineraFirebase
import com.example.gasolinerias.model.Municipio
import com.example.gasolinerias.model.Provincia
import com.example.gasolinerias.viewModel.BusquedaViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaMapa(gasolineras: List<Gasolinera>, context: Context) {
    val viewModel = remember { BusquedaViewModel() }

    var comunidades by remember { mutableStateOf<Array<ComunidadAutonoma>?>(null) }
    var provincias by remember { mutableStateOf<Array<Provincia>?>(null) }
    var municipios by remember { mutableStateOf<Array<Municipio>?>(null) }

    var selectedComunidad by remember { mutableStateOf<ComunidadAutonoma?>(null) }
    var selectedProvincia by remember { mutableStateOf<Provincia?>(null) }
    var selectedMunicipio by remember { mutableStateOf<Municipio?>(null) }

    var gasolinerasMapa by remember { mutableStateOf<List<Gasolinera>>(emptyList()) }

    val primaryColor = Color(0xFF004080)

    LaunchedEffect(Unit) {
        viewModel.getComunidadesAutonomas { result -> comunidades = result }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = primaryColor,
                ),
                title = { Text("Gasolinerías") },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuButton(
                            label = "C. Autonoma",
                            items = comunidades?.toList() ?: emptyList(),
                            selectedItem = selectedComunidad,
                            onItemSelected = { comunidad ->
                                selectedComunidad = comunidad
                                selectedProvincia = null
                                selectedMunicipio = null
                                provincias = null
                                municipios = null
                                viewModel.getProvincias(comunidad.IDCCAA) { result ->
                                    provincias = result
                                }
                            }
                        )
                        DropdownMenuButton(
                            label = "Provincia",
                            items = provincias?.toList() ?: emptyList(),
                            selectedItem = selectedProvincia,
                            onItemSelected = { provincia ->
                                selectedProvincia = provincia
                                selectedMunicipio = null
                                municipios = null
                                viewModel.getMunicipios(provincia.IDPovincia) { result ->
                                    municipios = result
                                }
                            }
                        )
                        DropdownMenuButton(
                            label = "Municipio",
                            items = municipios?.toList() ?: emptyList(),
                            selectedItem = selectedMunicipio,
                            onItemSelected = { municipio ->
                                selectedMunicipio = municipio
                                Log.e("Municipio", municipio.toString())

                                gasolinerasMapa = emptyList()
                                viewModel.getGasolinerasPorMunicipio(municipio.id) { result ->
                                    gasolinerasMapa = result?.ListaEESSPrecio!!
                                        .filter { it.precioGasolina95E5 != "" && it.precioGasoleoA != "" }
                                        .sortedBy { it.precioGasolina95E5 }
                                    if (gasolinerasMapa.isEmpty()) {
                                        Toast.makeText(context, "No hay gasolinerías en este municipio", Toast.LENGTH_SHORT).show()
                                    }
                                    Log.e("Gasolinera", result.ListaEESSPrecio.toString())
                                }
                            }
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            if (gasolinerasMapa.isNotEmpty()) {
                MapaConMarcadores(
                    gasolineras = gasolinerasMapa,
                    modifier = Modifier.padding(innerPadding),
                    context = context,
                    dropDownMenu = true
                )
            } else {
                MapaConMarcadores(
                    gasolineras = gasolineras,
                    modifier = Modifier.padding(innerPadding),
                    context = context,
                    dropDownMenu = false
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.R)
@SuppressLint("MissingPermission")
@Composable
fun MapaConMarcadores(
    gasolineras: List<Gasolinera>,
    modifier: Modifier = Modifier,
    context: Context,
    dropDownMenu: Boolean
) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedGasolinera = remember { mutableStateOf<Gasolinera?>(null) }

    if (showDialog.value && selectedGasolinera.value != null) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.favorites_dialog_title)) },
            text = { Text(text = stringResource(R.string.favorites_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.e("Gasolinera", selectedGasolinera.value!!.rotulo)
                        guardarGasolinera(selectedGasolinera.value!!)
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(R.string.favorites_dialog_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text = stringResource(R.string.favorites_dialog_no))
                }
            }
        )
    }

    var cameraMoved by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var ubi by remember { mutableStateOf(LatLng(0.0, 0.0))}
    // Check for location permission
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        LaunchedEffect(Unit) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    ubi = LatLng(location.latitude, location.longitude)
                    if (!cameraMoved && !dropDownMenu) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(ubi, 15f))
                        cameraMoved = true
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MapaConMarcadores", "Error al obtener la ubicación: ${exception.message}")
                }
        }
    }


    if(ubi.latitude != 0.0 && ubi.longitude != 0.0){
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {


            Marker(
                state = rememberMarkerState(position = ubi),
                title = "Mi ubicación",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.persona),
                anchor = Offset(0.1f, 0.1f),
            )


            if (dropDownMenu) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            gasolineras[0].latitud.replace(",", ".").toDouble(),
                            gasolineras[0].longitudWGS84.replace(",", ".").toDouble()
                        ), 15f
                    )
                )
            }else{
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            ubi.latitude,
                            ubi.longitude), 15f)
                )
            }

            for (i in gasolineras.indices) {
                val latitud = gasolineras[i].latitud.replace(",", ".").toDoubleOrNull()
                val longitud = gasolineras[i].longitudWGS84.replace(",", ".").toDoubleOrNull()
                if (latitud != null && longitud != null) {
                    if (i < 3) {
                        Marker(
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                            state = rememberMarkerState(position = LatLng(latitud, longitud)),
                            title = gasolineras[i].rotulo,
                            snippet = stringResource(R.string.gasoline) + "95: " + "${gasolineras[i].precioGasolina95E5}€ | Diesel:${gasolineras[i].precioGasoleoA}€",
                            onInfoWindowClick = {
                                selectedGasolinera.value = gasolineras[i]
                                showDialog.value = true
                            }
                        )
                    } else {
                        Marker(
                            state = rememberMarkerState(position = LatLng(latitud, longitud)),
                            title = gasolineras[i].rotulo,
                            snippet = stringResource(R.string.gasoline) + "95: " + "${gasolineras[i].precioGasolina95E5}€ | Diesel:${gasolineras[i].precioGasoleoA}€",
                            onInfoWindowClick = {
                                selectedGasolinera.value = gasolineras[i]
                                showDialog.value = true
                            }
                        )
                    }
                }
            }
        }
    }else{
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {


            Marker(
                state = rememberMarkerState(position = ubi),
                title = "Mi ubicación",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.persona),
                anchor = Offset(0.1f, 0.1f),
            )

            if (dropDownMenu) {
                cameraPositionState.move(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            gasolineras[0].latitud.replace(",", ".").toDouble(),
                            gasolineras[0].longitudWGS84.replace(",", ".").toDouble()
                        ), 15f
                    )
                )
            }

            for (i in gasolineras.indices) {
                val latitud = gasolineras[i].latitud.replace(",", ".").toDoubleOrNull()
                val longitud = gasolineras[i].longitudWGS84.replace(",", ".").toDoubleOrNull()
                if (latitud != null && longitud != null) {
                    if (i < 3) {
                        Marker(
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                            state = rememberMarkerState(position = LatLng(latitud, longitud)),
                            title = gasolineras[i].rotulo,
                            snippet = stringResource(R.string.gasoline) + "95: " + "${gasolineras[i].precioGasolina95E5}€ | Diesel:${gasolineras[i].precioGasoleoA}€",
                            onInfoWindowClick = {
                                selectedGasolinera.value = gasolineras[i]
                                showDialog.value = true
                            }
                        )
                    } else {
                        Marker(
                            state = rememberMarkerState(position = LatLng(latitud, longitud)),
                            title = gasolineras[i].rotulo,
                            snippet = stringResource(R.string.gasoline) + "95: " + "${gasolineras[i].precioGasolina95E5}€ | Diesel:${gasolineras[i].precioGasoleoA}€",
                            onInfoWindowClick = {
                                selectedGasolinera.value = gasolineras[i]
                                showDialog.value = true
                            }
                        )
                    }
                }
            }
        }
    }


}



fun guardarGasolinera(gasolinera: Gasolinera) {
    val db = Firebase.firestore
    val gasolinerasRef = db.collection("gasolineras")
    val uid = Firebase.auth.currentUser!!.uid
    val gasolineraFirebase = GasolineraFirebase(
        id = "",
        rotulo = gasolinera.rotulo,
        direccion = gasolinera.direccion,
        horario = gasolinera.horario,
        latitud = gasolinera.latitud,
        localidad = gasolinera.localidad,
        longitudWGS84 = gasolinera.longitudWGS84,
        uid = uid
    )

    gasolinerasRef.add(gasolineraFirebase)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Gasolinera guardada con ID: ${documentReference.id}")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error al guardar la gasolinera", e)
        }
}

@Composable
fun <T> DropdownMenuButton(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    maxWidth: Dp = 120.dp
) where T : Any {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedItem?.toString() ?: label) }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
            ),
            modifier = Modifier
                .widthIn(min = 0.dp, max = maxWidth)
                .width(120.dp)
        ) {
            Text(
                text = selectedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            item.toString()
                                .lowercase(Locale.getDefault())
                                .split(" ")
                                .joinToString(" ") { it.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                                } }
                        )
                    },
                    onClick = {
                        selectedText = item.toString()
                            .lowercase(Locale.getDefault())
                            .split(" ")
                            .joinToString(" ") { it.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            } }
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier.widthIn(min = 0.dp, max = maxWidth)
                )
            }
        }
    }
}
