package com.courierdriver.views.profile

import android.app.Dialog
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityHelpBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.model.HelpLinksModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.viewmodels.HelpScreenViewModel
import com.courierdriver.views.authentication.LoginActivity

class HelpScreenActivity : BaseActivity(), DialogssInterface {
    private var helpLinksData: HelpLinksModel.Body? = null
    private var binding: ActivityHelpBinding? = null
    private var viewModel: HelpScreenViewModel? = null
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()

    override fun initViews() {
        binding = viewDataBinding as ActivityHelpBinding?
        viewModel = ViewModelProviders.of(this).get(HelpScreenViewModel::class.java)
        binding!!.viewModel = viewModel

        loaderObserver()
        helpLinksListObserver()
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
                    "how_it_works" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.howWorksLink)
                        startActivity(intent)
                    }
                    "tv_respond_order" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.resFirstOrderLink)
                        startActivity(intent)
                    }
                    "tv_frequently_quest" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.faqLink)
                        startActivity(intent)
                    }
                    "tv_imp_points" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.impPoints)
                        startActivity(intent)
                    }
                    "tv_pay_comission" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.payCommissionLink)
                        startActivity(intent)
                    }
                    "tv_payment_for_order" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.collectPaymentLink)
                        startActivity(intent)
                    }
                    "tv_earn_more" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.avoidFineLink)
                        startActivity(intent)
                    }
                    "tv_contact_us" -> {
                        val intent = Intent(this,TermsAndConditionsActivity::class.java)
                        intent.putExtra("url",helpLinksData!!.contactUsLink )
                        startActivity(intent)
                    }
                }
            })
        )
    }

    private fun helpLinksListObserver() {
        viewModel!!.helpLinks()
        viewModel!!.helpLinksData().observe(this,
            Observer<HelpLinksModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            helpLinksData = response.body
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