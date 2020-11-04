package com.android.courier.model.notifications

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class NotificationsResponse {
    @SerializedName("body")
    @Expose
    var data : ArrayList<Data>? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

}

class Data {
    @SerializedName("id")
    @Expose
    var id : String? = null
    @SerializedName("notificationTitle")
    @Expose
    var notificationTitle : String? = null
    @SerializedName("notificationDescription")
    @Expose
    var notificationDescription : String? = null
    @SerializedName("userId")
    @Expose
    var userId : String? = null
    @SerializedName("orderId")
    @Expose
    var orderId : String? = null
    @SerializedName("role")
    @Expose
    var role : String? = null
    @SerializedName("readStatus")
    @Expose
    var readStatus : String? = null
    @SerializedName("status")
    @Expose
    var status : String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt : String? = null
    @SerializedName("updatedAt")
    @Expose
    var updatedAt : String? = null

}
