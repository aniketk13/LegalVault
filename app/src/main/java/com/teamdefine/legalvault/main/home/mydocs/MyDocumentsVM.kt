package com.teamdefine.legalvault.main.home.mydocs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.api.RetrofitInstance2
import com.teamdefine.legalvault.api.RetrofitInstance3
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubRequestModel
import com.teamdefine.legalvault.main.home.model.GptRequestModel
import com.teamdefine.legalvault.main.home.model.GptResponseModel
import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import com.teamdefine.legalvault.main.home.mydocs.models.SignUrlResponseModel
import com.teamdefine.legalvault.main.utility.event.Event
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

    private val _pdfUrl: MutableLiveData<String> =
        MutableLiveData()
    val pdfUrl: LiveData<String>
        get() = _pdfUrl

    private val _docText: MutableLiveData<String> = MutableLiveData()
    val docText: LiveData<String>
        get() = _docText

    private val _gptResponseSummary: MutableLiveData<Event<GptResponseModel>> = MutableLiveData()
    val gptResponseSummary: LiveData<Event<GptResponseModel>>
        get() = _gptResponseSummary

    fun getAllDocs() {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response = RetrofitInstance.api.getSignatureRequests()
                if (response.isSuccessful) {
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

    fun summarizeDoc(prompt: String) {
        val body = GptRequestModel(prompt)
        try {
            updateLoadingModel(LoadingModel.LOADING)
            viewModelScope.launch {
                val response = RetrofitInstance2.gptApi.getAgreement(body)
                if (response.isSuccessful) {
                    _gptResponseSummary.postValue(Event(response.body()!!))
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

    fun getDocTextFromFirebase(signatureId: String) {
        try {
            updateLoadingModel(LoadingModel.LOADING)
            firestoreInstance.collection("linkedLists").document(signatureId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val request = documentSnapshot.getString("text")
                        request?.let {
                            _docText.postValue(it)
                        }
                    }
                    updateLoadingModel(LoadingModel.COMPLETED)
                }
                .addOnFailureListener { e ->
                    updateLoadingModel(LoadingModel.ERROR)
                    Timber.e("Error getting document: $e")
                }
        } catch (e: Exception) {
            updateLoadingModel(LoadingModel.ERROR)
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
//                    updateLoadingModel(LoadingModel.COMPLETED)
                    _updateRequestSuccess.postValue(true)
                    updateLoadingModel(LoadingModel.COMPLETED)
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
//                            updateLoadingModel(LoadingModel.COMPLETED)
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
            delay(12000)
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
                delay(12000)
            }
        }
    }

    fun getFile(signatureId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getFile(signatureId)
                if (response.isSuccessful) {
                    _pdfUrl.postValue(response.body()?.file_url)
                }
            } catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
    }
}
