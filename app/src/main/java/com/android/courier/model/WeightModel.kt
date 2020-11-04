package com.android.courier.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressModel {
    @SerializedName("lat")
    @Expose
    var lat : String? = null
    @SerializedName("lng")
    @Expose
    var lng : String? = null
    @SerializedName("address")
    @Expose
    var address : String? = null
    @SerializedName("date")
    @Expose
    var date : String? = null
    @SerializedName("time")
    @Expose
    var time : String? = null
    @SerializedName("phone_number")
    @Expose
    var phone_number : String? = null
    var id : Int=-1

}


