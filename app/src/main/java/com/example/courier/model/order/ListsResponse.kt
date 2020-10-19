package com.example.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ListsResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("vehicleData")
        @Expose
        var vehicleData : ArrayList<VehicleData>? = null
        @SerializedName("weightData")
        @Expose
        var weightData : ArrayList<WeightData>? = null
        @SerializedName("deliveryOptionData")
        @Expose
        var deliveryOptionData : ArrayList<DeliveryOptionData>? = null
        @SerializedName("bannersData")
        @Expose
        var bannersData : ArrayList<BannersData>? = null

    }

    class VehicleData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null

    }

    class WeightData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null
    }

    class DeliveryOptionData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("title")
        @Expose
        var title : String? = null
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
