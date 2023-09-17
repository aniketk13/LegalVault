package com.teamdefine.legalvault.main.home.model

data class GptResponseModel(
    val results: ArrayList<generatedText>
)

data class generatedText(
    val generated_text: String
)