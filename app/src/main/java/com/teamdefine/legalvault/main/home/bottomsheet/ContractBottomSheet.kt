package com.teamdefine.legalvault.main.home.bottomsheet


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.teamdefine.legalvault.databinding.LayoutMenuBottomSheetBinding
import com.teamdefine.legalvault.main.home.BiometricAuthListener
import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubRequestModel
import com.teamdefine.legalvault.main.home.bottomsheet.model.Input
import com.teamdefine.legalvault.main.home.mydocs.MyDocumentsVM
import com.teamdefine.legalvault.main.home.mydocs.models.SignatureRequest
import com.teamdefine.legalvault.main.utility.BiometricUtil
import timber.log.Timber

class ContractBottomSheet : BottomSheetDialogFragment(), BiometricAuthListener {
    private val bottomSheetVM: MyDocumentsVM by activityViewModels()
    private lateinit var binding: LayoutMenuBottomSheetBinding
    private lateinit var signature: SignatureRequest
    private lateinit var firebaseInstance: FirebaseAuth
    private var flag: Int = 0


    companion object {
        fun newInstance(
            signature: SignatureRequest
        ): ContractBottomSheet {
            val fragment = ContractBottomSheet()
            val args = Bundle().apply {
                putParcelable("SIGNATURE", signature)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = LayoutMenuBottomSheetBinding.inflate(inflater, container, false).also {
        firebaseInstance = FirebaseAuth.getInstance()
        binding = it
        signature = arguments?.getParcelable("SIGNATURE")!!
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initClickListeners()
        initObserver()
    }

    private fun initObserver() {
        bottomSheetVM.signingUrl.observe(viewLifecycleOwner, Observer {
            showBiometricLoginOption()
        })
        bottomSheetVM.statusUpdateFile.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Timber.i("New file sent to github")
            }
        })
    }

    private fun initViews() {
        binding.contractName.text = signature.subject
    }

    private fun initClickListeners() {
        binding.apply {
            this.signContract.setOnClickListener {
                flag = 2
                val signers = signature.signatures
                val filter =
                    signers.indexOfFirst { it?.signer_email_address == firebaseInstance.currentUser?.email }
                signers[filter]?.let { it1 -> bottomSheetVM.getDocSignUrl(it1.signature_id) }
            }
            previewContract.setOnClickListener {
                flag = 1
            }
            modifyContract.setOnClickListener {
                flag = 3
            }
        }
    }

    private fun showBiometricLoginOption() {
        BiometricUtil.showBiometricPrompt(this, this)
    }

    override fun onBiometricAuthenticationSuccess(result: BiometricPrompt.AuthenticationResult) {
        Timber.i("Success Biometric")
        when (flag) {
            1 -> Timber.i("Success with Previewing Contract")
            2 -> {
                Timber.i("Success with Signing Contract")
                var newContent: String? = null
                try {
                    val inputStream = requireContext().assets.open("scratch.html")
                    newContent = inputStream.bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (newContent != null) {
                    Timber.i(newContent)
                    newContent = newContent.replace(
                        "__SIGN_URL__",
                        "${bottomSheetVM.signingUrl.value?.embedded?.sign_url}"
                    )
                    Timber.i(newContent)
                    val body = GitHubRequestModel(inputs = Input(new_content = "$newContent"))
                    Timber.i(body.toString())
                    bottomSheetVM.updateFileGithub(body)
                }
            }

            3 -> Timber.i("Success with Modifying Contract")
            else -> Timber.i("Error/Select an Option")
        }
    }

    override fun onBiometricAuthenticationError(errorCode: Int, errorMessage: String) {
    }
}