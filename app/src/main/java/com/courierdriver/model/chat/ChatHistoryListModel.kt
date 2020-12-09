package com.courierdriver.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Array

class ChatHistoryListModel {


    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("body")
    @Expose
    var body: Data? = null


    class Data {

        @SerializedName("users")
        @Expose
        var users: ArrayList<Users>? = null
    }

    class Users {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("senderId")
        @Expose
        var senderId: String? = null

        @SerializedName("driverId")
        @Expose
        var driverId: String? = null

        @SerializedName("groupId")
        @Expose
        var groupId: String? = null

        @SerializedName("actualMessageId")
        @Expose
        var actualMessageId: String? = null

        @SerializedName("messageType")
        @Expose
        var messageType: String? = null

        @SerializedName("type")
        @Expose
        var type: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("sentAt")
        @Expose
        var sentAt: String? = null
        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("media")
        @Expose
        var media: String? = null
        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null
        @SerializedName("senderName")
        @Expose
        var senderName: String? = null

        @SerializedName("senderImage")
        @Expose
        var senderImage: String? = null
        @SerializedName("orderId")
        @Expose
        var orderId: String? = null
        @SerializedName("groupMember")
        @Expose
        var groupMember: String? = null
        @SerializedName("unread")
        @Expose
        var unread: String? = null

    }


}
