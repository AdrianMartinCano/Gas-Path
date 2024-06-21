@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gasolinerias.view

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gasolinerias.model.Gasolinera
import com.example.gasolinerias.R
import com.example.gasolinerias.viewModel.BottomNavItem
import com.example.gasolinerias.viewModel.MapaViewModel

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VistaMain(navController: NavController, context: Context) {
    val innerNavController = rememberNavController()
    var gasolineras by remember { mutableStateOf<List<Gasolinera>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        gasolineras = obtenerGasolineras(context)
//        Log.e("Gasolineras", gasolineras.toString())
        isLoading = false
    }
    if (isLoading) {
//         Muestra un indicador de carga mientras se obtienen los datos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier)
        }

    } else {

        Scaffold(
            bottomBar = { BottomNavigationBar(innerNavController, context) }
        ) { innerPadding ->


            NavHost(
                navController = innerNavController,
                startDestination = BottomNavItem.Home(context.getString(R.string.home)).route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home(context.getString(R.string.home)).route) {
                    VistaMapa(gasolineras, context)
                }
                composable(BottomNavItem.Favs(context.getString(R.string.favorites)).route) {
                    GasolinerasFav()
                }
                composable(BottomNavItem.Profile(context.getString(R.string.settings)).route) {
                    AjustesPreview(navController, innerNavController)
                }
                composable("AjustesCuenta") {
                    AjustesCuenta(navController, innerNavController,context)
                }

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, context: Context) {
    val items = listOf(
        BottomNavItem.Home(context.getString(R.string.home)),
        BottomNavItem.Favs(context.getString(R.string.favorites)),
        BottomNavItem.Profile(context.getString(R.string.settings))
    )

    // Obtén el estado de la entrada actual en el back stack
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val PrimaryColor = Color(0xFF004080)
    val SecundaryColor = Color(0xFFFFA500)

    NavigationBar(containerColor = PrimaryColor) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SecundaryColor,  // Color del icono cuando está seleccionado
                    selectedTextColor = SecundaryColor,  // Color del texto cuando está seleccionado
                    unselectedIconColor = Color.White,  // Color del icono cuando no está seleccionado
                    unselectedTextColor = Color.White,  // Color del texto cuando no está seleccionado
                    indicatorColor = PrimaryColor // Color del indicador, si lo hay
                )
            )
        }
    }
}


suspend fun obtenerGasolineras(context: Context): List<Gasolinera> {
    val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val savedDistance = sharedPreferences.getInt(DISTANCE_KEY, 1)
    Log.e("savedDistance", savedDistance.toString())
    return MapaViewModel().getGasolineras(context, savedDistance)
}
