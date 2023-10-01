package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubRequestModel
import com.teamdefine.legalvault.main.home.bottomsheet.model.GitHubUpdateFileStatusResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface GitHubAPI {

    @Headers(
        "Accept: application/vnd.github+json",
        "Authorization: Bearer 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"
    )
    @POST("repos/aniketk13/testing/actions/workflows/deploy.yaml/dispatches")
    suspend fun updateFile(@Body body: GitHubRequestModel): Response<Any>

    @GET("repos/aniketk13/testing/actions/workflows/{workflow_name}/runs")
    suspend fun getFileUpdateStatus(@Path("workflow_name") workflow_name: String): Response<GitHubUpdateFileStatusResponseModel>
}