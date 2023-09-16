package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.main.onboarding.model.ApiAppRequestModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HelloSignAPI {
    @Headers("Content-Type: application/json")
    @POST("v3/api_app")
    suspend fun createApp(@Body body: ApiAppRequestModel): Response<ApiAppResponseModel>
}