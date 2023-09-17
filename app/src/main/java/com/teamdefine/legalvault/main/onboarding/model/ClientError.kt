package com.teamdefine.legalvault.main.onboarding.model

data class ClientError(
    val error: AppError
)

data class AppError(
    val error_msg: String,
    val error_name: String
)

