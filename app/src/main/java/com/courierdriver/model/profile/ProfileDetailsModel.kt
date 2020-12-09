package com.courierdriver.model.profile

import com.google.gson.annotations.SerializedName

class ProfileDetailsModel {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("userName")
        val userName: String? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("phoneNumber")
        val phoneNumber: String? = null

        @SerializedName("status")
        val status: String? = null

        @SerializedName("firstName")
        val firstName: String? = null

        @SerializedName("avgRating")
        val avgRating: String? = null

        @SerializedName("totalOrders")
        val totalOrders: String? = null

        @SerializedName("isApproved")
        val isApproved: Boolean? = null

        @SerializedName("noCompletedOrds")
        val noCompletedOrds: Boolean? = null

        @SerializedName("delayedOrders")
        val delayedOrders: String? = null

        @SerializedName("referralCode")
        val referralCode: String? = null
    }
}