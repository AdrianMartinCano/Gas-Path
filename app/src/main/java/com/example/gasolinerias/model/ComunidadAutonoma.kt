package com.example.gasolinerias.model

import com.google.gson.annotations.SerializedName

data class ComunidadAutonoma(



    @SerializedName("IDCCAA") val IDCCAA: String,
    @SerializedName("CCAA") val CCAA: String

){
    override fun toString(): String = CCAA
}
