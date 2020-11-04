package com.courierdriver.model.order

import com.google.gson.annotations.SerializedName

class OrderListModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: ArrayList<Body>? = null

    class Body {

        @SerializedName("pickupAddress")
        val pickupAddress: PickupAddress? = null

        @SerializedName("deliveryAddress")
        val deliveryAddress: ArrayList<DeliveryAddress>? = null

        @SerializedName("orderNo")
        val orderNo: String? = null

        @SerializedName("distance")
        val distance: Double? = null

        @SerializedName("id")
        val id: String? = null

        @SerializedName("itemName")
        val itemName: String? = null

        @SerializedName("createdAt")
        val createdAt: String? = null

        @SerializedName("parcelvalue")
        val parcelvalue: String? = null

        @SerializedName("totalOrderPrice")
        val totalOrderPrice: String? = null

        @SerializedName("vehicle")
        val vehicle: Vehicle? = null

        @SerializedName("weight")
        val weight: Weight? = null

        @SerializedName("deliveryoption")
        val deliveryoption: Deliveryoption? = null

        class Deliveryoption {

            @SerializedName("title")
            val title: String? = null
        }

        class Weight {

            @SerializedName("name")
            val name: String? = null
        }

        class Vehicle {

            @SerializedName("name")
            val name: String? = null
        }

        class DeliveryAddress {

            @SerializedName("address")
            val address: String? = null

            @SerializedName("date")
            val date: String? = null

            @SerializedName("id")
            val id: Int? = null

            @SerializedName("lat")
            val lat: Double? = null

            @SerializedName("long")
            val long: Double? = null

            @SerializedName("phoneNumber")
            val phoneNumber: String? = null

            @SerializedName("time")
            val time: String? = null
        }

        class PickupAddress {

            @SerializedName("address")
            val address: String? = null

            @SerializedName("date")
            val date: String? = null

            @SerializedName("id")
            val id: Int? = null

            @SerializedName("lat")
            val lat: Double? = null

            @SerializedName("long")
            val long: Double? = null

            @SerializedName("phoneNumber")
            val phoneNumber: String? = null

            @SerializedName("time")
            val time: String? = null
        }
    }
}