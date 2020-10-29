package com.rdm.mygas.ui.gas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import com.rdm.mygas.databinding.FragmentGasListBinding
import com.rdm.mygas.util.Injector

class FavoriteGasFragment : Fragment() {
    private val viewModel: GasListViewModel by viewModels {
        Injector.provideGasListViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGasListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        viewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }

        val adapter = GasAdapter()
        binding.gasList.adapter = adapter
        subscribeUi(adapter)
        viewModel.setGasFavorite(true)
        return binding.root
    }

    private fun subscribeUi(adapter: GasAdapter) {
        viewModel.fetchFavoriteData().observe(viewLifecycleOwner) { gas ->
            adapter.submitList(gas)
        }
    }
}