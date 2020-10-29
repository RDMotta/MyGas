package com.rdm.mygas.ui.gas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rdm.mygas.databinding.ListGasItemBinding
import com.rdm.mygas.model.Gas

class GasAdapter : ListAdapter<Gas, RecyclerView.ViewHolder>(GasDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val plant = getItem(position)
        (holder as GasViewHolder).bind(plant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GasViewHolder(
            ListGasItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    class GasViewHolder(
        private val binding: ListGasItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Gas) {
            binding.apply {
                gas = item
                executePendingBindings()
            }
        }
    }
}

private class GasDiffCallback : DiffUtil.ItemCallback<Gas>() {

    override fun areItemsTheSame(oldItem: Gas, newItem: Gas): Boolean {
        return oldItem.gasId == newItem.gasId
    }

    override fun areContentsTheSame(oldItem: Gas, newItem: Gas): Boolean {
        return oldItem == newItem
    }
}