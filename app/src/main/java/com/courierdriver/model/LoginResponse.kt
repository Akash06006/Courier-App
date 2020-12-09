package com.courierdriver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("body")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null
    var code: Int? = null

    override fun toString(): String {
        return "ClassPojo [data = $data, message = $message, status = $code]"
    }

    inner class Data {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("firstName")
        @Expose
        var firstName: String? = null

        @SerializedName("lastName")
        @Expose
        var lastName: String? = null

        @SerializedName("gender")
        @Expose
        var gender: String? = null

        @SerializedName("dob")
        @Expose
        var dob: Any? = null

        @SerializedName("phoneNumber")
        @Expose
        var phoneNo: String? = null

        @SerializedName("countryCode")
        @Expose
        var countryCode: String? = null

        @SerializedName("isSocial")
        @Expose
        var isSocial: String? = null

        @SerializedName("socialType")
        @Expose
        var socialType: String? = null

        @SerializedName("socialId")
        @Expose
        var socialId: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("isDocUploaded")
        @Expose
        var isDocUploaded: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("sessionToken")
        @Expose
        var token: String? = null

        @SerializedName("userType")
        @Expose
        var userType: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("refreshToken")
        @Expose
        var refreshToken: String? = null

        @SerializedName("regionId")
        @Expose
        var regionId: String? = null
    }
}
