package com.example.gasolinerias.model

import com.google.gson.annotations.SerializedName

data class GasolineraFirebase(
    @SerializedName("id") var id: String,
    @SerializedName("Dirección") val direccion: String,
    @SerializedName("Horario") val horario: String,
    @SerializedName("Latitud") val latitud: String,
    @SerializedName("Localidad") val localidad: String,
    @SerializedName("Longitud (WGS84)") val longitudWGS84: String,
    @SerializedName("Rótulo") val rotulo: String,
    @SerializedName("UID") val uid: String
){
    constructor() : this("", "", "", "", "", "", "", "")
}
