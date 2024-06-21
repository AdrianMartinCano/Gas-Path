package com.example.gasolinerias.model

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class Calculate {



    private fun convertToRadians(degrees: Double): Double {
        return degrees * PI / 180.0
    }

    // Método para calcular la distancia
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat1Rad = convertToRadians(lat1)
        val lon1Rad = convertToRadians(lon1)
        val lat2Rad = convertToRadians(lat2)
        val lon2Rad = convertToRadians(lon2)

        val distance=  acos(sin(lat1Rad) * sin(lat2Rad) + cos(lat1Rad) * cos(lat2Rad) * cos(lon2Rad - lon1Rad))* 6371
        return String.format("%.2f", distance).replace(",", ".").toDouble()
    }



    fun getBoundingBox(lat: Double, lon: Double, distance: Double): Pair<Pair<Double, Double>, Pair<Double, Double>> {
        val latChange = distance / 111.2
        val lonChange = abs(distance / (111.2 * cos(lat * PI / 180)))

        val topLeft = Pair(lat + latChange, lon - lonChange)
        val bottomRight = Pair(lat - latChange, lon + lonChange)

        return Pair(topLeft, bottomRight)
    }


}
