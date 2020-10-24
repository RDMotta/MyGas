package com.rdm.mygas

import com.rdm.mygas.model.Gas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class GasService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/RDMotta/MyGas/master/")
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val customGasService = retrofit.create(CustomGasService::class.java)

    suspend fun allGas(): List<Gas> = withContext(Dispatchers.Default) {
        delay(1500)
        val result = customGasService.getAllGas()
        result.shuffled()
    }

    suspend fun gasByFavorite(favorite: Boolean) = withContext(Dispatchers.Default) {
        delay(1500)
        val result = customGasService.getAllGas()
        result.filter { it.favorite == favorite }.shuffled()
    }

    suspend fun customGasFavorite(): List<String> = withContext(Dispatchers.Default) {
        val result = customGasService.getCustomGasFavorite()
        result.map { gas -> gas.gasId }
    }
}

interface CustomGasService {
    @GET("/app/src/main/assets/gas.json")
    suspend fun getAllGas() : List<Gas>

    @GET("/app/src/main/assets/gas_order_favorite.json")
    suspend fun getCustomGasFavorite() : List<Gas>
}