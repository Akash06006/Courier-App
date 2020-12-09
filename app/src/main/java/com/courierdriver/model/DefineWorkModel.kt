package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class DefineWorkModel {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: List<Body>? = null

    class Body {

        @SerializedName("id")
        val id: String? = null
        @SerializedName("que")
        val que: String? = null
        @SerializedName("option1")
        val option1: String? = null
        @SerializedName("option2")
        val option2: String? = null
        @SerializedName("option3")
        val option3: String? = null
        @SerializedName("option4")
        val option4: String? = null
        @SerializedName("status")
        val status: Int? = null
        @SerializedName("createdAt")
        val createdAt: Int? = null
    }
}