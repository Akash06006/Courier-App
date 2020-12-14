package com.android.courier.model

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

        @SerializedName("postedUserId")
        @Expose
        private val postedUserId: String? = null

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


