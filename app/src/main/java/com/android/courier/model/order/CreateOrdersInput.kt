package com.android.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CreateOrdersInput {
    @SerializedName("pickupAddress")
    @Expose
    var pickupAddress : PickupAddress? = null
    @SerializedName("deliveryAddress")
    @Expose
    var deliveryAddress : ArrayList<PickupAddress>? = null
    @SerializedName("weight")
    @Expose
    var weight : String? = null
    @SerializedName("address1")
    @Expose
    var address1 : String? = null
    @SerializedName("vehicle")
    @Expose
    var vehicle : VehicleData? = null
    @SerializedName("deliveryoption")
    @Expose
    var deliveryoption : DeliveryOptionData? = null
    @SerializedName("orderNo")
    @Expose
    var orderNo : String? = null
    @SerializedName("orderId")
    @Expose
    var orderId : String? = null
    @SerializedName("pendingCCharges")
    @Expose
    var pendingCCharges : String? = null
    @SerializedName("cancelOrderId")
    @Expose
    var cancelOrderIds : String? = null
    @SerializedName("usedLPoints")
    @Expose
    var usedLPoints : String? = null
    @SerializedName("lPointsPrice")
    @Expose
    var lPointsPrice : String? = null
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
    @SerializedName("isaddAltered")
    @Expose
    var isaddAltered : String? = "false"


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
    @SerializedName("offerPrice")
    @Expose
    var offerPrice : String? = null
    @SerializedName("securityFee")
    @Expose
    var securityFee : String? = null
    @SerializedName("notifyRecipient")
    @Expose
    var notifyRecipient : String? = null
    @SerializedName("notifyMe")
    @Expose
    var notifyMe : String? = null
    @SerializedName("deliveryCharges")
    @Expose
    var deliveryCharges : String? = null
    @SerializedName("orderPrice")
    @Expose
    var orderPrice : String? = null
    @SerializedName("fareCollected")
    @Expose
    var fareCollected : String? = null
    @SerializedName("weightValue")
    @Expose
    var weightValue : String? = null
    @SerializedName("deliveryType")
    @Expose
    var deliveryType : String? = null

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
        var id : Int = -1
        @SerializedName("address")
        @Expose
        var address : String? = null
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
        @SerializedName("isComplete")
        @Expose
        var isComplete : String? = null

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
