package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class StatisticsModel {
    @SerializedName("code")
    val code: Int? = null

    @SerializedName("message")
    val message: String? = null

    @SerializedName("body")
    val body: Body? = null

    class Body {

        @SerializedName("pointsData") val pointsData : PointsData?=null
        @SerializedName("commissionData") val commissionData : CommissionData?=null
        @SerializedName("all") val all : All?=null
        @SerializedName("week") val week : Week?=null
        @SerializedName("year") val year : ArrayList<Year>?=null

        class CommissionData {

            @SerializedName("totalCommission")
            val totalCommission: String? = null
            @SerializedName("earnedCash")
            val earnedCash: String? = null
            @SerializedName("payableCommission")
            val payableCommission: String? = null
            @SerializedName("usedPoints")
            val usedPoints: String? = null
            @SerializedName("usedCash")
            val usedCash: String? = null
            @SerializedName("additionalCharges")
            val additionalCharges: AdditionalCharges? = null

            class AdditionalCharges {

                /*@SerializedName("total")
                val total: String? = null*/
                @SerializedName("securityFee")
                val securityFee: String? = null

                @SerializedName("cancellationCharges")
                val cancellationCharges: String? = null

                @SerializedName("cancelChargesCust")
                val cancelChargesCust: String? = null
            }
        }

        class PointsData {

            @SerializedName("earnedPoints") val earnedPoints : String?=null
            @SerializedName("invitedFriends") val invitedFriends : String?=null
        }

        class Year {
            @SerializedName("completedOrders") val completedOrders : String?=null
            @SerializedName("avgRating") val avgRating : String?=null
            @SerializedName("myIncome") val myIncome : String?=null
            @SerializedName("penalty") val penalty : String?=null
            @SerializedName("month") val month : String?=null
        }

        class Week {

            @SerializedName("completedOrders") val completedOrders : String?=null
            @SerializedName("avgRating") val avgRating : String?=null
            @SerializedName("myIncome") val myIncome : String?=null
            @SerializedName("penalty") val penalty : String?=null
        }

        class All {
            @SerializedName("completedOrders")
            val completedOrders: String? = null

            @SerializedName("avgRating")
            val avgRating: String? = null

            @SerializedName("myIncome")
            val myIncome: String? = null
        }
    }
}