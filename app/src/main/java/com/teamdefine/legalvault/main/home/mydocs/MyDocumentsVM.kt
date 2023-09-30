package com.teamdefine.legalvault.main.home.mydocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import com.teamdefine.legalvault.main.home.mydocs.models.SignUrlResponseModel
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sign

class MyDocumentsVM : BaseViewModel() {
    private val _myDocs: MutableLiveData<MyDocsResponseModel> = MutableLiveData()
    val myDocs: LiveData<MyDocsResponseModel>
        get() = _myDocs

    private val _signingUrl:MutableLiveData<SignUrlResponseModel> = MutableLiveData()
    val signingUrl:LiveData<SignUrlResponseModel>
        get() = _signingUrl

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

    fun getDocSignUrl(signature_id:String){
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response=RetrofitInstance.api.getSigningUrl(signature_id)
                if(response.isSuccessful){
                    updateLoadingModel(LoadingModel.COMPLETED)
                    _signingUrl.postValue(response.body())
                }else {
                    updateLoadingModel(LoadingModel.ERROR)
                }
            }catch (e: Exception) {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e(e.message.toString())
            }
        }
    }
}