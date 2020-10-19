package com.example.courier.model.loyalty

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class LoyaltyResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

}

class Data {
    @SerializedName("id")
    @Expose
    var id : String? = null
    @SerializedName("totalPoints")
    @Expose
    var totalPoints : String? = null
    @SerializedName("user")
    @Expose
    var users : ArrayList<User>? = null

}

class User {
    @SerializedName("name")
    @Expose
    var name : String? = null
    @SerializedName("date")
    @Expose
    var date : String? = null
    @SerializedName("type")
    @Expose
    var type : String? = null
    @SerializedName("points")
    @Expose
    var points : String? = null
    @SerializedName("image")
    @Expose
    var image : String? = null

}
/*
class User {
    @SerializedName("image")
    @Expose
    var image : String? = null
    @SerializedName("id")
    @Expose
    var id : String? = null
    @SerializedName("firstName")
    @Expose
    var firstName : String? = null
    @SerializedName("lastName")
    @Expose
    var lastName : String? = null
}*/
