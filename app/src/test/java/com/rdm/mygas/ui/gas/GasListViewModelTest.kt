package com.rdm.mygas.ui.gas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rdm.mygas.Repository
import com.rdm.mygas.model.Gas
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GasListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repository = mockk<Repository>()
    private val dataObserver = mockk<Observer<List<Gas>>>(relaxed = true)
    private val dataObserverFavorite = mockk<Observer<List<Gas>>>(relaxed = true)

    @Test
    fun `When ViewModel call repository`(){

        val mockListFavorite = MutableLiveData<List<Gas>>()
        mockListFavorite.postValue(listOf<Gas>(
            Gas("1","Gas Test1",4.5,false),
            Gas("2","Gas Test2",3.5,true),
            Gas("3","Gas Test3",4.5,false)))

        val mockList = MutableLiveData<List<Gas>>()
        mockList.postValue(listOf<Gas>(
            Gas("1","Gas Test1",2.0,true),
            Gas("2","Gas Test2",2.0,true)))

        every { repository.getData() } answers {mockList}
        every { repository.getGas(true) } answers {mockListFavorite}

        val viewModel = initViewModel()

        viewModel.fetchData()
        viewModel.fetchFavoriteData()

        verify { repository.getData() }
        verify { repository.getGas(true) }
        verify { dataObserver.onChanged(mockList.value) }
        verify { dataObserverFavorite.onChanged(mockListFavorite.value) }
    }

    private fun initViewModel(): GasListViewModel{
        val viewModel = GasListViewModel(repository)

        viewModel.fetchData().observeForever(dataObserver)
        viewModel.fetchFavoriteData().observeForever(dataObserverFavorite)
        return viewModel
    }
}



