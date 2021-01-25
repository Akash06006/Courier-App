package com.android.courier.model.order

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class OrdersDetailResponse() : Serializable {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    /*deliveryOption -> {JsonPrimitive@8505} ""51b1c365-b88b-4ca4-bf01-4361c0a1ee3c""*/
    class Data : Serializable {
        @SerializedName("completedorder")
        @Expose
        var completedorder : ListsResponse.CompletedOrder? = null
        @SerializedName("pickupAddress")
        @Expose
        var pickupAddress : PickupAddress? = null
        @SerializedName("deliveryAddress")
        @Expose
        var deliveryAddress : ArrayList<PickupAddress>? = null
        @SerializedName("assignedEmployees")
        @Expose
        var assignedEmployees : AssignedEmployees? = null
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
        @SerializedName("discountPercent")
        @Expose
        var discountPercent : String? = null
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
        @SerializedName("notifyRecipient")
        @Expose
        var notifyRecipient : String? = null
        @SerializedName("notifyMe")
        @Expose
        var notifyMe : String? = null
        @SerializedName("deliveryCharges")
        @Expose
        var deliveryCharges : String? = null
        @SerializedName("cancellationCharges")
        @Expose
        var cancellationCharges : String? = null
        @SerializedName("cancelOrderId")
        @Expose
        var cancelOrderId : String? = null
        @SerializedName("pendingCCharges")
        @Expose
        var pendingCCharges : String? = null
        @SerializedName("usedLPoints")
        @Expose
        var usedLPoints : String? = null
        @SerializedName("lPointsPrice")
        @Expose
        var lPointsPrice : String? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt : String? = null

    }

    class OrderStatus : Serializable {
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

    class PayViaNew : Serializable {
        @SerializedName("type")
        @Expose
        var type : String? = null
        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber : String? = null
    }

    inner class CompletedOrder {
        @SerializedName("empId")
        @Expose
        var empId : String? = null
        @SerializedName("orderId")
        @Expose
        var orderId : String? = null
        @SerializedName("firstName")
        @Expose
        var firstName : String? = null
        @SerializedName("lastName")
        @Expose
        var lastName : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
    }

    class PickupAddress : Serializable {
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

    class VehicleData : Serializable {
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

    class WeightData : Serializable {
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

    class DeliveryOptionData : Serializable {
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

    class BannersData : Serializable {
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

    class AssignedEmployees : Serializable {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("firstName")
        @Expose
        var firstName : String? = null
        @SerializedName("lastName")
        @Expose
        var lastName : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber : String? = null
        @SerializedName("countryCode")
        @Expose
        var countryCode : String? = null
        @SerializedName("rating")
        @Expose
        var rating : String? = null
        @SerializedName("payVia")
        @Expose
        var payVia : ArrayList<String>? = null
        @SerializedName("payViaNew")
        @Expose
        var payViaNew : ArrayList<PayViaNew>? = null
    }

}
