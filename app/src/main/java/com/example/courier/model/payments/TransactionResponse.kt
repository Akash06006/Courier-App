package com.example.courier.model.payments

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class TransactionResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

}

class Data {
    @SerializedName("transactions")
    @Expose
    var transactions : ArrayList<Transactions>? = null

}

class Transactions {
    @SerializedName("id")
    @Expose
    var id : String? = null
    @SerializedName("transactionId")
    @Expose
    var transactionId : String? = null
    @SerializedName("paymentMode")
    @Expose
    var paymentMode : String? = null
    @SerializedName("paymentType")
    @Expose
    var paymentType : String? = null
    @SerializedName("amount")
    @Expose
    var amount : String? = null
    @SerializedName("transactionStatus")
    @Expose
    var transactionStatus : String? = null
    @SerializedName("order")
    @Expose
    var order : Order? = null
    @SerializedName("orderNo")
    @Expose
    var orderNo : String? = null
    @SerializedName("itemName")
    @Expose
    var itemName : String? = null
    @SerializedName("totalOrderPrice")
    @Expose
    var totalOrderPrice : String? = null
    @SerializedName("offerPrice")
    @Expose
    var offerPrice : String? = null
    @SerializedName("deliveryCharges")
    @Expose
    var deliveryCharges : String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt : String? = null
    @SerializedName("orderPrice")
    @Expose
    var orderPrice : String? = null

}

class Order {
    @SerializedName("orderNo")
    @Expose
    var orderNo : String? = null
    @SerializedName("itemName")
    @Expose
    var itemName : String? = null
    @SerializedName("totalOrderPrice")
    @Expose
    var totalOrderPrice : String? = null
    @SerializedName("offerPrice")
    @Expose
    var offerPrice : String? = null
    @SerializedName("deliveryCharges")
    @Expose
    var deliveryCharges : String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt : String? = null

}