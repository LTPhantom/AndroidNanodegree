package com.udacity.shoestore.list

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe


class ShoeListViewModel : ViewModel() {
    private val _shoeList = MutableLiveData<List<Shoe>>()
    val shoeList: LiveData<List<Shoe>>
        get() = _shoeList

    private val _loggedin = MutableLiveData<Boolean>()
    val loggedin: LiveData<Boolean>
        get() = _loggedin

    private val _navigateUp = MutableLiveData<Boolean>()
    val navigateUp: LiveData<Boolean>
        get() = _navigateUp

    init {
        _shoeList.value = listOf()
        _loggedin.value = false
        _navigateUp.value = false
    }

    fun addNewShoe(shoe: Shoe) {
        _shoeList.value = _shoeList.value?.plus(listOf(shoe))
        navigateBack()
    }

    fun logIn() {
        _loggedin.value = true
    }

    fun logOut() {
        _shoeList.value = listOf()
        _loggedin.value = false
    }

    fun navigateBack() {
        _navigateUp.value = true
    }

    fun onNavigateUpComplete() {
        _navigateUp.value = false
    }
}