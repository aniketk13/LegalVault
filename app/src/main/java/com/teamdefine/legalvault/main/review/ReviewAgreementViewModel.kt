package com.teamdefine.legalvault.main.review

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.teamdefine.legalvault.api.RetrofitInstance
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.utility.Utility
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber
import java.io.File

class ReviewAgreementViewModel : BaseViewModel(), KoinComponent {

    private val _filePath: MutableLiveData<String?> = MutableLiveData()
    val filePath: LiveData<String?>
        get() = _filePath

    private val _savedFileUrl: MutableLiveData<String> = MutableLiveData()
    val savedFileUrl: LiveData<String>
        get() = _savedFileUrl
    fun generatePdf(context: Context, text: String, fileName: String, view: View) {
        viewModelScope.launch {
            try {
                val path = Utility.createPdfFromTextAsync(
                    context,
                    "Utility.getRandomText()",
                    "test1",
                    view
                )
                _filePath.postValue(path)
            } catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
    }

    fun savePdfToFirebase(fileName: String?, fileUri: Uri, firebaseUser: FirebaseUser) {

        val storageRef = Firebase.storage.reference.child("allDocuments/${firebaseUser.uid}/$fileName")
        storageRef.putFile(fileUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener {
                Timber.e("Success: ${it.path.toString()}")
                _savedFileUrl.postValue(it.path)
            }.addOnFailureListener {
                Timber.e("Failure: ${it.message.toString()}")
            }
        }
    }

    fun savePdfToStorage(localFilePath: String, fileName: String, firebaseUser: FirebaseUser){
        try {
            val storageRef = Firebase.storage.reference.child("allDocuments/${firebaseUser.uid}/$fileName")
            val fileUri = Uri.fromFile(File(localFilePath))
            storageRef.putFile(fileUri).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    Timber.e("Downlaod URL: ${it.toString()}")
                    _savedFileUrl.postValue(it.toString())
                }
            }.addOnFailureListener {
                Timber.e("Failure: ${it.message.toString()}")
            }
        }catch (e: java.lang.Exception){
            Timber.e(e.message.toString())
        }
    }

    fun sendDocForSignatures(signatureDocBody: EmbeddedSignRequestModel){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.sendDocForSignatures(signatureDocBody)
                if(response.isSuccessful)
                    Timber.e("Successful")
                else{
                    Timber.e("Unsuccessful: ${response.errorBody().toString()}")
                }
            } catch (e: Exception) {
                Timber.e(e.message.toString())
            }
        }
    }
}