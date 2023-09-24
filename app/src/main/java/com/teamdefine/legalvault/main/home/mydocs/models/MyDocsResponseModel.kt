package com.teamdefine.legalvault.main.home.mydocs.models

data class MyDocsResponseModel(
    val list_info: ListInfo,
    val signature_requests: ArrayList<SignatureRequest>
)

data class ListInfo(
    val num_results: Int
)

data class SignatureRequest(
    val subject: String,
    val created_at: Long,
    val is_complete: Boolean,
    val files_url: String,
    val client_id: String,
    val signature_request_id: String
)
