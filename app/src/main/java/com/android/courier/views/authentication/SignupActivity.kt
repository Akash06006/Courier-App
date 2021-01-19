package com.android.courier.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.text.TextUtils
import android.widget.EditText
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.common.FirebaseFunctions
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivitySignupBinding
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.ValidationsClass
import com.android.courier.viewmodels.LoginViewModel
import com.android.courier.views.home.LandingActivty
import com.google.gson.JsonObject
import org.json.JSONObject

class SignupActivity : BaseActivity() {
    private lateinit var activitySignupbinding : ActivitySignupBinding
    private lateinit var loginViewModel : LoginViewModel
    val mOtpJsonObject = JsonObject()
    var isSocial = false
    var socialType = ""
    var socialId = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_signup
    }

    override fun initViews() {
        activitySignupbinding =
            viewDataBinding as ActivitySignupBinding //DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        activitySignupbinding.loginViewModel = loginViewModel
        val fbSocial = intent.extras.get("fbSocial").toString()
        val googleSocial = intent.extras.get("googleSocial").toString()
        if (fbSocial.equals("true")) {
            isSocial = true
            socialType = "facebook"
            val fbDetails = JSONObject(intent.extras.get("fbData").toString())
            if (fbDetails.has("id")) {
                socialId = fbDetails.getString("id")
            }
            if (fbDetails.has("name")) {
                val fullname = fbDetails.getString("name")
                if (fullname.contains(" ")) {
                    var name = fullname.split(" ")
                    val firstName = name[0]
                    val lastname = name[1]

                    activitySignupbinding.edtFirstName.setText(firstName)
                    activitySignupbinding.edtLastName.setText(lastname)
                } else {
                    activitySignupbinding.edtFirstName.setText(fullname)
                }
            }
            if (fbDetails.has("email")) {
                val email = fbDetails.getString("email")
                activitySignupbinding.edtEmail.setText(email)

            }
        } else if (googleSocial.equals("true")) {
            isSocial = true
            socialType = "google"
            val googleDetails = JSONObject(intent.extras.get("googleData").toString())
            if (googleDetails.has("id")) {
                socialId = googleDetails.getString("id")
            }
            if (googleDetails.has("name")) {
                val fullname = googleDetails.getString("name")
                if (fullname.contains(" ")) {
                    var name = fullname.split(" ")
                    val firstName = name[0]
                    val lastname = name[1]

                    activitySignupbinding.edtFirstName.setText(firstName)
                    activitySignupbinding.edtLastName.setText(lastname)
                } else {
                    activitySignupbinding.edtFirstName.setText(fullname)
                }
            }
            if (googleDetails.has("email")) {
                val email = googleDetails.getString("email")
                activitySignupbinding.edtEmail.setText(email)
            }
        }
        // activityLoginbinding.tvForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        loginViewModel.getSignupRes().observe(this,
            Observer<LoginResponse> { loginResponse->
                stopProgressDialog()
                // FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        /* SharedPrefClass().putObject(
                             MyApplication.instance,
                             "isLogin",
                             true
                         )*/
                        GlobalConstants.VERIFICATION_TYPE = "signup"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        // mOtpJsonObject.addProperty("phoneNumber", response.data?.phoneNumber)
                        //mOtpJsonObject.addProperty("countryCode", response.data?.countryCode)
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.ACCESS_TOKEN,
                            loginResponse.data!!.token
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERID,
                            loginResponse.data!!.id
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.PRIVACY_POLICY,
                            loginResponse.data!!.privacyLink
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.ABOUT_US,
                            loginResponse.data!!.aboutUsLink
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.TERMS_CONDITION,
                            loginResponse.data!!.termsLink
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USEREMAIL,
                            loginResponse.data!!.email
                        )
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERNAME,
                            loginResponse.data!!.firstName + " " + loginResponse.data!!.lastName
                        )


                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.IS_SOCIAL,
                            isSocial
                        )

                        if (!TextUtils.isEmpty(loginResponse.data!!.referralCode)) {
                            SharedPrefClass().putObject(
                                MyApplication.instance,
                                GlobalConstants.REFERRAL_CODE,
                                loginResponse.data!!.referralCode
                            )
                        }

                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERNAME,
                            activitySignupbinding.edtFirstName.text.toString() + " " + activitySignupbinding.edtLastName.text.toString()
                        )
                        val mJsonObject = JsonObject()
                        mJsonObject.addProperty("userId", loginResponse.data!!.id)
                        mJsonObject.addProperty("sessionToken", loginResponse.data!!.token)
                        // loginViewModel.callVerifyUserApi(mJsonObject)
                        /*showToastSuccess(message)
                        val intent = Intent(this, OTPVerificationActivity::class.java)
                        startActivity(intent)
                        finish()*/

                    } else if (loginResponse.code == 408) {
                        showToastError(message)
                    } else {
                        showToastError(message)
                    }

                }
            })
        /*loginViewModel.getEmailError().observe(this, Observer<String> { emailError->
            activityLoginbinding.etEmail.error = emailError
            activityLoginbinding.etEmail.requestFocus()
        })


        loginViewModel.getPasswordError().observe(this, Observer<String> { passError->
            activityLoginbinding.etPassword.requestFocus()
            activityLoginbinding.etPassword.error = passError
        })*/
        loginViewModel.getVerifyUserRes().observe(this,
            Observer<CommonModel> { loginResponse->
                stopProgressDialog()
                if (loginResponse != null) {
                    val message = loginResponse.message

                    if (loginResponse.code == 200) {
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "isLogin",
                            true
                        )
                        //showToastSuccess(message)
                        val intent = Intent(this, LandingActivty::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        //showToastError(message)
                    }

                }
            })


        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        loginViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btnSignup" -> {
                        val fName = activitySignupbinding.edtFirstName.text.toString()
                        val lName = activitySignupbinding.edtLastName.text.toString()
                        val email = activitySignupbinding.edtEmail.text.toString()
                        val phone = activitySignupbinding.edtPhone.text.toString()
                        val password = activitySignupbinding.edtPassword.text.toString()
                        val confirmPassword =
                            activitySignupbinding.edtConfirmPassword.text.toString()
                        val referralCode =
                            activitySignupbinding.edtReferral.text.toString()


                        when {
                            fName.isEmpty() -> showError(
                                activitySignupbinding.edtFirstName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.fname
                                )
                            )
                            lName.isEmpty() -> showError(
                                activitySignupbinding.edtLastName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.lname
                                )
                            )
                            email.isEmpty() -> showError(
                                activitySignupbinding.edtEmail,
                                getString(R.string.empty) + " " + getString(
                                    R.string.email
                                )
                            )
                            !email.matches((ValidationsClass.EMAIL_PATTERN).toRegex()) ->
                                showError(
                                    activitySignupbinding.edtEmail,
                                    getString(R.string.invalid) + " " + getString(
                                        R.string.email
                                    )
                                )
                            phone.isEmpty() -> showError(
                                activitySignupbinding.edtPhone,
                                getString(R.string.empty) + " " + getString(
                                    R.string.phone_number
                                )
                            )
                            phone.length < 10 -> showError(
                                activitySignupbinding.edtPhone,
                                getString(R.string.phone_number) + " " + getString(
                                    R.string.phone_min
                                )
                            )
                            /* password.isEmpty() -> showError(
                                 activitySignupbinding.edtPassword,
                                 getString(R.string.empty) + " " + getString(
                                     R.string.password
                                 )
                             )
                             password.length < 8 -> showError(
                                 activitySignupbinding.edtPassword,
                                 getString(R.string.password_len_msg)
                             )
                             confirmPassword.isEmpty() -> showError(
                                 activitySignupbinding.edtConfirmPassword,
                                 getString(R.string.empty) + " " + getString(
                                     R.string.confirm_password
                                 )
                             )
                             password != confirmPassword -> showError(
                                 activitySignupbinding.edtConfirmPassword,
                                 MyApplication.instance.getString(R.string.mismatch_paaword)
                             )*/
                            !activitySignupbinding.chkTerms.isChecked() -> {
                                showToastError("Please agree to Terms and Conditions")
                            }
                            else -> {
                                mOtpJsonObject.addProperty(
                                    "countryCode",
                                    "+" + 91
                                )
                                mOtpJsonObject.addProperty("phoneNumber", phone)
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty("firstName", fName)
                                mJsonObject.addProperty("lastName", lName)
                                mJsonObject.addProperty(
                                    "countryCode",
                                    "+" + activitySignupbinding.btnCcp.selectedCountryCode
                                )
                                mJsonObject.addProperty("phoneNumber", phone)
                                mJsonObject.addProperty("email", email)
                                // mJsonObject.addProperty("password", password)
                                mJsonObject.addProperty(
                                    "isSocial",
                                    isSocial
                                )
                                mJsonObject.addProperty(
                                    "deviceToken",
                                    GlobalConstants.NOTIFICATION_TOKEN
                                )
                                mJsonObject.addProperty("platform", "android")
                                mJsonObject.addProperty("referralCode", referralCode)

                                mJsonObject.addProperty("socialType", socialType)
                                mJsonObject.addProperty("socialId", socialId)


                                if (UtilsFunctions.isNetworkConnected()) {
                                    loginViewModel.callSignupApi(mJsonObject)
                                }
                                //val intent = Intent(this, OTPVerificationActivity::class.java)
                                //intent.putExtra("catId", ""/*categoriesList[position].id*/)
                                //startActivity(intent)
                            }
                        }

                    }
                }
            })
        )

    }

    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }
}