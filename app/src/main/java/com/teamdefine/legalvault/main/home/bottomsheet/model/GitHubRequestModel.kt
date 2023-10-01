package com.teamdefine.legalvault.main.home.bottomsheet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GitHubRequestModel(
    val ref: String = "master",
    val inputs: Input
) : Parcelable

@Parcelize
data class Input(
    val new_content: String
) : Parcelable
