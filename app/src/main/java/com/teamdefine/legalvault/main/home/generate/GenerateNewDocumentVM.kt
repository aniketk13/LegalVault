package com.teamdefine.legalvault.main.home.generate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance2
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.model.GptRequestModel
import com.teamdefine.legalvault.main.home.model.GptResponseModel
import com.teamdefine.legalvault.main.utility.Utility
import com.teamdefine.legalvault.main.utility.event.Event
import kotlinx.coroutines.launch
import timber.log.Timber

class GenerateNewDocumentVM : BaseViewModel() {
    private val _gptResponse: MutableLiveData<Event<GptResponseModel>> = MutableLiveData()
    val gptResponse: LiveData<Event<GptResponseModel>>
        get() = _gptResponse

    fun generateAgreement(prompt: String) {
        val body = GptRequestModel(prompt)
        try {
            updateLoadingModel(LoadingModel.LOADING)
            viewModelScope.launch {
                val response = RetrofitInstance2.gptApi.getAgreement(body)
                if (response.isSuccessful) {
                    _gptResponse.postValue(Event(response.body()!!))
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

    fun extractText(url: String) {
        viewModelScope.launch {
            Timber.e(Utility.extractTextFromPdfUrl(url))
        }
    }
}