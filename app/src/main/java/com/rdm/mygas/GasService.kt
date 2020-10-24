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
        .baseUrl("https://raw.githubusercontent.com/")
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

    suspend fun customCardSortOrder(): List<String> = withContext(Dispatchers.Default) {
        val result = customGasService.getCustomGasSortOrder()
        result.map { gas -> gas.gasId }
    }
}

interface CustomGasService {
    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/plants.json")
    suspend fun getAllGas() : List<Gas>

    @GET("googlecodelabs/kotlin-coroutines/master/advanced-coroutines-codelab/sunflower/src/main/assets/custom_plant_sort_order.json")
    suspend fun getCustomGasSortOrder() : List<Gas>
}