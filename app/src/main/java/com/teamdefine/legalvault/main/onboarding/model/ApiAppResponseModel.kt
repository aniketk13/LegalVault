package com.teamdefine.legalvault.main.onboarding.model

data class ApiAppResponseModel(
    val api_app: AppClient
)

data class AppClient(
    val client_id: String
)