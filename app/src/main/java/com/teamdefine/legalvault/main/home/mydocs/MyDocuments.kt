package com.teamdefine.legalvault.main.home.mydocs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.databinding.FragmentMyDocumentsBinding
import timber.log.Timber
import java.util.concurrent.Executor

class MyDocuments : Fragment() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var binding: FragmentMyDocumentsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private val viewmodel: MyDocumentsVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentMyDocumentsBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        viewmodel.getAllDocs()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initClickListeners()
        initViews()
        initObservers()
    }

    private fun initViews() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = createBiometricPrompt()
        showBiometricPrompt()
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Biometric authentication succeeded
                // You can implement your logic here, e.g., navigate to the secure part of the app.
                Timber.i("Biometric Success")

            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Handle authentication errors here
            }
        }

        return BiometricPrompt(this, executor, callback)
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setDeviceCredentialAllowed(true)
//            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }


    private fun initClickListeners() {
        TODO("Not yet implemented")
    }

    private fun initObservers() {
        viewmodel.myDocs.observe(viewLifecycleOwner, Observer {
            viewmodel.getDataFromFirestore()
        })
        viewmodel.firestoreSnapshot.observe(viewLifecycleOwner, Observer { snapshot ->
            val clientId = snapshot.getValue("clientId") as String
            viewmodel.myDocs.value?.signature_requests?.let { requests ->
                val filteredList = requests.filter { it.client_id == clientId }
                //send data to recycler view
            }
        })
    }
}