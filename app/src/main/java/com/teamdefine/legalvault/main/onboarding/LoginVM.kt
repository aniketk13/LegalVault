package com.teamdefine.legalvault.main.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppRequestModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppResponseModel
import com.teamdefine.legalvault.main.onboarding.model.ClientError
import com.teamdefine.legalvault.main.utility.event.Event
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginVM : BaseViewModel() {
    private val _appResponse: MutableLiveData<ApiAppResponseModel> = MutableLiveData()
    val appResponse: LiveData<ApiAppResponseModel>
        get() = _appResponse

    private val _clientError: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val clientError: LiveData<Event<Boolean>>
        get() = _clientError

    fun createAppGetClientID(userId: String) {
        val body = ApiAppRequestModel("LegalVault$userId", arrayListOf("teamdefine.legalvault.com"))
        try {
            viewModelScope.launch {
                val response = RetrofitInstance.api.createApp(body)
                if (response.isSuccessful) {
                    _appResponse.postValue(response.body())
                    updateLoadingModel(LoadingModel.COMPLETED)
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                    try {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ClientError::class.java
                        )
                        if (errorResponse.error.error_msg == "An app with the same name already exists")
                            _clientError.postValue(Event(true))
                        Timber.e("APP ERROR: ${response.message().toString()}")
                    } catch (e: Exception) {
                        Timber.e(e.message.toString())
                    }
                }
            }
        } catch (e: Exception) {
            updateLoadingModel(LoadingModel.ERROR)
            Timber.e(e.message.toString())
        }
    }

}