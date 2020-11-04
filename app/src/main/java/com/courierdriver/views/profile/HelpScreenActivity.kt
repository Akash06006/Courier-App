package com.courierdriver.views.profile

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.databinding.ActivityHelpBinding
import com.courierdriver.model.CommonModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.HelpScreenViewModel
import com.courierdriver.views.authentication.LoginActivity

class HelpScreenActivity : BaseActivity() {
    private var binding: ActivityHelpBinding? = null
    private var viewModel: HelpScreenViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityHelpBinding?
        viewModel = ViewModelProviders.of(this).get(HelpScreenViewModel::class.java)
        binding!!.viewModel = viewModel

        setToolbarTextIcons()
        viewClicks()
    }

    private fun viewClicks() {
        viewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_logout" -> {
                        logoutObserver()
                    }
                    "tv_spot_and_error" -> {
                        startActivity(Intent(this, SpotAndErrorActivity::class.java))
                    }
                }
            })
        )
    }

    private fun logoutObserver() {
        viewModel!!.logout()
        viewModel!!.logoutData().observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    when (response.code) {
                        200 -> {
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

    private fun setToolbarTextIcons() {
        binding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        binding!!.toolbarCommon.imgRight.visibility = View.GONE
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.help)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_help
    }
}