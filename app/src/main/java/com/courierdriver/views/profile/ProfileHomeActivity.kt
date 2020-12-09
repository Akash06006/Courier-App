package com.courierdriver.views.profile

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.courierdriver.R
import com.courierdriver.databinding.FragmentProfileHomeBinding
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.BaseFragment
import com.courierdriver.viewmodels.home.HomeViewModel


class ProfileHomeActivity : BaseActivity() {
    private var binding: FragmentProfileHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel

    override fun initViews() {
        binding = viewDataBinding as FragmentProfileHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding!!.homeViewModel = homeViewModel

        newCallFragments(ProfileDetailsFragment(),supportFragmentManager)
        viewClicks()
        setToolbarTextIcons()
    }

    private fun setToolbarTextIcons() {
        binding!!.toolbarCommon.imgToolbarText.text = getString(R.string.profile)
        binding!!.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        binding!!.toolbarCommon.imgRight.visibility = View.GONE
        binding!!.toolbarCommon.imgRight.setImageResource(R.drawable.ic_help)
    }

    private fun viewClicks() {
        var textViewList = ArrayList<TextView>()
        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_right" -> {
                        startActivity(Intent(this, HelpScreenActivity::class.java))
                    }
                    "tv_profile_details" -> {
                        setButtonEnabled(binding!!.tvProfileDetails)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvStatistics)
                        textViewList.add(binding!!.tvAccount)
                        textViewList.add(binding!!.tvIdProof)
                        textViewList.add(binding!!.tvLocomoId)
                        textViewList.add(binding!!.tvPaymentOptions)
                        setButtonDisabled(textViewList)

                        newCallFragments(ProfileDetailsFragment(),supportFragmentManager)
                    }
                    "tv_statistics" -> {
                        setButtonEnabled(binding!!.tvStatistics)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvProfileDetails)
                        textViewList.add(binding!!.tvAccount)
                        textViewList.add(binding!!.tvIdProof)
                        textViewList.add(binding!!.tvLocomoId)
                        textViewList.add(binding!!.tvPaymentOptions)
                        setButtonDisabled(textViewList)

                        newCallFragments(StatisticsActivity(),supportFragmentManager)
                    }
                    "tv_account" -> {
                        setButtonEnabled(binding!!.tvAccount)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvProfileDetails)
                        textViewList.add(binding!!.tvStatistics)
                        textViewList.add(binding!!.tvIdProof)
                        textViewList.add(binding!!.tvLocomoId)
                        textViewList.add(binding!!.tvPaymentOptions)
                        setButtonDisabled(textViewList)

                        newCallFragments(ProfileFragment(),supportFragmentManager)
                    }
                    "tv_id_proof" -> {
                        setButtonEnabled(binding!!.tvIdProof)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvProfileDetails)
                        textViewList.add(binding!!.tvStatistics)
                        textViewList.add(binding!!.tvAccount)
                        textViewList.add(binding!!.tvLocomoId)
                        textViewList.add(binding!!.tvPaymentOptions)
                        setButtonDisabled(textViewList)

                        newCallFragments(ProfileDocumentFragment(),supportFragmentManager)
                    }
                    "tv_locomo_id" -> {
                        setButtonEnabled(binding!!.tvLocomoId)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvProfileDetails)
                        textViewList.add(binding!!.tvStatistics)
                        textViewList.add(binding!!.tvAccount)
                        textViewList.add(binding!!.tvPaymentOptions)
                        textViewList.add(binding!!.tvIdProof)
                        setButtonDisabled(textViewList)

                        newCallFragments(LocomoIdFragment(),supportFragmentManager)
                    }
                    "tv_payment_options"->
                    {
                        setButtonEnabled(binding!!.tvPaymentOptions)
                        textViewList = ArrayList()
                        textViewList.add(binding!!.tvProfileDetails)
                        textViewList.add(binding!!.tvStatistics)
                        textViewList.add(binding!!.tvAccount)
                        textViewList.add(binding!!.tvLocomoId)
                        textViewList.add(binding!!.tvIdProof)
                        setButtonDisabled(textViewList)

                        newCallFragments(PaymentListActivity(),supportFragmentManager)
                    }
                }
            })
        )
    }

    private fun setButtonEnabled(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlue))
    }

    private fun setButtonDisabled(textView: ArrayList<TextView>) {
        for (i in 0 until textView.size)
        textView[i].setTextColor(ContextCompat.getColor(this, R.color.colorBlack))
    }

/*
    private fun newCallFragments(
        fragment: androidx.fragment.app.Fragment?
    ) {
        val childFragMan: FragmentManager = childFragmentManager
        val childFragTrans: FragmentTransaction = childFragMan.beginTransaction()
        childFragTrans.replace(R.id.frame_layout, fragment!!)
        childFragTrans.commit()
    }
*/

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile_home
    }
}