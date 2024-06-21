package com.example.gasolinerias.api
import com.example.gasolinerias.model.ComunidadAutonoma
import com.example.gasolinerias.model.ListaGasolineras
import com.example.gasolinerias.model.Municipio
import com.example.gasolinerias.model.Provincia
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("Listados/MunicipiosPorProvincia/{id}")
    fun getMunicipios(@Path("id") id: String): Call<Array<Municipio>>

    @GET("EstacionesTerrestres/")
    fun getEESS(): Call<ListaGasolineras>


    @GET("Listados/ProvinciasPorComunidad/{id}")
    fun getProvincias(@Path("id") id: String): Call<Array<Provincia>>


    @GET("EstacionesTerrestres/FiltroMunicipio/{id}")
    fun getGasolinerasPorId(@Path("id") id: String): Call<ListaGasolineras>

    @GET("Listados/ComunidadesAutonomas/")
    fun getComunidadesAutonomas(): Call<Array<ComunidadAutonoma>>


}