package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class GetSubjectsModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: ArrayList<Body>? = null

    class Body {

        @SerializedName("id")
        val id: String? = null

        @SerializedName("subject")
        val subject: String? = null

        @SerializedName("status")
        val status: Int? = null

        @SerializedName("createdAt")
        val createdAt: String? = null

        @SerializedName("updatedAt")
        val updatedAt: String? = null
    }
}