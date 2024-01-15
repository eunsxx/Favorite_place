package com.example.favoriteplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.favoriteplace.databinding.FragmentRallyplaceBinding

class RallyPlaceFragment: Fragment(){

    lateinit var binding: FragmentRallyplaceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRallyplaceBinding.inflate(inflater,container,false)

        return binding.root
    }
}