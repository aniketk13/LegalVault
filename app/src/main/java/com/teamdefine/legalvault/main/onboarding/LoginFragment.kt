package com.teamdefine.legalvault.main.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.legalvault.R
import com.teamdefine.legalvault.databinding.FragmentLoginBinding
import com.teamdefine.legalvault.main.base.LoadingModel
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import timber.log.Timber

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val firebaseInstance = FirebaseAuth.getInstance()
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val viewModel: LoginVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentLoginBinding.inflate(layoutInflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleAuth()
        initClickListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.appResponse.observe(viewLifecycleOwner) { appResponse ->
            saveUserToDb(
                firebaseInstance.currentUser?.uid.toString(),
                appResponse.api_app.client_id
            )
        }
        viewModel.loadingModel.observe(viewLifecycleOwner) {
            binding.loadingModel.progressBar.setVisibilityBasedOnLoadingModel(it)
        }
    }

    private fun saveUserToDb(firebaseUserId: String, clientId: String) {
        val user: MutableMap<String, Any> = HashMap()
        user["clientId"] = clientId
        firebaseDb.collection("Users").document(firebaseUserId).set(user).addOnSuccessListener {
            navigateToHomeScreen()
            binding.root.showSnackBar("User registered successfully")
        }.addOnFailureListener {
            viewModel.updateLoadingModel(LoadingModel.ERROR)
            Timber.e(it.message.toString())
            binding.root.showSnackBar(it.message.toString())
        }
    }

    private fun initGoogleAuth() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun initClickListeners() {
        binding.googleAuth.setOnClickListener {
            authenticateWithGoogle()
        }
    }

    private fun authenticateWithGoogle() {
        viewModel.updateLoadingModel(LoadingModel.LOADING)
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseInstance.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val result = work.result?.signInMethods
                            if (result.isNullOrEmpty())
                                userRegistration(isRegistered = false, task)
                            else
                                userRegistration(isRegistered = true, task)
                        } else {
                            viewModel.updateLoadingModel(LoadingModel.ERROR)
                            binding.root.showSnackBar("oops!")
                        }
                    }
            } else {
                binding.root.showSnackBar("Internal authentication error")
                viewModel.updateLoadingModel(LoadingModel.ERROR)
            }
        }

    private fun userRegistration(isRegistered: Boolean, task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, isRegistered)
            }
        } else {
            viewModel.updateLoadingModel(LoadingModel.ERROR)
            binding.root.showSnackBar("Some error occurred")
        }
    }

    private fun updateUI(account: GoogleSignInAccount, isRegistered: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseInstance.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (isRegistered) {
                    viewModel.updateLoadingModel(LoadingModel.COMPLETED)
                    navigateToHomeScreen()
                } else {
                    createDropBoxSignApp()
                }
            } else {
                viewModel.updateLoadingModel(LoadingModel.ERROR)
                binding.root.showSnackBar("Some error occurred")
            }
        }
    }

    private fun createDropBoxSignApp() {
        viewModel.createAppGetClientID(firebaseInstance.currentUser?.uid.toString())
    }

    private fun navigateToHomeScreen() {
        viewModel.updateLoadingModel(LoadingModel.COMPLETED)
    }
}