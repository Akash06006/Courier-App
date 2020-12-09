package com.courierdriver.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class NotificationListModel {
    @SerializedName("code")
    val code: Int? = null
    @SerializedName("message")
    val message: String? = null
    @SerializedName("body")
    val body: List<Body>? = null

    class Body : Serializable {
        @SerializedName("id")
        val id: String? = null

        @SerializedName("notificationTitle")
        val notificationTitle: String? = null

        @SerializedName("notificationType")
        val notificationType: String? = null

        @SerializedName("senderId")
        val senderId: String? = null

        @SerializedName("notificationDescription")
        val notificationDescription: String? = null

        @SerializedName("userId")
        val userId: String? = null

        @SerializedName("orderId")
        val orderId: String? = null

        @SerializedName("role")
        val role: Int? = null

        @SerializedName("readStatus")
        val readStatus: Int? = null

        @SerializedName("status")
        val status: Int? = null

        @SerializedName("createdAt")
        val createdAt: String? = null

        @SerializedName("updatedAt")
        val updatedAt: String? = null
    }
}