package com.teamdefine.legalvault.main.home.mydocs.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyDocsResponseModel(
    val list_info: ListInfo,
    val signature_requests: ArrayList<SignatureRequest>
) : Parcelable

@Parcelize
data class ListInfo(
    val num_results: Int
) : Parcelable

@Parcelize
data class SignatureRequest(
    val subject: String,
    val created_at: Long,
    val is_complete: Boolean,
    val files_url: String,
    val client_id: String,
    val signature_request_id: String,
    val signatures: ArrayList<Signatures?>
) : Parcelable

@Parcelize

data class Signatures(
    val signer_email_address: String,
    val signer_name: String,
    val signature_id: String
) : Parcelable
