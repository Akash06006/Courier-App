package com.example.courier.model.profile

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class RegionResponse {
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null

    }
}
