package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.BuildConfig
import com.teamdefine.legalvault.main.home.model.GptRequestModel
import com.teamdefine.legalvault.main.home.model.GptResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface GptAPI {
    @Headers("Authorization: bearer ${BuildConfig.GPT_API_KEY}", "Content-Type: application/json")
    @POST("v1/inference/jondurbin/airoboros-l2-70b-gpt4-1.4.1")
    suspend fun getAgreement(@Body body: GptRequestModel): Response<GptResponseModel>
}