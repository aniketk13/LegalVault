package com.teamdefine.legalvault.main.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestoreInstance = FirebaseFirestore.getInstance()

    private val _loadingModel: MutableLiveData<LoadingModel> = MutableLiveData()
    val loadingModel: LiveData<LoadingModel>
        get() = _loadingModel

    private val _firestoreSnapshot: MutableLiveData<MutableMap<String, Any>> = MutableLiveData()
    val firestoreSnapshot: LiveData<MutableMap<String, Any>>
        get() = _firestoreSnapshot

    fun updateLoadingModel(loadingModel: LoadingModel = LoadingModel.COMPLETED) {
        _loadingModel.postValue(loadingModel)
    }

    fun getDataFromFirestore() {
        viewModelScope.launch {
            updateLoadingModel(LoadingModel.LOADING)
            firebaseAuth.currentUser?.let {
                firestoreInstance.collection("Users").document(firebaseAuth.currentUser!!.uid).get()
                    .addOnSuccessListener { doc ->
                        doc?.let {
                            _firestoreSnapshot.postValue(it.data as MutableMap<String, Any>)
                            updateLoadingModel(LoadingModel.COMPLETED)
                        }
                    }.addOnFailureListener {
                        updateLoadingModel(LoadingModel.ERROR)
                        Timber.e(it.message.toString())
                    }
            }
            updateLoadingModel(LoadingModel.COMPLETED)
        }
    }
}