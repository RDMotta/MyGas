package com.rdm.mygas

import androidx.annotation.AnyThread
import androidx.lifecycle.*
import com.rdm.funnyquotes.utils.CacheOnSuccess
import com.rdm.funnyquotes.utils.ComparablePair
import com.rdm.mygas.model.Gas
import com.rdm.mygas.model.GasDao

import kotlinx.coroutines.*

interface Repository{
    fun getData(): LiveData<List<Gas>>
    fun getGas(favorite: Boolean): LiveData<List<Gas>>
    suspend fun tryUpdateRecentGasCache()
    suspend fun tryUpdateRecentGasForFavorite(favorite: Boolean)
}

class GasRepository private constructor(
    private val gasDao: GasDao,
    private val gasService: NetworkService,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
)  : Repository {
    private var gasListGasCacheFavorite = CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
        gasService.gasFavorite()
    }

    private var gasListGasCache = CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
        gasService.allGas()
    }

    override fun getData(): LiveData<List<Gas>> = liveData<List<Gas>> {
        val gasLiveData = gasDao.getGas()
        val customSortOrder = gasListGasCache.getOrAwait()
        emitSource(gasLiveData.map { gasList -> gasList.applySort(customSortOrder as List<String>) })
    }

    override fun getGas(favorite: Boolean): LiveData<List<Gas>> = liveData<List<Gas>> {
        val gasLiveData = gasDao.getGasFavorite(favorite)
        val customSortOrder = gasListGasCacheFavorite.getOrAwait()
        emitSource(gasLiveData.map { gasList -> gasList.applySort(customSortOrder) })
    }

    private fun List<Gas>.applySort(customSortOrder: List<String>): List<Gas> {
        return sortedBy { gas ->
            val positionForItem = customSortOrder.indexOf(gas.gasId).let { order ->
                if (order > -1) order else Int.MAX_VALUE
            }
            ComparablePair(positionForItem, gas.description)
        }
    }

    @AnyThread
    private suspend fun List<Gas>.applyMainSafeSort(customSortOrder: List<String>) =
            withContext(defaultDispatcher) {
                this@applyMainSafeSort.applySort(customSortOrder)
            }

    private suspend fun shouldUpdateGasCache(favorite: Boolean): Boolean {
        return true
    }

    suspend override fun tryUpdateRecentGasCache() {
        withContext(defaultDispatcher) {
            fetchRecentGas()
        }
    }

    suspend override fun tryUpdateRecentGasForFavorite(favorite: Boolean) {
        if (shouldUpdateGasCache(favorite)) withContext(defaultDispatcher) {fetchGasForFavorite(favorite)}
    }

    private suspend fun fetchRecentGas() {
        val gas = gasService.allGas()
        gasDao.insertAll(gas)
    }

    private suspend fun fetchGasForFavorite(favorite: Boolean): List<Gas> {
        val gas = gasService.gasByFavorite(favorite)
        gasDao.insertAll(gas)
        return gas
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: GasRepository? = null

        fun getInstance(gasDao: GasDao, networkService: NetworkService) =
                instance ?: synchronized(this) {
                    instance ?: GasRepository(gasDao, networkService).also { instance = it }
                }
    }
}