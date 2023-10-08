package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.main.home.generate.InfuraAddResponse
import okhttp3.MultipartBody
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InfuraAPI {
    @Multipart
    @Headers("Authorization: Basic MldUeWhaMkJUWkRYUTl2U09NSmozSEZCcU94OmRkMDg0ZGYwZTVjZjkyOGE0MzVlZDUxM2YzNmY3Nzcz")
    @POST("api/v0/add?pin=true&cid-version=1")
    suspend fun addDocumentToInfura(@Part filePart: MultipartBody.Part): InfuraAddResponse

}