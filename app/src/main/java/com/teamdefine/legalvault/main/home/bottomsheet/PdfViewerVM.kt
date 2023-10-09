package com.teamdefine.legalvault.main.home.bottomsheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamdefine.legalvault.main.base.BaseViewModel
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.utility.event.Event
import timber.log.Timber

class PdfViewerVM : BaseViewModel() {

    private val _docHash: MutableLiveData<Event<String?>> = MutableLiveData()
    val docHash: LiveData<Event<String?>>
        get() = _docHash

    fun getDocHashFromFirebase(signatureId: String) {
        try {
            updateLoadingModel(LoadingModel.LOADING)
            firestoreInstance.collection("linkedLists").document(signatureId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val request = documentSnapshot.getString("hash")
                        request?.let {
                            _docHash.postValue(Event(it))
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
}