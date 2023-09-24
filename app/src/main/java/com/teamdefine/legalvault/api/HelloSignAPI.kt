package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppRequestModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppResponseModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface HelloSignAPI {
    @Headers("Content-Type: application/json")
    @POST("v3/api_app")
    suspend fun createApp(@Body body: ApiAppRequestModel): Response<ApiAppResponseModel>

    @Headers("Content-Type: application/json")
    @POST("v3/signature_request/create_embedded")
    suspend fun sendDocForSignatures(@Body signatureDocBody: EmbeddedSignRequestModel): Response<Unit>

    @GET("v3/signature_request/list")
    suspend fun getSignatureRequests(): Response<MyDocsResponseModel>
}