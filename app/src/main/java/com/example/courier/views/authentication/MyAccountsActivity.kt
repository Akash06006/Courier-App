package com.example.courier.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import com.example.courier.R
import com.example.courier.databinding.ActivityMyAccountsBinding
import com.example.courier.utils.BaseActivity
import com.example.courier.viewmodels.MyAccountsViewModel

class MyAccountsActivity : BaseActivity() {
    lateinit var binding : ActivityMyAccountsBinding
    private var accountsViewModel : MyAccountsViewModel? = null

    override fun getLayoutId() : Int {
        return R.layout.activity_my_accounts
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityMyAccountsBinding
        accountsViewModel = ViewModelProviders.of(this).get(MyAccountsViewModel::class.java)
        binding.myaccountsViewModel = accountsViewModel
        binding.toolbarCommon.imgToolbarText.text=getString(R.string.my_account)
        accountsViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
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
