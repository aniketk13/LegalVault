package com.teamdefine.legalvault.main.home.mydocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.api.RetrofitInstance3
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubRequestModel
import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import com.teamdefine.legalvault.main.home.mydocs.models.SignUrlResponseModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MyDocumentsVM : BaseViewModel() {
    private val _myDocs: MutableLiveData<MyDocsResponseModel> = MutableLiveData()
    val myDocs: LiveData<MyDocsResponseModel>
        get() = _myDocs

    private val _signingUrl: MutableLiveData<SignUrlResponseModel> = MutableLiveData()
    val signingUrl: LiveData<SignUrlResponseModel>
        get() = _signingUrl

    private val _updateRequestSuccess: MutableLiveData<Boolean> = MutableLiveData(false)
    val updateRequestSuccess: LiveData<Boolean>
        get() = _updateRequestSuccess

    private val _updateFileSuccess: MutableLiveData<Boolean> =
        MutableLiveData(false)
    val updateFileSuccess: LiveData<Boolean>
        get() = _updateFileSuccess

    private val _pageDeployedSuccess: MutableLiveData<Boolean> =
        MutableLiveData(false)
    val pageDeployedSuccess: LiveData<Boolean>
        get() = _pageDeployedSuccess

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

    fun getDocSignUrl(signature_id: String) {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response = RetrofitInstance.api.getSigningUrl(signature_id)
                if (response.isSuccessful) {
                    updateLoadingModel(LoadingModel.COMPLETED)
                    _signingUrl.postValue(response.body())
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                }
            } catch (e: Exception) {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e(e.message.toString())
            }
        }
    }

    fun updateFileGithub(updateReqBody: GitHubRequestModel) {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response = RetrofitInstance3.githubAPI.updateFile(updateReqBody)
                if (response.isSuccessful) {
                    updateLoadingModel(LoadingModel.COMPLETED)
                    _updateRequestSuccess.postValue(true)
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                }
            } catch (e: Exception) {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e(e.message.toString())
            }
        }
    }

    fun getUpdateFileStatus() {
        viewModelScope.launch {
            delay(2000)
            while (true) {
                try {
                    updateLoadingModel(LoadingModel.LOADING)
                    val response = RetrofitInstance3.githubAPI.getFileUpdateStatus("deploy.yaml")
                    if (response.isSuccessful) {
                        if (response.body()?.workflow_runs?.get(0)?.status == "completed") {
                            _updateFileSuccess.postValue(true)
                            updateLoadingModel(LoadingModel.COMPLETED)
                            break
                        }
                    } else {
                        updateLoadingModel(LoadingModel.ERROR)
                    }
                } catch (e: Exception) {
                    updateLoadingModel(LoadingModel.ERROR)
                    Timber.e(e.message.toString())
                }
                delay(7000)
            }
        }
    }

    fun getPageDeploymentStatus() {
        viewModelScope.launch {
            delay(15000)
            while (true) {
                try {
                    updateLoadingModel(LoadingModel.LOADING)
                    val response = RetrofitInstance3.githubAPI.getFileUpdateStatus("70956816")
                    if (response.isSuccessful) {
                        if (response.body()?.workflow_runs?.get(0)?.status == "completed") {
                            _pageDeployedSuccess.postValue(true)
                            updateLoadingModel(LoadingModel.COMPLETED)
                            break
                        }
                    } else {
                        updateLoadingModel(LoadingModel.ERROR)
                    }
                } catch (e: Exception) {
                    updateLoadingModel(LoadingModel.ERROR)
                    Timber.e(e.message.toString())
                }
                delay(15000)
            }
        }
    }
}
