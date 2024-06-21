package com.example.gasolinerias.model

import com.google.gson.annotations.SerializedName

data class Municipio(

    @SerializedName("IDMunicipio") val id: String,
    @SerializedName("Municipio") val Municipio: String,
    @SerializedName("Provincia") val Provincia: String,
    @SerializedName("IDProvincia") val IDProvincia: String,
    @SerializedName("IDCCAA") val IDCCAA: String,
    @SerializedName("CCAA") val CCAA: String

){
    override fun toString(): String = Municipio
}