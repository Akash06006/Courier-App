package com.example.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class ApplyCouponResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

}

class Data {
    @SerializedName("totalAmount")
    @Expose
    var totalAmount : String? = null
    @SerializedName("discountPrice")
    @Expose
    var discountPrice : String? = null
    @SerializedName("payableAmount")
    @Expose
    var payableAmount : String? = null
    @SerializedName("coupanId")
    @Expose
    var coupanId : String? = null
    @SerializedName("coupanCode")
    @Expose
    var coupanCode : String? = null
    @SerializedName("coupanDiscount")
    @Expose
    var coupanDiscount : String? = null
}
