package com.rdm.mygas.ui.gas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rdm.mygas.Repository
import com.rdm.mygas.model.Gas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GasListViewModel internal constructor(
        private val gasRepository: Repository
) : ViewModel() {
    private val _snackbar = MutableLiveData<String?>()

    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)

    val spinner: LiveData<Boolean>
        get() = _spinner

    private val gas: LiveData<List<Gas>> =  gasRepository.getData()
    private val gasFavorite: LiveData<List<Gas>> =  gasRepository.getGas(true)

    fun fetchData(): LiveData<List<Gas>> {
        //clearGas()
        return gas
    }
    fun fetchFavoriteData() = gasFavorite


    fun setGasFavorite(favorite: Boolean) {
        launchDataLoad { gasRepository.tryUpdateRecentGasForFavorite(favorite) }
    }

    private fun clearGas() {
        launchDataLoad { gasRepository.tryUpdateRecentGasCache() }
    }

    fun onSnackbarShown() {
        _snackbar.value = null
    }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
                try {
                    _spinner.value = true
                    block()
                } catch (error: Throwable) {
                    _snackbar.value = error.message
                } finally {
                    _spinner.value = false
                }
            }

    }
}