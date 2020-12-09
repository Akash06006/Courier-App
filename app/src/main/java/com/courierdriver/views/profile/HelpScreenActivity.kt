package com.courierdriver.views.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityHelpBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.viewmodels.HelpScreenViewModel
import com.courierdriver.views.authentication.LoginActivity
import com.payumoney.core.PayUmoneyConfig
import com.payumoney.core.PayUmoneyConstants
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HelpScreenActivity : BaseActivity(), DialogssInterface {
    private var binding: ActivityHelpBinding? = null
    private var viewModel: HelpScreenViewModel? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()

    override fun initViews() {
        binding = viewDataBinding as ActivityHelpBinding?
        viewModel = ViewModelProviders.of(this).get(HelpScreenViewModel::class.java)
        binding!!.viewModel = viewModel

        loaderObserver()
        setToolbarTextIcons()
        viewClicks()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_payments" -> {
                        startActivity(Intent(this, PaymentHistoryActivity::class.java))
                    }
                    "tv_logout" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            this,
                            this,
                            "logout",
                            getString(R.string.logout),
                            getString(R.string.want_to_logout),
                            getString(R.string.logout_alert)
                        )
                        confirmationDialog!!.show()
                    }
                    "tv_spot_and_error" -> {
                        startActivity(Intent(this, SpotAndErrorActivity::class.java))
                    }
                    "tv_how_to_complete_order" -> {
                        startActivity(Intent(this, TermsAndConditionsActivity::class.java))
                    }
                }
            })
        )
    }

    private fun logoutObserver() {
        viewModel!!.logout()
        viewModel!!.logoutData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            confirmationDialog!!.dismiss()
                            showToastSuccess(response.message)
                            SharedPrefClass().putObject(
                                this,
                                "isLogin",
                                false
                            )
                            val intent1 = Intent(this, LoginActivity::class.java)
                            startActivity(intent1)
                            finish()
                            SharedPrefClass().clearAll(this)
                        }
                        else -> {
                            showToastError(response.message)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String?) {
        logoutObserver()
    }

    override fun onDialogCancelAction(mView: View?, mKey: String?) {
        confirmationDialog!!.dismiss()
    }

    private fun setToolbarTextIcons() {
        binding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        binding!!.toolbarCommon.imgRight.visibility = View.GONE
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.help)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_help
    }

    private fun loaderObserver() {
        viewModel!!.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }
}