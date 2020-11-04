package com.android.courier.model.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DistanceResponse {
    @SerializedName("destination_addresses")
    @Expose
    var destination_addresses : ArrayList<String>? = null
    @SerializedName("rows")
    @Expose
    var rows : ArrayList<Rows>? = null
    @SerializedName("origin_addresses")
    @Expose
    var origin_addresses : ArrayList<String>? = null
    @SerializedName("status")
    @Expose
    var status : String? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Rows {
        @SerializedName("elements")
        @Expose
        var elements : ArrayList<Elements>? = null

    }

    class Elements {
        @SerializedName("duration")
        @Expose
        var duration : Duration? = null
        @SerializedName("distance")
        @Expose
        var distance : Distance? = null
        @SerializedName("status")
        @Expose
        var status : String? = null

    }

    class Distance {
        @SerializedName("text")
        @Expose
        var text : String? = null
        @SerializedName("value")
        @Expose
        var value : String? = null

    }

    class Duration {
        @SerializedName("text")
        @Expose
        var text : String? = null
        @SerializedName("value")
        @Expose
        var value : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null

    }

    class BannersData {
        @SerializedName("url")
        @Expose
        var url : String? = null
        @SerializedName("icon")
        @Expose
        var icon : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("code")
        @Expose
        var code : String? = null
        @SerializedName("description")
        @Expose
        var description : String? = null
        @SerializedName("discount")
        @Expose
        var discount : String? = null
        @SerializedName("type")
        @Expose
        var type : String? = null

    }

}
