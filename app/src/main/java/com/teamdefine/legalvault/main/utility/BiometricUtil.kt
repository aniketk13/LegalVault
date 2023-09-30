package com.teamdefine.legalvault.main.utility

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.teamdefine.legalvault.main.home.BiometricAuthListener
import timber.log.Timber

object BiometricUtil {

    private fun initBiometricPrompt(
        context: Fragment,listener: BiometricAuthListener
    ): BiometricPrompt {
        // 1
        val executor = ContextCompat.getMainExecutor(context.requireContext())

        // 2
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Biometric authentication succeeded
                // You can implement your logic here, e.g., navigate to the secure part of the app.
                Timber.i("Biometric Success")
                super.onAuthenticationSucceeded(result)
                listener.onBiometricAuthenticationSuccess(result)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Handle authentication errors here
                listener.onBiometricAuthenticationError(errorCode, errString.toString())
            }
        }

        // 3
        return BiometricPrompt(context,executor,callback)
    }


    fun showBiometricPrompt(context: Fragment,listener:BiometricAuthListener) {
        // 1
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate using your fingerprint")
            .setDeviceCredentialAllowed(true)
//            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt= initBiometricPrompt(context,listener)
        biometricPrompt.authenticate(promptInfo)


    }

}