package com.courierdriver.model

import com.google.gson.annotations.SerializedName

class AddDocModel
{
    @SerializedName("code")
     val code:Int?=null
    @SerializedName("message")
     val message:String?=null
    @SerializedName("body")
     val body:Body?=null

    class Body
    {
        @SerializedName("image")
        val image : String?=null
    }
}