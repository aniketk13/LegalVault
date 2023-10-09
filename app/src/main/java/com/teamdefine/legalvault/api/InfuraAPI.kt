package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.BuildConfig
import com.teamdefine.legalvault.main.home.generate.InfuraAddResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface InfuraAPI {
    @Multipart
    @Headers("Authorization: Basic ${BuildConfig.Infura_API_KEY}")
    @POST("api/v0/add?pin=true&cid-version=1")
    suspend fun addDocumentToInfura(@Part filePart: MultipartBody.Part): InfuraAddResponse

    @Headers("Authorization: Basic ${BuildConfig.Infura_API_KEY}")
    @POST("api/v0/pin/rm")
    suspend fun removeDocument(@Query("arg") arg: String): Response<Any>

}