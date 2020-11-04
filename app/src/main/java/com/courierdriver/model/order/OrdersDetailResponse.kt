package com.courierdriver.model.order

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrdersDetailResponse {
    @SerializedName("body")
    @Expose
    var data: Data? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("code")
    @Expose
    var code: Int? = null

    /*deliveryOption -> {JsonPrimitive@8505} ""51b1c365-b88b-4ca4-bf01-4361c0a1ee3c""*/
    /*
    private String[] pickupInstructions;
*/
    /*({
    body =     {
        adminComission = 256;
        createdAt = "19/10/2020 03:26";
        deliveryAddress =         (
                        {
                address = "Old Power House Rd, Jalalabad, Punjab 152023, India";
                date = "19-10-2020";
                id = 1;
                isComplete = 0;
                lat = "30.605209213458416";
                long = "74.25226170569658";
                phoneNumber = 9530606006;
                time = "18:47";
            },
                        {
                address = "BDO office St, Dashmesh Nagri, Jalalabad, Punjab 152023, India";
                date = "19-10-2020";
                id = 2;
                isComplete = 0;
                lat = "30.602991233396505";
                long = "74.25090216100216";
                time = "21:33";
            }
        );
        deliveryoption =         {
            title = "Same day";
        };
        driverCCharges = 50;
        driverEarning = 1024;
        fareCollected = "Pickup end";
        id = "65bd4a1b-9120-49b6-a96d-5af92afa86d8";
        itemName = "new order";
        orderNo = ORDER0033;
        orderStatus =         {
            status = 2;
            statusName = Accepted;
        };
        pickupAddress =         {
            address = "Muktsar Rd, Jalalabad, Punjab 152023, India";
            date = "19-10-2020";
            id = 1;
            lat = "30.604600615866477";
            long = "74.25047468394041";
            phoneNumber = "90564 43279";
            time = "15:47";
        };
        progressStatus = 2;
        totalOrderPrice = 1280;
        vehicle =         {
            name = Bus;
        };
        weight =         {
            name = "1 to 5kg";
        };
    };
    code = 200;
    message = Details;
})*/


    class Data {
        @SerializedName("pickupAddress")
        @Expose
        var pickupAddress: PickupAddress? = null

        @SerializedName("deliveryAddress")
        @Expose
        var deliveryAddress: ArrayList<PickupAddress>? = null

        @SerializedName("isPaid")
        @Expose
        var isPaid: String? = null

        @SerializedName("offerPrice")
        @Expose
        var offerPrice: String? = null

        @SerializedName("pickupLong")
        @Expose
        var pickupLong: String? = null

        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = null

        @SerializedName("tip")
        @Expose
        var tip: String? = null

        @SerializedName("pickupLat")
        @Expose
        var pickupLat: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

        @SerializedName("weight")
        @Expose
        var weight: WeightData? = null

        @SerializedName("vehicle")
        @Expose
        var vehicle: VehicleData? = null

        @SerializedName("deliveryoption")
        @Expose
        var deliveryoption: DeliveryOptionData? = null

        @SerializedName("orderNo")
        @Expose
        var orderNo: String? = null

        @SerializedName("orderStatus")
        @Expose
        var orderStatus: OrderStatus? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("itemName")
        @Expose
        var itemName: String? = null

        @SerializedName("vehicleType")
        @Expose
        var vehicleType: String? = null

        @SerializedName("deliveryOption")
        @Expose
        var deliveryOption: String? = null

        @SerializedName("weightId")
        @Expose
        var weightId: String? = null

        @SerializedName("promoCode")
        @Expose
        var promoCode: String? = null

        @SerializedName("totalOrderPrice")
        @Expose
        var totalOrderPrice: String? = null

        @SerializedName("companyId")
        @Expose
        var companyId: String? = null

        @SerializedName("userId")
        @Expose
        var userId: String? = null

        @SerializedName("progressStatus")
        @Expose
        var progressStatus: String? = null

        @SerializedName("userShow")
        @Expose
        var userShow: String? = null

        @SerializedName("trackStatus")
        @Expose
        var trackStatus: String? = null

        @SerializedName("trackingLatitude")
        @Expose
        var trackingLatitude: String? = null

        @SerializedName("trackingLongitude")
        @Expose
        var trackingLongitude: String? = null

        @SerializedName("cancellationReason")
        @Expose
        var cancellationReason: String? = null

        @SerializedName("paymentType")
        @Expose
        var paymentType: String? = null

        @SerializedName("cancellable")
        @Expose
        var cancellable: String? = null

        @SerializedName("distance")
        @Expose
        var distance: String? = null

        @SerializedName("parcelValue")
        @Expose
        var parcelValue: String? = null

        @SerializedName("orderPrice")
        @Expose
        var orderPrice: String? = null

        @SerializedName("fareCollected")
        @Expose
        var fareCollected: String? = null

        @SerializedName("notifyRecipient")
        @Expose
        var notifyRecipient: String? = null

        @SerializedName("notifyMe")
        @Expose
        var notifyMe: String? = null

        @SerializedName("deliveryCharges")
        @Expose
        var deliveryCharges: String? = null

        @SerializedName("cancelCharges")
        @Expose
        var cancellationCharges: String? = null

        @SerializedName("cancelOrderId")
        @Expose
        var cancelOrderId: String? = null

        @SerializedName("pendingCCharges")
        @Expose
        var pendingCCharges: String? = null

        @SerializedName("usedLPoints")
        @Expose
        var usedLPoints: String? = null

        @SerializedName("lPointsPrice")
        @Expose
        var lPointsPrice: String? = null

    }

    class OrderStatus {
        @SerializedName("statusName")
        @Expose
        var statusName: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("parentStatus")
        @Expose
        var parentStatus: String? = null
    }

    class PickupAddress {
        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("lat")
        @Expose
        var lat: String? = null

        @SerializedName("long")
        @Expose
        var long: String? = null

        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber: String? = null

        @SerializedName("date")
        @Expose
        var date: String? = null

        @SerializedName("time")
        @Expose
        var time: String? = null

    }

    class VehicleData {
        @SerializedName("icon")
        @Expose
        var icon: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("price")
        @Expose
        var price: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

    }

    class WeightData {
        @SerializedName("icon")
        @Expose
        var icon: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("status")
        @Expose
        var status: String? = null

        @SerializedName("price")
        @Expose
        var price: String? = null
    }

    class DeliveryOptionData {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("price")
        @Expose
        var price: String? = null

        @SerializedName("selected")
        @Expose
        var selected: String? = null

    }

    class BannersData {
        @SerializedName("url")
        @Expose
        var url: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

    }

}
