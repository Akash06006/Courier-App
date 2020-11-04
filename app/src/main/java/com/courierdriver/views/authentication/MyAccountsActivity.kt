package com.courierdriver.views.authentication

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.databinding.ActivityMyAccountsBinding
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.MyAccountsViewModel

class MyAccountsActivity : BaseActivity() {
    lateinit var binding: ActivityMyAccountsBinding
    private var accountsViewModel: MyAccountsViewModel? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_my_accounts
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityMyAccountsBinding
        accountsViewModel = ViewModelProviders.of(this).get(MyAccountsViewModel::class.java)
        binding.myaccountsViewModel = accountsViewModel
        binding.toolbarCommon.imgToolbarText.text = getString(R.string.my_account)
        accountsViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_change_password" -> {
                        val intent1 = Intent(this, ChangePasswrodActivity::class.java)
                        startActivity(intent1)
                    }
                }

            })
        )

    }
}
