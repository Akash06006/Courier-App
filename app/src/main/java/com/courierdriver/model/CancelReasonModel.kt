package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class CancelReasonModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: ArrayList<Body>? = null

    class Body {

        @SerializedName("id")
        val id: String? = null

        @SerializedName("reason")
        val reason: String? = null

        @SerializedName("status")
        val status: Int? = null

        @SerializedName("type")
        val type: String? = null

        @SerializedName("companyId")
        val companyId: String? = null

        @SerializedName("createdAt")
        val createdAt: String? = null
    }
}