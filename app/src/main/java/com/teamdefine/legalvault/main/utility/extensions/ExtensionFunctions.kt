package com.teamdefine.legalvault.main.utility.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.teamdefine.legalvault.main.base.LoadingModel

fun Context.toast(message: String) {
    Toast.makeText(
        this, message,
        if (message.length <= 25) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).show()
}

fun Fragment.toast(msg: String) {
    requireContext().toast(msg)
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.setVisibilityBasedOnLoadingModel(loadingModel: LoadingModel) {
    visibility = when (loadingModel) {
        LoadingModel.LOADING -> View.VISIBLE
        else -> View.GONE
    }
}