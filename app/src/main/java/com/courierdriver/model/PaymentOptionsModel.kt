package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class PaymentOptionsModel
{
    @SerializedName("code") val code : Int?=null
    @SerializedName("message") val message : String?=null
    @SerializedName("body") val body : ArrayList<Body>?=null

    class Body {
        @SerializedName("id")
        val id: String? = null
        @SerializedName("type")
        val type: String? = null
        @SerializedName("isSelected")
        val isSelected: Boolean? = null
        @SerializedName("gpayNo")
        val gpayNo: String? = null
        @SerializedName("phonePayNo")
        val phonePayNo: String? = null
        @SerializedName("paytmNo")
        val paytmNo: String? = null
    }
}

