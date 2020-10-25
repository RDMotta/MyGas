package com.rdm.mygas.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.AndroidAnnotatedBuilder
import com.rdm.mygas.model.Gas
import com.rdm.mygas.ui.gas.GasListViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

import java.util.*

@RunWith(AndroidJUnit4::class)
class GasListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: GasListViewModel

    @Test
    fun changingViewModelValue_ShouldSetListViewItems() {
        val scenario = launchFragment<MyFragment>()
        scenario.onFragment { fragment ->
            fragment.myViewModel.status.value = "status1"
            assert(fragment.myListView.adapter.getItem(0) == "a")
        }
    }

}