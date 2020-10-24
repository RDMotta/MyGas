package com.rdm.mygas

import androidx.annotation.AnyThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.rdm.funnyquotes.utils.CacheOnSuccess
import com.rdm.funnyquotes.utils.ComparablePair
import com.rdm.mygas.model.Gas
import com.rdm.mygas.model.GasDao

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
class GasRepository private constructor(
        private val gasDao: GasDao,
        private val gasService: GasService,
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private var gasListSortOrderCache = CacheOnSuccess(onErrorFallback = { listOf<String>() }) {
        gasService.customCardSortOrder()
    }

    val gas: LiveData<List<Gas>> = liveData<List<Gas>> {
        val gasLiveData = gasDao.getGas()
        val customSortOrder = gasListSortOrderCache.getOrAwait()
        emitSource(gasLiveData.map { gasList -> gasList.applySort(customSortOrder) })
    }

    private val customSortFlow = gasListSortOrderCache::getOrAwait.asFlow()

    val gasFlow: Flow<List<Gas>>
        get() = gasDao.getGasFlow()
                .combine(customSortFlow) { gas, sortOrder ->
                    gas.applySort(sortOrder)
                }
                .flowOn(defaultDispatcher)
                .conflate()

    fun getGas(favorite: Boolean): Flow<List<Gas>> {
        return gasDao.getGasFavoriteFlow(favorite)
                .map { gasList ->
                    val sortOrderFromNetwork = gasListSortOrderCache.getOrAwait()
                    val nextValue = gasList.applyMainSafeSort(sortOrderFromNetwork)
                    nextValue
                }
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

    suspend fun tryUpdateRecentGasCache() {
        fetchRecentGas()
    }

    suspend fun tryUpdateRecentGasForFavorite(favorite: Boolean) {
        if (shouldUpdateGasCache(favorite)) fetchGasForFavorite(favorite)
    }

    private suspend fun fetchRecentGas() {
        val plants = gasService.allGas()
        gasDao.insertAll(plants)
    }

    private suspend fun fetchGasForFavorite(favorite: Boolean): List<Gas> {
        val gas = gasService.gasByFavorite(favorite)
        gasDao.insertAll(gas)
        return gas
    }

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: GasRepository? = null

        fun getInstance(gasDao: GasDao, gasService: GasService) =
                instance ?: synchronized(this) {
                    instance ?: GasRepository(gasDao, gasService).also { instance = it }
                }
    }
}