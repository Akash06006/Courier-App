package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class HelpLinksModel {

    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("howWorksLink")
        val howWorksLink: String? = null
        @SerializedName("avoidFineLink")
        val avoidFineLink: String? = null
        @SerializedName("collectPaymentLink")
        val collectPaymentLink: String? = null
        @SerializedName("faqLink")
        val faqLink: String? = null
        @SerializedName("payCommissionLink")
        val payCommissionLink: String? = null
        @SerializedName("impPoints")
        val impPoints: String? = null
        @SerializedName("resFirstOrderLink")
        val resFirstOrderLink: String? = null
        @SerializedName("contactUsLink")
        val contactUsLink: String? = null
    }
}