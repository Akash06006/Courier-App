package com.example.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CreateOrderResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("pickupAddress")
        @Expose
        var pickupAddress : PickupAddress? = null
        @SerializedName("deliveryAddress")
        @Expose
        var deliveryAddress : ArrayList<PickupAddress>? = null
        @SerializedName("weight")
        @Expose
        var weight : WeightData? = null
        @SerializedName("vehicle")
        @Expose
        var vehicle : VehicleData? = null
        @SerializedName("deliveryoption")
        @Expose
        var deliveryoption : DeliveryOptionData? = null
        @SerializedName("orderNo")
        @Expose
        var orderNo : String? = null
        @SerializedName("orderStatus")
        @Expose
        var orderStatus : OrderStatus? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("itemName")
        @Expose
        var itemName : String? = null
        @SerializedName("vehicleType")
        @Expose
        var vehicleType : String? = null
        @SerializedName("deliveryOption")
        @Expose
        var deliveryOption : String? = null
        @SerializedName("weightId")
        @Expose
        var weightId : String? = null
        @SerializedName("promoCode")
        @Expose
        var promoCode : String? = null
        @SerializedName("totalOrderPrice")
        @Expose
        var totalOrderPrice : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
        @SerializedName("userId")
        @Expose
        var userId : String? = null
        @SerializedName("progressStatus")
        @Expose
        var progressStatus : String? = null
        @SerializedName("userShow")
        @Expose
        var userShow : String? = null
        @SerializedName("trackStatus")
        @Expose
        var trackStatus : String? = null
        @SerializedName("trackingLatitude")
        @Expose
        var trackingLatitude : String? = null
        @SerializedName("trackingLongitude")
        @Expose
        var trackingLongitude : String? = null
        @SerializedName("cancellationReason")
        @Expose
        var cancellationReason : String? = null
        @SerializedName("paymentType")
        @Expose
        var paymentType : String? = null
        @SerializedName("cancellable")
        @Expose
        var cancellable : String? = null
        @SerializedName("distance")
        @Expose
        var distance : String? = null
        @SerializedName("parcelValue")
        @Expose
        var parcelValue : String? = null
        @SerializedName("orderPrice")
        @Expose
        var orderPrice : String? = null
        @SerializedName("fareCollected")
        @Expose
        var fareCollected : String? = null

    }

    class OrderStatus {
        @SerializedName("statusName")
        @Expose
        var statusName : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null
        @SerializedName("parentStatus")
        @Expose
        var parentStatus : String? = null
    }

    class PickupAddress {
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("lat")
        @Expose
        var lat : String? = null
        @SerializedName("long")
        @Expose
        var long : String? = null
        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber : String? = null
        @SerializedName("date")
        @Expose
        var date : String? = null
        @SerializedName("time")
        @Expose
        var time : String? = null

    }

    class VehicleData {
        @SerializedName("icon")
        @Expose
        var icon : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null

    }

    class WeightData {
        @SerializedName("icon")
        @Expose
        var icon : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("status")
        @Expose
        var status : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
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
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null

    }

}
