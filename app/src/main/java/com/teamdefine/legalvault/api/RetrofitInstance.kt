package com.teamdefine.legalvault.api

import com.teamdefine.legalvault.BuildConfig
import com.teamdefine.legalvault.auth.BasicAuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.hellosign.com/"
private const val BASE_URL2 = "https://api.deepinfra.com/"
private const val BASE_URL3 = "https://api.github.com/"

object RetrofitInstance {
    //interceptors
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    ).addInterceptor(
        BasicAuthInterceptor(
            BuildConfig.DROPBOX_SIGN_API_KEY,
            ""
        )
    )

    //private instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //public instance
    val api: HelloSignAPI by lazy {
        retrofit.create(HelloSignAPI::class.java)
    }

}

object RetrofitInstance2 {
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    ).connectTimeout(5, TimeUnit.MINUTES).callTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
    private val retrofit2 by lazy {
        Retrofit.Builder().baseUrl(BASE_URL2).client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val gptApi: GptAPI by lazy {
        retrofit2.create(GptAPI::class.java)
    }
}

object RetrofitInstance3 {
    var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(
        loggingInterceptor
    )
    private val retrofit3 by lazy {
        Retrofit.Builder().baseUrl(BASE_URL3).client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    val githubAPI: GitHubAPI by lazy {
        retrofit3.create(GitHubAPI::class.java)
    }
}