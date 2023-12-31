package com.teamdefine.legalvault.main.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import com.teamdefine.legalvault.main.utility.event.EventObserver
import com.teamdefine.legalvault.main.utility.extensions.setVisibilityBasedOnLoadingModel
import com.teamdefine.legalvault.main.utility.extensions.showSnackBar
import timber.log.Timber

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseInstance: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore
    private val viewModel: LoginVM by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentLoginBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseInstance = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()
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
        viewModel.clientError.observe(viewLifecycleOwner, EventObserver {
            if (it) {
                binding.root.showSnackBar("Going home")
                navigateToHomeScreen()
            }
        })
    }

    private fun saveUserToDb(firebaseUserId: String, clientId: String) {
        viewModel.updateLoadingModel(LoadingModel.LOADING)
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
            .requestIdToken(getString(R.string.default_web_client_id))
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
            Timber.i("CODES: ${result.resultCode.toString()}, ${Activity.RESULT_OK.toString()}")
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseInstance.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val signInResults = work.result?.signInMethods
                            Timber.e(work.result.signInMethods.toString())
                            if (signInResults.isNullOrEmpty())
                                userRegistration(saveUserToDb = true, task)
                            else
                                userRegistration(saveUserToDb = false, task)
                        } else {
                            viewModel.updateLoadingModel(LoadingModel.ERROR)
                            binding.root.showSnackBar("oops!")
                        }
                    }.addOnFailureListener {
                        Timber.e(it.message.toString())
                    }
            } else {
                binding.root.showSnackBar("Internal authentication error")
                viewModel.updateLoadingModel(LoadingModel.ERROR)
            }
        }

    private fun userRegistration(saveUserToDb: Boolean, task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, saveUserToDb)
            }
        } else {
            viewModel.updateLoadingModel(LoadingModel.ERROR)
            binding.root.showSnackBar("Some error occurred")
        }
    }

    private fun updateUI(account: GoogleSignInAccount, saveUserToDb: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseInstance.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (saveUserToDb) {
                    createDropBoxSignApp()
                } else {
                    viewModel.updateLoadingModel(LoadingModel.COMPLETED)
                    navigateToHomeScreen()
                }
            } else {
                viewModel.updateLoadingModel(LoadingModel.ERROR)
                binding.root.showSnackBar("Sign in failure. Please try after sometime.")
            }
        }
    }

    private fun createDropBoxSignApp() {
        viewModel.createAppGetClientID(firebaseInstance.currentUser?.uid.toString())
    }

    private fun navigateToHomeScreen() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        viewModel.updateLoadingModel(LoadingModel.COMPLETED)
    }
}