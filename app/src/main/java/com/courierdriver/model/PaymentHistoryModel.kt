package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class PaymentHistoryModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: ArrayList<Body>? = null

    class Body {

        @SerializedName("type")
        val type: String? = null

        @SerializedName("orderId")
        val orderId: String? = null

        @SerializedName("orderNo")
        val orderNo: String? = null

        @SerializedName("orderPrice")
        val orderPrice: String? = null

        @SerializedName("driverEarning")
        val driverEarning: String? = null

        @SerializedName("paymentType")
        val paymentType: String? = null
    }
}