package com.teamdefine.legalvault.main.home.mydocs.models

data class SignUrlResponseModel(
    val embedded:Url
)

data class Url(
    val sign_url:String
)