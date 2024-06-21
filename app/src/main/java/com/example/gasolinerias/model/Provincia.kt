package com.example.gasolinerias.model

import com.google.gson.annotations.SerializedName

data class Provincia(

    @SerializedName("IDPovincia") val IDPovincia: String,
    @SerializedName("IDCCAA") val IDCCAA: String,
    @SerializedName("Provincia") val Provincia: String,
    @SerializedName("CCAA") val CCAA: String
){
    override fun toString(): String = Provincia
}