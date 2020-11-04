package com.courierdriver.views.authentication

import android.annotation.TargetApi
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.application.MyApplication
import com.courierdriver.databinding.ActivityChangePasswordBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.ChangePasswordModel

class ChangePasswrodActivity : BaseActivity() {
    private var changePasswordModel: ChangePasswordModel? = null

    @RequiresApi(Build.VERSION_CODES.M)
    private lateinit var activityChangePasswordBinding: ActivityChangePasswordBinding

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        activityChangePasswordBinding = viewDataBinding as ActivityChangePasswordBinding
        activityChangePasswordBinding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.change_password)

        changePasswordModel = ViewModelProviders.of(this).get(ChangePasswordModel::class.java)
        activityChangePasswordBinding.changeViewModel = changePasswordModel

        changePasswordModel!!.isLoading().observe(
            this,
            Observer<Boolean> { aBoolean ->
                if (aBoolean!!) {
                    startProgressDialog()
                } else {
                    stopProgressDialog()
                }
            }
        )


        changePasswordModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                val oldpass = activityChangePasswordBinding.etOldPassword.text.toString()
                val confirm = activityChangePasswordBinding.etConfirmPassword.text.toString()
                val password = activityChangePasswordBinding.etNewPassword.text.toString()

                when (it) {
                    "btn_submit" -> {
                        when {
                            oldpass.isEmpty() -> showOldPasswordError(
                                MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                                    R.string.enter_old_password
                                )
                            )
                            password.isEmpty() -> showPasswordError(
                                MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                                    R.string.enter_new_password1
                                )
                            )
                            password.length < 8 -> showPasswordError(
                                MyApplication.instance.getString(
                                    R.string.password_len_msg
                                )
                            )
                            confirm.isEmpty() -> showConfirmPasswordError(
                                MyApplication.instance.getString(R.string.empty) + " " + MyApplication.instance.getString(
                                    R.string.confirm_password
                                )
                            )
                            confirm.length < 8 -> showConfirmPasswordError(
                                MyApplication.instance.getString(
                                    R.string.password_len_msg
                                )
                            )
                            password != confirm -> showConfirmPasswordError(
                                MyApplication.instance.getString(
                                    R.string.mismatch_paaword
                                )
                            )
                            else -> changePasswordModel!!.changePasswordApi(oldpass, password)
                        }

                    }
                }

            })
        )



        changePasswordModel!!.getChangePasswordResponse().observe(this,
            Observer<CommonModel> { commonResponse ->
                stopProgressDialog()
                if (commonResponse != null) {
                    val message = commonResponse.message.toString()

                    if (commonResponse.code == 200) {
                        this.eventCreatedDialog(this, "change_password", message)

                    } else {
                        showToastError(message)
                    }

                }
            })

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun showPasswordError(error: String) {
        activityChangePasswordBinding.etNewPassword.requestFocus()
        activityChangePasswordBinding.etNewPassword.error = error

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun showOldPasswordError(error: String) {
        activityChangePasswordBinding.etOldPassword.requestFocus()
        activityChangePasswordBinding.etOldPassword.error = error

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun showConfirmPasswordError(error: String) {
        activityChangePasswordBinding.etConfirmPassword.requestFocus()
        activityChangePasswordBinding.etConfirmPassword.error = error

    }

}
