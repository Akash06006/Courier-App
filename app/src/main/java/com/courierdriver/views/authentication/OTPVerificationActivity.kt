package com.courierdriver.views.authentication

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.common.FirebaseFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityOtpVerificationBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.broadcastReceiver.OTP_Receiver
import com.courierdriver.viewmodels.LoginViewModel
import com.courierdriver.viewmodels.OTPVerificationModel
import com.courierdriver.views.home.DefineWorkActivity
import com.courierdriver.views.home.LandingActivty
import com.goodiebag.pinview.Pinview.PinViewEventListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import org.json.JSONException


class OTPVerificationActivity : BaseActivity() {
    private lateinit var otpVerificationModel: OTPVerificationModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var activityOtpVerificationBinding: ActivityOtpVerificationBinding
    private var mVerificationId: String? = null
    private var mAuth = FirebaseAuth.getInstance()
    private var mJsonObject = JsonObject()
    var userId = ""
    var token = ""
    var number = ""
    var countryCode = ""
    var isDocUploaded = ""
    var isTimeRunning = false

    override fun getLayoutId(): Int {
        return R.layout.activity_otp_verification
    }

    @SuppressLint("SetTextI18n")
    public override fun initViews() {
        activityOtpVerificationBinding = viewDataBinding as ActivityOtpVerificationBinding
        otpVerificationModel = ViewModelProviders.of(this).get(OTPVerificationModel::class.java)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activityOtpVerificationBinding.verifViewModel = otpVerificationModel

        activityOtpVerificationBinding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.otp_verify)

        countDown()
        sharedPrefValue()


        getVerifyUserObserver()
        loaderObserver()
        viewClicks()
        requestsmspermission()
        getIntentData()
        OTP_Receiver().setEditText(activityOtpVerificationBinding.pinview)
        /* activityOtpVerificationBinding.pinview.setPinViewEventListener(object :
             Pinview.PinViewEventListener() {
             fun onDataEntered(pinview: Pinview, fromUser: Boolean) {
                 //Make api calls here or what not
                 Toast.makeText(this@MainActivity, pinview.getValue(), Toast.LENGTH_SHORT).show()
             }
         })*/


    }

    private fun requestsmspermission() {
        val smspermission: String = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, smspermission)
        //check if read SMS permission is granted or not
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = smspermission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        }
    }

    private fun viewClicks() {
        otpVerificationModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                val otp = activityOtpVerificationBinding.pinview.value.toString()
                when (it) {
                    /*"btn_send" -> {
                        val intent = Intent(this, LandingActivty::class.java)
                        startActivity(intent)
                    }*/
                    "btn_send" -> {
                        when {
                            TextUtils.isEmpty(otp) -> {
                                showToastError(getString(R.string.empty) + " " + getString(R.string.OTP))
                            }
                            otp.length < 6 -> {
                                showToastError(
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.OTP
                                    )
                                )
                            }
                            else -> {
                                try {
                                    mVerificationId = SharedPrefClass().getPrefValue(
                                        MyApplication.instance.applicationContext,
                                        GlobalConstants.OTP_VERIFICATION_ID
                                    ).toString()

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                startProgressDialog()
                                verifyVerificationCode(otp)
                            }
                        }
                    }
                    "tv_resend" -> {
                        if (!isTimeRunning) {
                            val mJsonObject1 = JsonObject()
                            mJsonObject1.addProperty(
                                "countryCode",
                                countryCode
                            )
                            mJsonObject1.addProperty(
                                "phoneNumber",
                                number
                            )
                            FirebaseFunctions.sendOTP("resend", mJsonObject1, this)
                            countDown()
                        }
                    }
                }

            })
        )
    }

    private fun loaderObserver() {
        otpVerificationModel.loading.observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    private fun getVerifyUserObserver() {
        loginViewModel.getVerifyUserRes().observe(this,
            Observer<CommonModel> { loginResponse ->
                stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "isLogin",
                            true
                        )

                        showToastSuccess(message)
                        if (isDocUploaded == "true") {
                            val intent = Intent(this, LandingActivty::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            if (GlobalConstants.VERIFICATION_TYPE == "signup") {
                                val intent = Intent(this, DefineWorkActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this, DocumentVerificatonActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                        }

                    } else {
                        //showToastError(message)
                    }

                }
            })
    }

    private fun getIntentData() {
        try {
            /*number = intent.extras?.get("phoneNumber").toString()
            countryCode = intent.extras?.get("countryCode").toString()
            mJsonObject.addProperty("phoneNumber", number)
            mJsonObject.addProperty("countryCode", countryCode)
            number = number.replace("\"", "")
            countryCode = countryCode.replace("\"", "")*/

            number = intent.getStringExtra("phoneNumber")
            countryCode = intent.getStringExtra("countryCode")

            mJsonObject = JsonObject()

            mJsonObject.addProperty(
                "countryCode",
                "+" + countryCode
            )
            mJsonObject.addProperty(
                "phoneNumber",
                number
            )

            GlobalConstants.VERIFICATION_TYPE = "signup"
            FirebaseFunctions.sendOTP("login", mJsonObject, this)

            val msg = activityOtpVerificationBinding.tvOtpSent.text.toString()
            activityOtpVerificationBinding.tvOtpSent.text = "$msg $number"

        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    private fun sharedPrefValue() {
        userId = SharedPrefClass().getPrefValue(this, GlobalConstants.USERID).toString()
        token = SharedPrefClass().getPrefValue(this, GlobalConstants.ACCESS_TOKEN).toString()
        isDocUploaded =
            SharedPrefClass().getPrefValue(this, GlobalConstants.IS_DOC_UPLOADED).toString()
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            //Getting the code sent by SMS
            stopProgressDialog()
            val code = phoneAuthCredential.smsCode
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                // verifying the code

            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onVerificationFailed(e: FirebaseException) {
            if ((e as FirebaseAuthException).errorCode == "ERROR_INVALID_PHONE_NUMBER")
                showToastError(
                    e.message.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0]
                )
            else
                showToastError(getString(R.string.server_not_reached))
        }

    }

    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                stopProgressDialog()
                if (task.isSuccessful) {
                    showToastSuccess("OTP Verified")
                    if (GlobalConstants.VERIFICATION_TYPE == "signup") {
                        callVerifyUserApi()
                    } else {
                        val intent = Intent(this, ResetPasswrodActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    /*  var dob = SharedPrefClass().getPrefValue(this, "dob").toString()
                                  var intent: Intent? = null
                                  if (TextUtils.isEmpty(dob) || dob.equals("null")) {
                                      intent = Intent(this, DatesActivity::class.java)
                                  } else {
                                      intent = Intent(this, LandingMainActivity::class.java)
                                  }*/

                } else {
                    //verification unsuccessful.. display an error message
                    var message = getString(R.string.something_error)

                    if (task.exception is FirebaseAuthException) {
                        message = getString(R.string.invalid_otp)
                    }

                    showToastError(message)
                    //Toast.makeText(OtpVerificationFirebase.this, message, Toast.LENGTH_SHORT).show();

                }
            }
    }

    private fun callVerifyUserApi() {
        val mJsonObject = JsonObject()
        mJsonObject.addProperty("userId", userId)
        mJsonObject.addProperty("sessionToken", token)
        loginViewModel.callVerifyUserApi(mJsonObject)
    }

    private fun countDown() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimeRunning = true
                // activityOtpVerificationBinding.resendOTP = 1
                activityOtpVerificationBinding.tvResend.text =
                    "Resend in " + millisUntilFinished / 1000 + " sec"
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                isTimeRunning = false
                activityOtpVerificationBinding.tvResend.text =
                    getString(R.string.resend_otp)
            }

        }.start()
    }
}