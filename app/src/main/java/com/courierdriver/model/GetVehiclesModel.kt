package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class GetVehiclesModel {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: ArrayList<Body>? = null

    class Body {

        @SerializedName("icon")
        val icon: String? = null

        @SerializedName("id")
        val id: String? = null

        @SerializedName("name")
        val name: String? = null

        @SerializedName("price")
        val price: Int? = null
    }
}