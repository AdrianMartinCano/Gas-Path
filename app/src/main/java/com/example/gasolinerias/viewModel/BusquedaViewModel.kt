package com.example.gasolinerias.viewModel

import com.example.gasolinerias.api.ApiService
import com.example.gasolinerias.model.ComunidadAutonoma
import com.example.gasolinerias.model.ListaGasolineras
import com.example.gasolinerias.model.Municipio
import com.example.gasolinerias.model.Provincia
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BusquedaViewModel {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getComunidadesAutonomas(callback: (Array<ComunidadAutonoma>?) -> Unit){
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getComunidadesAutonomas()
        call.enqueue(object : Callback<Array<ComunidadAutonoma>> {
            override fun onResponse(call: Call<Array<ComunidadAutonoma>>, response: Response<Array<ComunidadAutonoma>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                    // Maneja el error de respuesta
                }
            }

            override fun onFailure(call: Call<Array<ComunidadAutonoma>>, t: Throwable) {
                callback(null)
                // Maneja la falla de la llamada
            }
        })
    }

    fun getProvincias(idAutonoma: String, callback: (Array<Provincia>?) -> Unit){
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getProvincias(idAutonoma)
        call.enqueue(object : Callback<Array<Provincia>> {
            override fun onResponse(call: Call<Array<Provincia>>, response: Response<Array<Provincia>>) {
                if (response.isSuccessful) {
                    callback(response.body())


                } else {
                    callback(null)
                    Log.e("Error", "Error al obtener las provincias")
                    // Maneja el error de respuesta
                }
            }

            override fun onFailure(call: Call<Array<Provincia>>, t: Throwable) {
                callback(null)
                // Maneja la falla de la llamada
            }
        })
    }



    fun getGasolinerasPorMunicipio(idMunicipio: String, callback: (ListaGasolineras?) -> Unit){
        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getGasolinerasPorId(idMunicipio)
        call.enqueue(object : Callback<ListaGasolineras> {
            override fun onResponse(call: Call<ListaGasolineras>, response: Response<ListaGasolineras>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    response.errorBody()?.let {
                        val errorBody = it.string()
                        // Maneja el error de respuesta
                        Log.e("error", errorBody)
                    }

                    callback(null)
                    // Maneja el error de respuesta
                }
            }

            override fun onFailure(call: Call<ListaGasolineras>, t: Throwable) {
                callback(null)
                // Maneja la falla de la llamada
            }
        })
    }

    fun getMunicipios(idProvincia: String, callback: (Array<Municipio>?) -> Unit) {

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getMunicipios(idProvincia)
        call.enqueue(object : Callback<Array<Municipio>> {
            override fun onResponse(call: Call<Array<Municipio>>, response: Response<Array<Municipio>>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                    // Silent Error
                }
            }

            override fun onFailure(call: Call<Array<Municipio>>, t: Throwable) {
                callback(null)
                // Maneja la falla de la llamada
            }
        })
    }


    fun getAllgasolineras(callback: (ListaGasolineras?) -> Unit){

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getEESS()
        call.enqueue(object : Callback<ListaGasolineras> {
            override fun onResponse(call: Call<ListaGasolineras>, response: Response<ListaGasolineras>) {
                if (response.isSuccessful) {
                    callback(response.body())

                } else {
                    callback(null)
                    // Maneja el error de respuesta
                }
            }

            override fun onFailure(call: Call<ListaGasolineras>, t: Throwable) {
                callback(null)
                // Maneja la falla de la llamada
            }
        })
    }


}

