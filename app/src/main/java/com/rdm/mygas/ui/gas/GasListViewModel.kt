package com.rdm.mygas

import androidx.lifecycle.*
import com.rdm.mygas.model.FavoriteGas
import com.rdm.mygas.model.Gas
import com.rdm.mygas.model.NoFavorite
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class GasListViewModel internal constructor(
        private val gasRepository: GasRepository
) : ViewModel() {
    private val _snackbar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackbar

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val gasFavorite = MutableLiveData<FavoriteGas>(NoFavorite)
//
//    val gas: LiveData<List<Gas>> = gasFavorite.switchMap { gas ->
//        if (gas.favorite == NoFavorite) {
//            gasRepository.gas
//        } else {
//            gasRepository.getGas(gas.favorite)
//        }
//    }

    private val gasFavoriteChannel = ConflatedBroadcastChannel<FavoriteGas>()
    val gasUsingFlow: LiveData<List<Gas>> = gasFavoriteChannel.asFlow()
            .flatMapLatest { gas ->
                if (gas == NoFavorite) {
                    gasRepository.gasFlow
                } else {
                    gasRepository.getGas(gas.favorite)
                }
            }.asLiveData()

    init {
        clearGas()

        gasFavoriteChannel.asFlow()
                .mapLatest { gas ->
                    _spinner.value = true
                    if (gas == NoFavorite) {
                        gasRepository.tryUpdateRecentGasCache()
                    } else {
                        gasRepository.tryUpdateRecentGasForFavorite(gas.favorite)
                    }
                }
                .onCompletion {  _spinner.value = false }
                .catch { throwable ->  _snackbar.value = throwable.message  }
                .launchIn(viewModelScope)
    }

    fun setGasFavorite(favorite: Boolean) {
        launchDataLoad { gasRepository.tryUpdateRecentGasForFavorite(favorite) }
    }

    fun clearGas() {
        launchDataLoad { gasRepository.tryUpdateRecentGasCache() }
    }

    fun isFiltered() = gasFavorite.value != NoFavorite
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
