package com.example.gasolinerias.view

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.gasolinerias.model.Gasolinera
import com.example.gasolinerias.R
import com.example.gasolinerias.viewModel.MapaViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Declaración de SharedPreferences
val PREFS_NAME = "MyPrefsFile"
val DISTANCE_KEY = "distance"

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun AjustesPreview(navController: NavController, innerNavController: NavHostController) {
    val PrimaryColor = Color(0xFF004080)
    val showDialog = remember { mutableStateOf(false) }
    val distances = listOf(1, 5, 10, 25)
    val expanded = remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val savedDistance = sharedPreferences.getInt(DISTANCE_KEY, 1)

    // Inicialización de la distancia seleccionada con la distancia guardada
    val selectedDistance = remember { mutableStateOf(savedDistance) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(id = R.string.settings_logout_confirm_title)) },
            text = { Text(text = stringResource(id = R.string.settings_logout_confirm_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        navController.navigate("login")
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog.value = false }
                ) {
                    Text(text = stringResource(id = R.string.confirm_no))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.settings_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))


            Button(
                onClick = { innerNavController.navigate("AjustesCuenta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.settings_account), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { expanded.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.distance_format, selectedDistance.value), color = Color.White)
            }



            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                distances.forEach { distance ->
                    DropdownMenuItem(
                        text = { Text(text = "$distance km") },  // Se agrega el texto aquí
                        onClick = {
                            selectedDistance.value = distance
                            Log.d(
                                "prueba",
                                "Distancia seleccionada en ajustrespreview" + selectedDistance.value.toString()
                            )

                            Toast.makeText(context, "Los cambios se reflejarán cuando reinicie la aplicación", Toast.LENGTH_LONG).show()
                            expanded.value = false
                            // Guardar la distancia seleccionada en SharedPreferences
                            sharedPreferences.edit().putInt(DISTANCE_KEY, distance).apply()
                        }
                    )
                }
            }
        }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(id = R.string.settings_logout), color = Color.White)
            }







            if (snackbarVisible) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text(text = stringResource(id = R.string.toast_distance_changed))
                }
            }

    }

}


