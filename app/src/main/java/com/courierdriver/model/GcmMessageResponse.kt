package com.courierdriver.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by psingh5 on 7/22/2016.
 */

class GcmMessageResponse {
    @SerializedName("notificationResponse")
    @Expose
    var notificationResponse: NotificationResponse? = null

    class NotificationResponse {
        @SerializedName("message")
        @Expose
        var message: String? = null
        @SerializedName("requestType")
        @Expose
        var requestType: String? = null
        @SerializedName("likedUserId")
        @Expose
        private val likedUserId: String? = null
        @SerializedName("postedUserId")
        @Expose
        private val postedUserId: String? = null
        @SerializedName("contract number")
        @Expose
        private val contractNumber: String? = null
        @SerializedName("transporterId")
        @Expose
        private val transporterId: String? = null
        @SerializedName("goodsOwnerId")
        @Expose
        private val goodsOwnerId: String? = null
        @SerializedName("senderName")
        @Expose
        val senderName: String? = null

        @SerializedName("receiverName")
        @Expose
        val receiverName: String? = null

        @SerializedName("senderId")
        @Expose
        val senderId: String? = null

        @SerializedName("receiverId")
        @Expose
        val receiverId: String? = null

        @SerializedName("type")
        @Expose
        val contractType: String? = null

        @SerializedName("rideId")
        @Expose
        val rideId: Int? = null

    }
}


