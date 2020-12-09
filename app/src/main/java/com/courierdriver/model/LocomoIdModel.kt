package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class LocomoIdModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("employeeId")
        val employeeId: String? = null

        @SerializedName("userName")
        val userName: String? = null

        @SerializedName("city")
        val city: String? = null

        @SerializedName("phoneNumber")
        val phoneNumber: String? = null

        @SerializedName("locomoNo")
        val locomoNo: String? = null

        @SerializedName("locomoEmail")
        val locomoEmail: String? = null

        @SerializedName("locomoIdUrl")
        val locomoIdUrl: String? = null

        @SerializedName("image")
        val image: String? = null
    }
}