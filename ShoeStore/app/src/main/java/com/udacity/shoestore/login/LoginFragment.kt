package com.udacity.shoestore.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentLoginBinding
import com.udacity.shoestore.list.ShoeListViewModel

class LoginFragment : Fragment(), View.OnClickListener {

    private val shoeListViewModel: ShoeListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding: FragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.logInButton.setOnClickListener(this)
        binding.signUpButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(view: View) {
        shoeListViewModel.logIn()
        view.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWelcomeFragment())
    }

}