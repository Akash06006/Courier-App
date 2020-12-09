package com.courierdriver.model.profile

import com.google.gson.annotations.SerializedName

class AccountDetailsModel {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("regionId")
        val regionId: String? = null

        @SerializedName("regionName")
        val regionName: String? = null

        @SerializedName("transportId")
        val transportId: String? = null

        @SerializedName("transportName")
        val transportName: String? = null

        @SerializedName("firstName")
        val firstName: String? = null

        @SerializedName("lastName")
        val lastName: String? = null

        @SerializedName("image")
        val image: String? = null

        @SerializedName("email")
        val email: String? = null

        @SerializedName("phoneNumber")
        val phoneNumber: String? = null

        @SerializedName("password")
        val password: String? = null

        @SerializedName("referenceContact")
        val referenceContact: String? = null

        @SerializedName("referenceName")
        val referenceName: String? = null
    }
}