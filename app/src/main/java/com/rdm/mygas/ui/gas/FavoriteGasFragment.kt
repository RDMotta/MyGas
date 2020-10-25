package com.rdm.mygas.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rdm.mygas.R

class FavoriteGasFragment : Fragment() {

    private lateinit var favoriteGasViewModel: FavoriteGasViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        favoriteGasViewModel =
                ViewModelProvider(this).get(FavoriteGasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favorite_gas, container, false)
        val textView: TextView = root.findViewById(R.id.text_favorite_gas)
        favoriteGasViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}