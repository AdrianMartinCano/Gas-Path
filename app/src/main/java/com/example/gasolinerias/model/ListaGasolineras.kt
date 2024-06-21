package com.example.gasolinerias.model

data class ListaGasolineras(
    val Fecha: String,
    val ListaEESSPrecio: List<Gasolinera>,
    val Nota: String,
    val ResultadoConsulta: String
)