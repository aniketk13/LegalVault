package com.teamdefine.legalvault.main.home.mydocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import kotlinx.coroutines.launch
import timber.log.Timber

class MyDocumentsVM : BaseViewModel() {
    private val _myDocs: MutableLiveData<MyDocsResponseModel> = MutableLiveData()
    val myDocs: LiveData<MyDocsResponseModel>
        get() = _myDocs

    fun getAllDocs() {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response = RetrofitInstance.api.getSignatureRequests()
                if (response.isSuccessful) {
                    updateLoadingModel(LoadingModel.COMPLETED)
                    _myDocs.postValue(response.body())
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                }
            } catch (e: Exception) {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e(e.message.toString())
            }
        }
    }
}