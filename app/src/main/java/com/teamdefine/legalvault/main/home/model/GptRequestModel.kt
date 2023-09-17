package com.teamdefine.legalvault.main.home.model

data class GptRequestModel(
    val input: String,
    val max_new_tokens: Int = 1000
)