package com.android.courier.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.common.FirebaseFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityForgotPasswordBinding
import com.android.courier.model.CommonModel
import com.android.courier.model.LoginResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.ValidationsClass
import com.android.courier.viewmodels.ForgotPasswordModel
import com.google.gson.JsonObject

class ForgotPasswrodActivity : BaseActivity() {
    private lateinit var forgotPasswordModel : ForgotPasswordModel
    val mOtpJsonObject = JsonObject()
    private lateinit var activityForgotPasswordBinding : ActivityForgotPasswordBinding

    override fun getLayoutId() : Int {
        return R.layout.activity_forgot_password
    }

    override fun initViews() {
        activityForgotPasswordBinding = viewDataBinding as ActivityForgotPasswordBinding
        activityForgotPasswordBinding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.forgot_password_heading)
        activityForgotPasswordBinding.toolbarCommon.imgRight.visibility = View.GONE
        forgotPasswordModel = ViewModelProviders.of(this).get(ForgotPasswordModel::class.java)
        activityForgotPasswordBinding.forgotViewModel = forgotPasswordModel


        forgotPasswordModel.isLoading().observe(this, Observer<Boolean> { aBoolean->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })


        forgotPasswordModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btn_cancel" -> {
                        finish()

                    }
                    "btn_send" -> {
                        if (TextUtils.isEmpty(activityForgotPasswordBinding.edtPhone.text.toString())) run {
                            activityForgotPasswordBinding.edtPhone.requestFocus()
                            activityForgotPasswordBinding.edtPhone.error =
                                getString(R.string.empty) + " " + MyApplication.instance.getString(
                                    R.string.phone_number
                                )
                        } else if (activityForgotPasswordBinding.edtPhone.text.length < 10) {
                            activityForgotPasswordBinding.edtPhone.error =
                                getString(R.string.phone_number) + " " + getString(
                                    R.string.phone_min
                                )

                        } else {
                            mOtpJsonObject.addProperty(
                                "countryCode",
                                "+" + activityForgotPasswordBinding.btnCcp.selectedCountryCode
                            )
                            mOtpJsonObject.addProperty(
                                "phoneNumber",
                                activityForgotPasswordBinding.edtPhone.text.toString()
                            )
                            forgotPasswordModel.forgotApi(
                                "+" + activityForgotPasswordBinding.btnCcp.selectedCountryCode,
                                activityForgotPasswordBinding.edtPhone.text.toString()
                            )

                        }

                    }
                }

            })
        )

        forgotPasswordModel.getForgotPasswordResponse().observe(this,
            Observer<LoginResponse> { forgotResponse->
                stopProgressDialog()
                if (forgotResponse != null) {
                    val message = forgotResponse.message

                    if (forgotResponse.code == 200) {
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "user_email",
                            activityForgotPasswordBinding.etEmail.text.toString()
                        )

                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            GlobalConstants.USERID,
                            forgotResponse.data!!.id
                        )
                        //showToastSuccess(message!!)
                        GlobalConstants.VERIFICATION_TYPE = "forgot"
                        FirebaseFunctions.sendOTP("login", mOtpJsonObject, this)
                        // val intent = Intent(this, ResetPasswrodActivity::class.java)
                        // startActivity(intent)

                    } else {
                        showToastError(message)
                    }

                }
            })

    }

}
