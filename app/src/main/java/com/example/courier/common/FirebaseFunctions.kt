package com.example.courier.common

import androidx.annotation.NonNull
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.firebaseMobile.OtpFirebaseActivity
import com.example.courier.utils.BaseActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonObject
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*

object FirebaseFunctions {
    private val mPhoneList = ArrayList<String>()
    private var mOtpFirebase = OtpFirebaseActivity()
    private fun checkNumberExist() {
        val mDatabase = FirebaseDatabase.getInstance().reference.child("PhoneNumber")
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot : DataSnapshot) {
                for (data in dataSnapshot.children) {
                    mPhoneList.add(data.key!!)
                }
            }

            override fun onCancelled(@NonNull databaseError : DatabaseError) {
                //Not in use
            }

        })
    }

    @JvmStatic
    fun sendOTP(key : String, mJsonObject : JsonObject, baseActivity : BaseActivity) {
        checkNumberExist()
        val countryCode = mJsonObject.get("countryCode").toString().replace("\"", "")
        val phone = mJsonObject.get("phoneNumber").toString().replace("\"", "")
        val phoneUtil = PhoneNumberUtil.getInstance()
        val isValid : Boolean
        var validNumber = false

        baseActivity.stopProgressDialog()

        try {
            val numberProto =
                phoneUtil.parse("$countryCode$phone", "RU") // Pass number & Country code
            //check whether the number is valid or no.
            isValid = phoneUtil.isValidNumber(numberProto)
            if (isValid) {
                if (mPhoneList.size > 0) {
                    for (i in mPhoneList.indices) {
                        if (mPhoneList[i] == phone) {
                            validNumber = true
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.already_exist))
                        }
                    }
                    if (!validNumber) {
                        mOtpFirebase.otpValidation(
                            key,
                            phone,
                            baseActivity,
                            mJsonObject
                        )
                    }
                } else {
                    mOtpFirebase.otpValidation(
                        key,
                        phone,
                        baseActivity,
                        mJsonObject
                    )
                }
            } else
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.invalid_number))
            println("Invalid Number")
        } catch (e : Exception) {
            UtilsFunctions.showToastError(e.localizedMessage)
            System.err.println("NumberParseException was thrown: $e")
        }

    }

}
