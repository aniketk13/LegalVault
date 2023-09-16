package com.teamdefine.legalvault.main.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    private val _loadingModel: MutableLiveData<LoadingModel> = MutableLiveData()
    val loadingModel: LiveData<LoadingModel>
        get() = _loadingModel


    fun updateLoadingModel(loadingModel: LoadingModel = LoadingModel.COMPLETED) {
        _loadingModel.postValue(loadingModel)
    }
}