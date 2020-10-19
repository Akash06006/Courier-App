package com.example.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class CalculatePriceResponse {
    @SerializedName("body")
    @Expose
    var data : Body? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

}

class Body {
    @SerializedName("afterDiscount")
    @Expose
    var afterDiscount : String? = null
    @SerializedName("beforeDiscount")
    @Expose
    var beforeDiscount : String? = null
    @SerializedName("cancelOrder")
    @Expose
    var cancelOrder : CancelOrder? = null
    @SerializedName("loyalityData")
    @Expose
    var loyalityData : LoyalityData? = null

}

class LoyalityData {
    @SerializedName("totalPoints")
    @Expose
    var totalPoints : String? = null
    @SerializedName("pricePerPoint")
    @Expose
    var pricePerPoint : String? = null
    @SerializedName("usabelPoints")
    @Expose
    var usabelPoints : String? = null
}

class CancelOrder {
    @SerializedName("totalCancelPrice")
    @Expose
    var totalCancelPrice : String? = null
    @SerializedName("orders")
    @Expose
    var orders : ArrayList<Orders>? = null
}

class Orders {
    @SerializedName("id")
    @Expose
    var orderId : String? = null
    @SerializedName("orderNo")
    @Expose
    var orderNo : String? = null
    @SerializedName("cancelCharges")
    @Expose
    var cancelCharges : String? = null
    @SerializedName("itemName")
    @Expose
    var itemName : String? = null
    @SerializedName("totalOrderPrice")
    @Expose
    var totalOrderPrice : String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt : String? = null

}