package com.teamdefine.legalvault.main.review.model

data class EmbeddedSignResponseModel(
    val signature_request: Response
)

data class Response(
    val signature_request_id: String
)