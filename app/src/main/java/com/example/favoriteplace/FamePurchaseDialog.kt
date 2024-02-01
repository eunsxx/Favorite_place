package com.example.favoriteplace

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.favoriteplace.databinding.DialogShopDetailPurchaseFameBinding

class FamePurchaseDialog : DialogFragment(){
    private lateinit var binding: DialogShopDetailPurchaseFameBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DialogShopDetailPurchaseFameBinding.inflate(inflater,container,false)
        val view=binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return view
    }
}