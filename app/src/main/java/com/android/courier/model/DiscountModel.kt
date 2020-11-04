package com.android.courier.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DiscountModel {
    @SerializedName("discount")
    @Expose
    var discount : Int = 0

}


