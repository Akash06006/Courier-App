package com.courierdriver.constants

import com.courierdriver.application.MyApplication
import java.io.File

/*
 * Created by admin on 30-01-2018.
 */

object GlobalConstants {
    val file = File(MyApplication.instance.filesDir, "Images")

    @JvmStatic
    val SHARED_PREF = "DEMOO_APP"

    @JvmStatic
    val SEND_DATA = "DEMO_APP"

    @JvmStatic
    val ACCESS_TOKEN = "access_token"

    @JvmStatic
    var IS_TUTORIAL = "false"

    @JvmStatic
    val CUSTOMER_IAMGE = "customer_image"

    @JvmStatic
    val REGION_ID = "region_id"

    @JvmStatic
    val AVAILABLE = "AVAILABLE"

    @JvmStatic
    val PHONE_NUMBER = "phone_number"

    @JvmStatic
    val EMAIL = "email"

    @JvmStatic
    val USERNAME = "username"

    @JvmStatic
    val USEREMAIL = "useremail"

    @JvmStatic
    val OUTLETEMAIL = "outletuseremail"

    @JvmStatic
    val USERDATA = "USERDATA"

    @JvmStatic
    val USERID = "USERID"

    @JvmStatic
    val USER_ID = "USER_ID"

    @JvmStatic
    val USER_IMAGE = "USERID"

    @JvmStatic
    val IS_SOCIAL = "USERID"

    @JvmStatic
    val USER_LOCATION = "USERLOCATION"

    @JvmStatic
    val IS_DOC_UPLOADED = "isDocUploaded"

    @JvmStatic
    var NOTIFICATION_TOKEN = "notification_token"

    @JvmStatic
    var NOTIFICATION_TOKENPref = "notification_token"

    @JvmStatic
    var SESSION_TOKEN = "session_token"

    @JvmStatic //http://stgcerb.cerebruminfotech.com:9067/
    val BASE_URL = "http://stgcerb.cerebruminfotech.com:9067/api/"

    @JvmStatic
    val BASE_SERVER = "http ://stgcerb.cerebruminfotech.com:9067"
    const val PLATFORM = "android"

    @JvmStatic
    var LOGIN = "every"

    @JvmStatic
    val COMPANY = "Company"

    @JvmStatic
    val OUTLET = "Outlet"

    @JvmStatic
    val USER_REG_FEE = "USER_REG_FEE"

    @JvmStatic
    val YEARLYFEE = "YEARLY_FEE"

    @JvmStatic
    val MONTHLYFEE = "MONTHLYFEE"

    @JvmStatic
    val UPCOMING = "UPCOMING"

    @JvmStatic
    var selectedFragment = 2

    @JvmStatic
    var selectedCheckedFragment = 2

    @JvmStatic
    var GUEST_PASS_COUNT = "0"

    @JvmStatic
    var OTP_VERIFICATION_ID = "OTP_VERIFICATION_ID"

    @JvmStatic
    var VERIFICATION_TYPE = "VERIFICATION_TYPE"

    @JvmStatic
    var PROFILE_DETAILS = "1"

    @JvmStatic
    var STATISTICS = "2"

    @JvmStatic
    var ACCOUNT = "3"

    @JvmStatic
    var DOCUMENTS_TAB = "4"

    @JvmStatic
    var CURRENCY_SIGN = "₹"


    @JvmStatic
    var MONEY_HASH = "https://debajyotibasak.000webhostapp.com/PayUMoneyHash.php"

    @JvmStatic
    var SURL =
        "https://www.payumoney.com/mobileapp/payumoney/success.php"/*"https://www.payumoney.com/mobileapp/payumoney/success.php"*/

    @JvmStatic
    var FURL =
        "https://www.payumoney.com/mobileapp/payumoney/failure.php"/*"https://www.payumoney.com/mobileapp/payumoney/failure.php"*/

    @JvmStatic
    var MERCHANT_KEY = "2n0gOgRt"

    @JvmStatic
    var MERCHANT_ID = "7305291"

    @JvmStatic
    var SALT = "hyi6zkm3XS"

    @JvmStatic
    var FIRST_NAME = "Navjeet"

    @JvmStatic
    var MOBILE = "7986607564"

    @JvmStatic
    var PAYUMONEY_EMAIL = "saininavjeet@seasia.in"

    @JvmStatic
    var DEBUG = true

    @JvmStatic
//    val SOCKET_URL = "http://51.79.40.224:9062"   // lIVE
    val SOCKET_URL = "http://stgcerb.cerebruminfotech.com:9067/"   // TESTING

    @JvmStatic
    val SOCKET_CHAT_URL = "http://stgcerb.cerebruminfotech.com:9067/"

    @JvmStatic
    var ROOM_ID = "roomID"

    @JvmStatic
    var ADMIN_ID = "25cbf58b-46ba-4ba2-b25d-8f8f653e9f11"

    @JvmStatic
    val TERMS_AND_CONDITIONS = "https://www.cerebruminfotech.com/"

    @JvmStatic
    val Aadhar_front = 1

    @JvmStatic
    val Aadhar_back = 2

    @JvmStatic
    val Pan_card = 3

    @JvmStatic
    val Driving_license_front = 4

    @JvmStatic
    val Driving_license_back = 5
}
