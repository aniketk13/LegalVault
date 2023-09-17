package com.teamdefine.legalvault.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance2
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.model.GptRequestModel
import com.teamdefine.legalvault.main.home.model.GptResponseModel
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeVM : BaseViewModel() {
    private val _gptResponse: MutableLiveData<GptResponseModel> = MutableLiveData()
    val gptResponse: LiveData<GptResponseModel>
        get() = _gptResponse

    fun generateAgreement(prompt: String) {
        val body = GptRequestModel(prompt)
        try {
            viewModelScope.launch {
                val response = RetrofitInstance2.gptApi.getAgreement(body)
                if (response.isSuccessful) {
                    _gptResponse.postValue(response.body())
                    updateLoadingModel(LoadingModel.COMPLETED)
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                }
            }
        } catch (e: Exception) {
            updateLoadingModel(LoadingModel.ERROR)
            Timber.e(e.message.toString())
        }
    }
}