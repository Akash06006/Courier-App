package com.android.courier.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VehicleModel {
    @SerializedName("id")
    @Expose
    var id : Int = 0
    @SerializedName("name")
    @Expose
    var name : String? = null
    @SerializedName("image")
    @Expose
    var image : String? = null

}


