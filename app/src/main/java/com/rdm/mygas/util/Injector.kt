package com.rdm.mygas.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.rdm.mygas.GasRepository
import com.rdm.mygas.GasService
import com.rdm.mygas.NetworkService
import com.rdm.mygas.ui.gas.GasListViewModelFactory

interface ViewModelFactoryProvider {
    fun provideGasListViewModelFactory(context: Context): GasListViewModelFactory
}

val Injector: ViewModelFactoryProvider
    get() = currentInjector

private object DefaultViewModelProvider: ViewModelFactoryProvider {
    private fun getGasRepository(context: Context): GasRepository {
        return GasRepository.getInstance(
            gasDao(context),
            gasService()
        )
    }

    private fun gasService() = NetworkService()
    private fun gasDao(context: Context) = AppDatabase.
        getInstance(context.applicationContext).gasDao()

    override fun provideGasListViewModelFactory(context: Context): GasListViewModelFactory {
        val repository = getGasRepository(context)
        return GasListViewModelFactory(repository)
    }
}

private object Lock

@Volatile private var currentInjector: ViewModelFactoryProvider =
    DefaultViewModelProvider


@VisibleForTesting
private fun setInjectorForTesting(injector: ViewModelFactoryProvider?) {
    synchronized(Lock) {
        currentInjector = injector ?: DefaultViewModelProvider
    }
}

@VisibleForTesting
private fun resetInjector() =
    setInjectorForTesting(null)