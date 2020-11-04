package com.android.courier.model.chat

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChatListModel {
    @SerializedName("id")
    @Expose
    var id : String? = null
    @SerializedName("chatType")
    @Expose
    var chatType : String? = null
    @SerializedName("senderId")
    @Expose
    var senderId : String? = null
    @SerializedName("groupId")
    @Expose
    var groupId : String? = null
    @SerializedName("actualMessageId")
    @Expose
    var actualMessageId : String? = null
    @SerializedName("messageType")
    @Expose
    var messageType : Int? = null
    @SerializedName("type")
    @Expose
    var type : Int? = null
    @SerializedName("status")
    @Expose
    var status : Int? = null
    @SerializedName("sentAt")
    @Expose
    var sentAt : Int? = null
    @SerializedName("message")
    @Expose
    var message : String? = null
    @SerializedName("media")
    @Expose
    var media : String? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail : String? = null
    @SerializedName("senderName")
    @Expose
    var senderName : String? = null
    @SerializedName("senderImage")
    @Expose
    var senderImage : String? = null

}


