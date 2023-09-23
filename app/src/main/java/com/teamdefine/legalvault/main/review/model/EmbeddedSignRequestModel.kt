package com.teamdefine.legalvault.main.review.model

import com.google.gson.annotations.SerializedName

data class EmbeddedSignRequestModel(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("title")
    val documentTitle: String,
    @SerializedName("subject")
    val documentSubject: String,
    @SerializedName("message")
    val documentMessage: String,
    @SerializedName("signers")
    val documentSigners: ArrayList<Signers>,
    @SerializedName("file_urls")
    val fireUrls: ArrayList<String>,
    @SerializedName("test_mode")
    val testMode: Int = 1
) {
    constructor() : this(
        "d0b0258b7a737cda807b5b996f31a765",
        "TestTitle",
        "TestSubject",
        "TestMsg",
        arrayListOf(
            Signers()
        ),
        arrayListOf("https://firebasestorage.googleapis.com/v0/b/legal-v.appspot.com/o/allDocuments%2FRi7reDAIqbXB4zprqxER3asXgrW2%2FTestFile1?alt=media&token=14b57a1a-02fa-4bb2-8724-d6ac77543795")
    )
}

data class Signers(
    @SerializedName("role")
    val signerRole: String,
    @SerializedName("name")
    val signerName: String,
    @SerializedName("email_address")
    val signerEmail: String
) {
    constructor() : this("TestRole", "TestName", "nitish.sharma2210@gmail.com")
}

data class FileUrls(
    val fileUrl: String
) {
    constructor() : this("https://firebasestorage.googleapis.com/v0/b/legal-v.appspot.com/o/allDocuments%2FRi7reDAIqbXB4zprqxER3asXgrW2%2FTestFile1?alt=media&token=14b57a1a-02fa-4bb2-8724-d6ac77543795")
}
