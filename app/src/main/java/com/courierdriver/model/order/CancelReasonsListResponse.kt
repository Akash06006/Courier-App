package com.example.courier.model.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CancelReasonsListResponse {
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

        @SerializedName("reason")
        @Expose
        var reason: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("companyId")
        @Expose
        var companyId: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

    }

}
