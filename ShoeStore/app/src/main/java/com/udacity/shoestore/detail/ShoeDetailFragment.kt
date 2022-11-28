package com.udacity.shoestore.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.InverseMethod
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.list.ShoeListViewModel
import com.udacity.shoestore.databinding.FragmentShoeDetailBinding
import com.udacity.shoestore.models.Shoe

class ShoeDetailFragment : Fragment() {

    private val shoeListViewModel: ShoeListViewModel by activityViewModels()
    private lateinit var binding : FragmentShoeDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_detail, container, false)
        binding.viewModel = shoeListViewModel
        binding.currentShoe = Shoe("", 5.0, "", "")
        binding.lifecycleOwner = this
        shoeListViewModel.navigateUp.observe(viewLifecycleOwner) { navigateUp ->
            if (navigateUp == true) {
                navigateBack()
                shoeListViewModel.onNavigateUpComplete()
            }
        }
        return binding.root
    }

    private fun navigateBack(){
        requireView().findNavController()
            .navigate(ShoeDetailFragmentDirections.actionShoeDetailFragmentToShoeListFragment())
    }
}

object Converter {
    @InverseMethod("stringToDouble")
    @JvmStatic fun doubleToString(value: Double?): String? {
        if (value == null) return "0"
        return value.toString()
    }

    @SuppressLint("UseValueOf")
    @JvmStatic fun stringToDouble(value: String?): java.lang.Double {
        if (value == null) return java.lang.Double(0.0)
        return if (value.isEmpty()) java.lang.Double(0.0) else java.lang.Double(value)
    }
}