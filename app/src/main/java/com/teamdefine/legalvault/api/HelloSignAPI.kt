package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.main.home.mydocs.models.MyDocsResponseModel
import com.teamdefine.legalvault.main.home.mydocs.models.SignUrlResponseModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppRequestModel
import com.teamdefine.legalvault.main.onboarding.model.ApiAppResponseModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignRequestModel
import com.teamdefine.legalvault.main.review.model.EmbeddedSignResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface HelloSignAPI {
    @Headers("Content-Type: application/json")
    @POST("v3/api_app")
    suspend fun createApp(@Body body: ApiAppRequestModel): Response<ApiAppResponseModel>

    @Headers("Content-Type: application/json")
    @POST("v3/signature_request/create_embedded")
    suspend fun sendDocForSignatures(@Body signatureDocBody: EmbeddedSignRequestModel): Response<EmbeddedSignResponseModel>

    @GET("v3/signature_request/list")
    suspend fun getSignatureRequests(): Response<MyDocsResponseModel>

    @GET("v3/embedded/sign_url/{signature_id}")
    suspend fun getSigningUrl(@Path("signature_id") signature_id: String): Response<SignUrlResponseModel>

    @GET("v3/signature_request/files/{files_url}")
    suspend fun getFile(@Path("files_url")fileUrl: String): Response<Unit>


}