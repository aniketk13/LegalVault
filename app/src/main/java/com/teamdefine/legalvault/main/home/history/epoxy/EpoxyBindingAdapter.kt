package com.teamdefine.legalvault.main.home.history.epoxy

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["setTamperProof"], requireAll = true)
fun setTamperProof(view: ImageView, tamperProof: Boolean) {
    view.visibility = if (tamperProof) View.VISIBLE else View.GONE
}