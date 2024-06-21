package com.example.gasolinerias.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gasolinerias.R
import com.example.gasolinerias.model.GasolineraFirebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

@Composable
fun GasolinerasFav() {
    val gasolineras = remember { mutableStateListOf<GasolineraFirebase>() }
    val showDialog = remember { mutableStateOf(false) }
    val selectedGasolinera = remember { mutableStateOf<String>("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.delete_from_favorites)) },
            text = { Text(stringResource(id = R.string.delete_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteGasolinera(selectedGasolinera.value)
                        showDialog.value = false
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text("No")
                }
            }
        )
    }

    // Obtener las gasolineras de Firestore
    getGasolineraPorUID { gasolinerasRecuperadas ->
        gasolineras.clear()
        gasolineras.addAll(gasolinerasRecuperadas)
    }

    if (gasolineras.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(id = R.string.no_gas_stations_saved))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            items(gasolineras.size) { index ->
                val gasolinera = gasolineras[index]
                GasolineraCard(
                    gasolinera = gasolinera,
                    onDelete = {
                        selectedGasolinera.value = gasolinera.id
                        showDialog.value = true
                    },
                    onViewInMaps = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


val PrimaryColor = Color(0xFF004080)  // Azul Marino
val SecondaryColor = Color(0xFFFFA500) // Naranja
val BackgroundColor = Color(0xFFF0F0F0) // Gris Claro
val TextColorPrimary = Color(0xFF000000) // Negro
val ButtonDeleteColor = Color(0xFFD32F2F) // Rojo

@Composable
fun GasolineraCard(gasolinera: GasolineraFirebase, onDelete: () -> Unit, onViewInMaps: () -> Unit) {
    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(BackgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = gasolinera.rotulo,
                color = TextColorPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = gasolinera.direccion + ", " + gasolinera.localidad,
                color = TextColorPrimary,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = gasolinera.horario,
                color = TextColorPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse(
                            "geo:0,0?q=${gasolinera.latitud.replace(",", ".")}," +
                                    "${gasolinera.longitudWGS84.replace(",", ".")}"
                        )
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryColor),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.googel_maps_button),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Borrar",
                        tint = ButtonDeleteColor
                    )
                }
            }
        }

    }
}


val db = Firebase.firestore

// Define una función para recuperar gasolineras por UID
fun getGasolineraPorUID(onComplete: (List<GasolineraFirebase>) -> Unit) {
    // Crea una referencia a la colección de gasolineras
    val gasolinerasRef = db.collection("gasolineras")
    val uid = Firebase.auth.currentUser!!.uid

    // Realiza la consulta buscando documentos que coincidan con el UID especificado
    gasolinerasRef.whereEqualTo("uid", uid)
        .get()
        .addOnSuccessListener { documents ->
            // Lista para almacenar las gasolineras recuperadas
            val gasolineras = mutableListOf<GasolineraFirebase>()

            // Procesa cada documento recuperado
            for (document in documents) {
                // Convierte el documento de Firestore en un objeto Gasolinera
                val gasolinera = document.toObject<GasolineraFirebase>()

                // Agrega la ID de la gasolinera al objeto
                gasolinera.id = document.id

                // Agrega la gasolinera a la lista
                gasolineras.add(gasolinera)
            }

            // Llama a la función onComplete y pasa la lista de gasolineras
            onComplete(gasolineras)
        }
        .addOnFailureListener { exception ->
            // Maneja los errores en la recuperación de datos
            Log.w(TAG, "Error al recuperar las gasolineras por UID", exception)
            // Llama a la función onComplete con una lista vacía
            onComplete(emptyList())
        }

}

private fun deleteGasolinera(gasolineraId: String) {
    val db = Firebase.firestore
    val gasolinerasRef = db.collection("gasolineras")
    Log.e("ID", gasolineraId)
    gasolinerasRef.document(gasolineraId)
        .delete()
        .addOnSuccessListener {
            Log.d(TAG, "Gasolinera eliminada correctamente")
            // Aquí puedes realizar cualquier acción adicional después de borrar la gasolinera
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error al eliminar la gasolinera", e)
            // Manejar el error si falla la eliminación de la gasolinera
        }
}