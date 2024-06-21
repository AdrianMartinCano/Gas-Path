package com.example.gasolinerias

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gasolinerias.view.VistaLogin
import com.example.gasolinerias.view.VistaMain
import com.example.gasolinerias.view.VistaSignUp
import com.example.gasolinerias.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                setupContent(this)
            } else {
                finish()
            }
        }

        if (hasLocationPermission()) {
            setupContent(this)
        } else {
            permissionLauncher.launch(this.permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun setupContent(context: Context) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Si hay un usuario autenticado, navegar a la pantalla correspondiente
//            Log.e("Sesion iniciada", "La sesion estaba activa")
            setContent {

                val navController = rememberNavController()
                val authViewModel = AuthViewModel()

                NavHost(navController, startDestination = "maps") {
                    composable("login") {
                        VistaLogin(navController, authViewModel, this@MainActivity)
                    }
                    composable("register") {
                        VistaSignUp(navController, authViewModel, this@MainActivity)
                    }
                    composable("maps") {
                        VistaMain(navController, context)
                    }
                    /* composable(
                         route = "mapa/{gasolineras}",
                         arguments = listOf(navArgument("gasolineras") { type = NavType.StringType })
                     ) { backStackEntry ->
                         val gasolinerasJson = backStackEntry.arguments?.getString("gasolineras")
                         val gson = Gson()
                         val type = object : TypeToken<List<Gasolinera>>() {}.type
                         val gasolineras: List<Gasolinera> = gson.fromJson(gasolinerasJson, type)
                         VistaMapa(gasolineras = gasolineras, context = LocalContext.current)
                     }*/
                }
            }

        } else {
            setContent {
                val navController = rememberNavController()
                val authViewModel = AuthViewModel()

                NavHost(navController, startDestination = "login") {
                    composable("login") {
                        VistaLogin(navController, authViewModel, this@MainActivity)
                    }
                    composable("register") {
                        VistaSignUp(navController, authViewModel, this@MainActivity)
                    }
                    composable("maps") {
                        VistaMain(navController, context)
                    }
                }
            }
        }
    }


    private fun hasLocationPermission(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


}
