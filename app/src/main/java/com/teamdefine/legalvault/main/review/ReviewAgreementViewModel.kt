package com.teamdefine.legalvault.main.review

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.utility.Utility
import com.teamdefine.legalvault.main.utility.event.Event
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.io.File

class ReviewAgreementViewModel : BaseViewModel(), KoinComponent {

    private val _localFilePath: MutableLiveData<String?> = MutableLiveData()
    val localFilePath: LiveData<String?>
        get() = _localFilePath

    private val _publicSavedFileUrl: MutableLiveData<String> = MutableLiveData()
    val publicSavedFileUrl: LiveData<String>
        get() = _publicSavedFileUrl

    private val _docSentForSignatures: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val docSentForSignatures: LiveData<Event<Boolean>>
        get() = _docSentForSignatures

    fun generatePdf(context: Context, generatedText: String, fileName: String, view: View) {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val path = Utility.createPdfFromTextAsync(
                    context,
                    generatedText,
                    fileName,
                    view
                )
                updateLoadingModel(LoadingModel.COMPLETED)
                _localFilePath.postValue(path)
            } catch (e: Exception) {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e(e.message.toString())
            }
        }
    }

    fun savePdfToStorage(localFilePath: String, fileName: String, firebaseUser: FirebaseUser) {
        try {
            updateLoadingModel(LoadingModel.LOADING)
            val storageRef =
                Firebase.storage.reference.child("allDocuments/${firebaseUser.uid}/$fileName")
            val fileUri = Uri.fromFile(File(localFilePath))
            storageRef.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    Timber.e("Downlaod URL: ${it.toString()}")
                    _publicSavedFileUrl.postValue(it.toString())
                    updateLoadingModel(LoadingModel.COMPLETED)
                }
            }.addOnFailureListener {
                updateLoadingModel(LoadingModel.ERROR)
                Timber.e("Failure: ${it.message.toString()}")
            }
        } catch (e: java.lang.Exception) {
            updateLoadingModel(LoadingModel.ERROR)
            Timber.e(e.message.toString())
        }
    }

    fun sendDocForSignatures(signatureDocBody: EmbeddedSignRequestModel) {
        viewModelScope.launch {
            try {
                updateLoadingModel(LoadingModel.LOADING)
                val response = RetrofitInstance.api.sendDocForSignatures(signatureDocBody)
                if (response.isSuccessful) {
                    updateLoadingModel(LoadingModel.COMPLETED)
                    Timber.e("Successful")
                    _docSentForSignatures.postValue(Event(true))
                } else {
                    updateLoadingModel(LoadingModel.ERROR)
                    Timber.e("Unsuccessful: ${response.errorBody().toString()}")
                }
            } catch (e: Exception) {
                updateLoadingModel(LoadingModel.LOADING)
                Timber.e(e.message.toString())
            }
        }
    }
}