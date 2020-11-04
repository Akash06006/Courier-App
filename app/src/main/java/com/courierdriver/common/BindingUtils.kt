package com.courierdriver.common

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import com.courierdriver.application.MyApplication
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.model.LoginResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

object BindingUtils {
    // @SuppressLint("SimpleDateFormat")
//    @JvmStatic
//    fun getDate(responseDate: String): String {
//        var startDateTime: String
//        startDateTime = try {
//            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US) as DateFormat
//            originalFormat.timeZone = TimeZone.getTimeZone("UTC")
//            val targetFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a") as DateFormat
//
//            val date = originalFormat.parse(responseDate) as Date
//            targetFormat.format(date)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "00-00-0000 00:00 AM"
//        }
//
//        return startDateTime.split(" ")[0]
//
//    }
    @JvmStatic
    fun shortText(s: String, size: Int): String {
        var returnText = s
        if (s.length > size) {
            returnText = s.substring(0, size) + "..."
        }

        return returnText
    }

    @JvmStatic
    fun boldText(s: String): Spanned {
        val sourceString = "<b>$s</b>"
        return Html.fromHtml(sourceString)
    }


    @JvmStatic
    fun getUserName(): String {
        var name = ""
        if (UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.USERNAME
                )
            )
        ) {
            name = SharedPrefClass().getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERNAME
            ).toString()
        } else {
            name = "Karan!"
        }

        return name

    }


    @JvmStatic
    fun getSavedData(type: String): String {
        var data = ""
        val gson = Gson()

        var dataSaved: LoginResponse.Data? = null

        if (UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.USERDATA
                )
            )
        ) {

            dataSaved = gson.fromJson(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.USERDATA
                ) as String, LoginResponse.Data::class.java
            )


        }


        when (type) {
            "guest_pass" -> {
                if (UtilsFunctions.checkObjectNull(
                        SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            GlobalConstants.GUEST_PASS_COUNT
                        )
                    )
                ) {
                    data = SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.GUEST_PASS_COUNT
                    ).toString()
                }
            }


            "email" -> {
                if (dataSaved != null) {
                    data = dataSaved.email.toString()
                }
            }


        }


        return data

    }


    @JvmStatic
    fun getSharedData(type: String): String {
        var returnString = ""
        when (type) {
            "yearlyfee" -> {
                returnString = if (UtilsFunctions.checkObjectNull(
                        SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            GlobalConstants.YEARLYFEE
                        )
                    )
                ) {
                    SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.YEARLYFEE
                    ).toString()
                } else "$0"

            }
            "monthlyfee" -> {
                returnString = if (UtilsFunctions.checkObjectNull(
                        SharedPrefClass().getPrefValue(
                            MyApplication.instance.applicationContext,
                            GlobalConstants.MONTHLYFEE
                        )
                    )
                ) {
                    SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.MONTHLYFEE
                    ).toString()
                } else "$0"

            }
        }


        return returnString

    }

    @JvmStatic
    fun getUserLocation(): String {
        var name = ""
        if (UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.USER_LOCATION
                )
            )
        ) {
            name = SharedPrefClass().getPrefValue(
                MyApplication.instance,
                GlobalConstants.USER_LOCATION
            ).toString()
        } else {
            name = "Default"
        }

        return name

    }

    @JvmStatic
    fun getUserFees(): String {
        var name = ""
        if (UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.USER_REG_FEE
                )
            )
        ) {
            name = SharedPrefClass().getPrefValue(
                MyApplication.instance,
                GlobalConstants.USER_REG_FEE
            ).toString()
        } else {
            name = "$0"
        }

        return name

    }

    @JvmStatic
    fun getProfileImage(): String {
        var image = ""
        if (UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.CUSTOMER_IAMGE
                )
            )
        ) {
            image = SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.CUSTOMER_IAMGE
            ).toString()

        }

        image = GlobalConstants.BASE_SERVER + image


        return image

    }

    @JvmStatic
    fun getImageURL(image: String?): String {
        var imagetoSend = image
        imagetoSend = GlobalConstants.BASE_SERVER + imagetoSend
        return imagetoSend

    }

    @JvmStatic
    fun getHtml(desc: String?): Spanned {
        //  var newDesc = desc!!.replace("<div>", "");
        //  newDesc = newDesc.replace("</div>", "\n");
        if (desc != null) {
            return if (Build.VERSION.SDK_INT >= 24) {
                Html.fromHtml(desc.trim(), Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(desc.trim())
            }
        }
        return Html.fromHtml("")
    }

    @JvmStatic
    fun convertDateIntoDayMonthDate(date: String?, format: String): String {
        // Display a date in day, month, year format
        var today = ""
        if (!TextUtils.isEmpty(date)) {
            val datee = Date.parse(date)

            when (format) {
                "DayMonthDate" -> {
                    val formatter = SimpleDateFormat("EEEE, MMMM dd")
                    today = formatter.format(datee)
                }

                "Day" -> {
                    val formatter = SimpleDateFormat("EEEE")
                    today = formatter.format(datee)
                }
                "Date" -> {
                    val formatter = SimpleDateFormat("dd")
                    today = formatter.format(datee)
                }
                "Month" -> {
                    val formatter = SimpleDateFormat("MMM")
                    today = formatter.format(datee)
                }
                "MonthDate" -> {
                    val formatter = SimpleDateFormat("MMM dd")
                    today = formatter.format(datee)
                }
            }
        }
        return today
    }

    @JvmStatic
    fun splitTime(serviceTime: String?, format: String): String {
        var time = ""
        if (!TextUtils.isEmpty(serviceTime)) {
            val timeParts = serviceTime!!.split(" ")

            if (format == "Time")
                time = timeParts[0]
            else if (format == "AM_PM")
                time = timeParts[1]
        }
        return time.toLowerCase()
    }

}