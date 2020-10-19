package com.example.courier.model
import androidx.databinding.BaseObservable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class ProfileModel : BaseObservable() {
    @SerializedName("data")
    @Expose
    var data:Data?=null
    @SerializedName("code")
    @Expose
    var code:Int?=null
    @SerializedName("message")
    @Expose
    var message:String?=null
    inner class Data : BaseObservable()  {
        @SerializedName("first_name")
        @Expose
        var firstName:String?=null
        @SerializedName("last_name")
        @Expose
        var lastName:String?=null
        @SerializedName("image")
        @Expose
        var image:String?=null
        @SerializedName("email")
        @Expose
        var email:String?=null

        @SerializedName("phone_no")
        @Expose
        var phoneNo:String?=null

        @SerializedName("dob")
        @Expose
        var dob:String?=null

        @SerializedName("gender")
        @Expose
        var gender:String?=null

        @SerializedName("user_description")
        @Expose
        var userDescription:String?=null

        @SerializedName("user_setting")
        @Expose
        var userSetting:ArrayList<UserSettings>?=null


        @SerializedName("customer_social_media_link")
        @Expose
        var customerSocialMediaLink:ArrayList<SocialLink>?=null


    }

    inner class SocialLink : BaseObservable()  {
        @SerializedName("url")
        @Expose
        var url:String?=null

    }








    inner class UserSettings : BaseObservable()  {
        @SerializedName("booking_alert")
        @Expose
        var bookingAlert:Boolean?=null
        @SerializedName("other_alert")
        @Expose
        var otherAlert:Boolean?=null
        @SerializedName("auto_checkin")
        @Expose
        var autoCheckin:Boolean?=null
        @SerializedName("auto_redeem_coupon")
        @Expose
        var autoRedeemCoupan:Boolean?=null
    }







}