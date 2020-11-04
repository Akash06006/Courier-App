package com.courierdriver.model.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegionResponse {
    @SerializedName("body")
    @Expose
    var data: ArrayList<Data>? = null

    @SerializedName("message")
    var message: String? = null
    var code: Int? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

    }
}
