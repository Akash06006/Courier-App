package com.example.courier.views.authentication

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Build
import android.text.TextUtils
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.example.courier.R
import com.example.courier.application.MyApplication
import com.example.courier.constants.GlobalConstants
import com.example.courier.databinding.ActivityResetPasswordBinding
import com.example.courier.model.CommonModel
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.utils.BaseActivity
import com.example.courier.viewmodels.ResetPasswordModel
import com.google.gson.JsonObject

class ResetPasswrodActivity : BaseActivity() {
    private lateinit var resetPasswordModel : ResetPasswordModel
    private lateinit var activityResetPasswordBinding : ActivityResetPasswordBinding

    override fun getLayoutId() : Int {
        return R.layout.activity_reset_password
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        activityResetPasswordBinding = viewDataBinding as ActivityResetPasswordBinding
        activityResetPasswordBinding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.reset_password)

        resetPasswordModel = ViewModelProviders.of(this).get(ResetPasswordModel::class.java)
        activityResetPasswordBinding.resetViewModel = resetPasswordModel

        resetPasswordModel.getResetPasswordResponse().observe(this,
            Observer<CommonModel> { commonResponse->
                if (commonResponse != null) {
                    val message = commonResponse.message

                    if (commonResponse.code == 200) {
                        showToastSuccess(message)
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        showToastError(message)
                    }

                }
            })

        resetPasswordModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btn_submit" -> {
                        val password = activityResetPasswordBinding.etNewPassword.text.toString()
                        val confirmPassword =
                            activityResetPasswordBinding.etConfirmPassword.text.toString()

                        when {
                            password.isEmpty() -> showError(
                                activityResetPasswordBinding.etNewPassword,
                                getString(R.string.empty) + " " + getString(
                                    R.string.enter_new_password1
                                )
                            )
                            password.length < 8 -> showError(
                                activityResetPasswordBinding.etNewPassword,
                                getString(R.string.password_len_msg)
                            )
                            confirmPassword.isEmpty() -> showError(
                                activityResetPasswordBinding.etConfirmPassword,
                                getString(R.string.empty) + " " + getString(
                                    R.string.confirm_password
                                )
                            )
                            password != confirmPassword -> showError(
                                activityResetPasswordBinding.etConfirmPassword,
                                MyApplication.instance.getString(R.string.mismatch_paaword)
                            )
                            else -> {
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty(
                                    "userId",
                                    SharedPrefClass().getPrefValue(
                                        this,
                                        GlobalConstants.USERID
                                    ).toString()
                                )
                                mJsonObject.addProperty("newPassword", password)
                                mJsonObject.addProperty("confirmPassword", confirmPassword)
                                resetPasswordModel.callResetPassword(mJsonObject)
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
