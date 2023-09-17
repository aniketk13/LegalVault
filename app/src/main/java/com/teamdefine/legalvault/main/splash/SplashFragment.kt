package com.teamdefine.legalvault.main.splash

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.teamdefine.legalvault.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var firebaseInstance: FirebaseAuth
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentSplashBinding.inflate(layoutInflater, container, false).also {
        binding = it
        firebaseInstance = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userInfo = checkLoggedInStatus()
        if (userInfo != null) {
            navigateToHomeFragment()
        } else
            navigateToLoginFragment()
    }

    private fun navigateToLoginFragment() {
        Handler().postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }, 2000)
    }

    //change this to home
    private fun navigateToHomeFragment() {
        Handler().postDelayed({
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }, 2000)
    }

    private fun checkLoggedInStatus(): FirebaseUser? {
        firebaseInstance.currentUser?.let {
            return it
        }
        return null
    }
}