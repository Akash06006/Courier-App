package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class ProfileDocumentModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("poaFront")
        val poaFront: String? = null

        @SerializedName("poaBack")
        val poaBack: String? = null

        @SerializedName("panCard")
        val panCard: String? = null

        @SerializedName("licenseFront")
        val licenseFront: String? = null

        @SerializedName("licenseBack")
        val licenseBack: String? = null

        @SerializedName("dlNumber")
        val dlNumber: String? = null

        @SerializedName("transport")
        val transport: Transport? = null

        @SerializedName("poaFApproved")
        val poaFApproved: Boolean? = null

        @SerializedName("poaBApproved")
        val poaBApproved: Boolean? = null

        @SerializedName("panApproved")
        val panApproved: Boolean? = null

        @SerializedName("licenseFApproved")
        val licenseFApproved: Boolean? = null

        @SerializedName("licenseBApproved")
        val licenseBApproved: Boolean? = null

        class Transport {
            @SerializedName("id")
            val id: String? = null

            @SerializedName("name")
            val name: String? = null
        }
    }
}