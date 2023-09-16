package com.teamdefine.legalvault.main.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppRequestModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppResponseModel
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginVM : BaseViewModel() {
    private val _appResponse: MutableLiveData<ApiAppResponseModel> = MutableLiveData()
    val appResponse: LiveData<ApiAppResponseModel>
        get() = _appResponse

    fun createAppGetClientID(userId: String) {
        val body = ApiAppRequestModel("LegalVault$userId", arrayListOf("teamdefine.legalvault.com"))
        try {
            viewModelScope.launch {
                val response = RetrofitInstance.api.createApp(body)
                if (response.isSuccessful) {
                    _appResponse.postValue(response.body())
                } else {
                    Timber.e(response.message().toString())
                }
            }
        } catch (e: Exception) {
            Timber.e(e.message.toString())
        }
    }

}