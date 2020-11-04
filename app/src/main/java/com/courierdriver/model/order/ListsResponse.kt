package com.courierdriver.model.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListsResponse {
    @SerializedName("body")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    var message: String? = null
    var code: Int? = null

    class Data {
        @SerializedName("vehicleData")
        @Expose
        var vehicleData: ArrayList<VehicleData>? = null

        @SerializedName("weightData")
        @Expose
        var weightData: ArrayList<WeightData>? = null

        @SerializedName("deliveryOptionData")
        @Expose
        var deliveryOptionData: ArrayList<DeliveryOptionData>? = null

    }

    class VehicleData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("selected")
        @Expose
        var selected: String? = null

    }

    class WeightData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("selected")
        @Expose
        var selected: String? = null
    }

    class DeliveryOptionData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

    }

}
