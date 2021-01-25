package com.android.courier.model.order

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import java.io.Serializable

class ListsResponse {
    @SerializedName("body")
    @Expose
    var data : Data? = null
    @SerializedName("message")
    var message : String? = null
    var code : Int? = null

    class Data {
        @SerializedName("vehicleData")
        @Expose
        var vehicleData : ArrayList<VehicleData>? = null
        @SerializedName("weightData")
        @Expose
        var weightData : ArrayList<WeightData>? = null
        @SerializedName("deliveryOptionData")
        @Expose
        var deliveryOptionData : ArrayList<DeliveryOptionData>? = null
        @SerializedName("bannersData")
        @Expose
        var bannersData : ArrayList<BannersData>? = null
        @SerializedName("completedorder")
        @Expose
        var completedorder : CompletedOrder? = null
        @SerializedName("referredToPoint")
        @Expose
        var referredToPoint : String? = null
        @SerializedName("referredByPoint")
        @Expose
        var referredByPoint : String? = null
        @SerializedName("adminNumber")
        @Expose
        var adminNumber : String? = null
        @SerializedName("androidLink")
        @Expose
        var androidLink : String? = null
        @SerializedName("iosLink")
        @Expose
        var iosLink : String? = null

    }/**/

    inner class CompletedOrder : Serializable {
        @SerializedName("empId")
        @Expose
        var empId : String? = null
        @SerializedName("orderId")
        @Expose
        var orderId : String? = null
        @SerializedName("firstName")
        @Expose
        var firstName : String? = null
        @SerializedName("lastName")
        @Expose
        var lastName : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("companyId")
        @Expose
        var companyId : String? = null
    }

    class VehicleData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("image")
        @Expose
        var image : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null

    }

    class WeightData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null
    }

    class DeliveryOptionData {
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("title")
        @Expose
        var title : String? = null
        @SerializedName("price")
        @Expose
        var price : String? = null
        @SerializedName("selected")
        @Expose
        var selected : String? = null

    }

    class BannersData {
        @SerializedName("url")
        @Expose
        var url : String? = null
        @SerializedName("icon")
        @Expose
        var icon : String? = null
        @SerializedName("id")
        @Expose
        var id : String? = null
        @SerializedName("name")
        @Expose
        var name : String? = null
        @SerializedName("code")
        @Expose
        var code : String? = null
        @SerializedName("description")
        @Expose
        var description : String? = null
        @SerializedName("discount")
        @Expose
        var discount : String? = null
        @SerializedName("type")
        @Expose
        var type : String? = null

    }

}
