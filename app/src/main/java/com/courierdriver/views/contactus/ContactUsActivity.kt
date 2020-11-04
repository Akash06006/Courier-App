package com.courierdriver.views.contactus

import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.databinding.ActivityContactUsBinding
import com.courierdriver.utils.BaseActivity
import com.courierdriver.viewmodels.ContactUsViewModel

class ContactUsActivity : BaseActivity() {
    lateinit var binding: ActivityContactUsBinding
    private var contactUsViewModel: ContactUsViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityContactUsBinding
        contactUsViewModel = ViewModelProviders.of(this).get(ContactUsViewModel::class.java)
        binding.contactUsViewModel = contactUsViewModel
        binding.toolbarCommon.imgToolbarText.text = getString(R.string.contact_us)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_contact_us
    }

}
