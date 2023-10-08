package com.teamdefine.legalvault.main.home.generate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance2
import com.teamdefine.legalvault.api.RetrofitInstance4
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.model.GptRequestModel
import com.teamdefine.legalvault.main.home.model.GptResponseModel
import com.teamdefine.legalvault.main.utility.Utility
import com.teamdefine.legalvault.main.utility.event.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

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

    fun uploadDocumentToInfura(docPath: String) {
        try {
            viewModelScope.launch {
                val fileToUpload = File(docPath)
                val requestBody =
                    RequestBody.create("application/octet-stream".toMediaType(), fileToUpload)
                val filePart =
                    MultipartBody.Part.createFormData("file", fileToUpload.name, requestBody)
                val response = RetrofitInstance4.infuraAPI.addDocumentToInfura(filePart)
            }
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

}