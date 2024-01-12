package com.example.favoriteplace

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.favoriteplace.databinding.FragmentCommunityFreeBinding
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFreeFragment : Fragment() {

    lateinit var binding: FragmentCommunityFreeBinding
    private val information= arrayListOf("최신 글","추천 많은 글","내가 작성한 글","내 댓글")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCommunityFreeBinding.inflate(inflater,container,false)

        val communityFreeAdapter=CommunityFreeVPAdapter(this)
        binding.communityFreeVp.adapter=communityFreeAdapter
        TabLayoutMediator(binding.communityFreeTb,binding.communityFreeVp){
            tab,position->
            tab.text=information[position]
        }.attach()
        Log.d("information","success")

        return binding.root
    }
}