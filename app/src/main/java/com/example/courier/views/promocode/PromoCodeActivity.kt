package com.example.courier.views.promocode

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.courier.R
import com.example.courier.adapters.PromoCodeListAdapter
import com.example.courier.application.MyApplication
import com.example.courier.common.UtilsFunctions
import com.example.courier.constants.GlobalConstants
import com.example.courier.databinding.ActivityPromoCodeBinding
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.utils.BaseActivity
import com.example.courier.viewmodels.promocode.PromoCodeViewModel
import com.example.services.model.promocode.PromoCodeListResponse
import com.google.gson.JsonObject

class PromoCodeActivity : BaseActivity() {
    lateinit var promoCodeBinding : ActivityPromoCodeBinding
    lateinit var promcodeViewModel : PromoCodeViewModel
    var promocodeListAdapter : PromoCodeListAdapter? = null
    var cartObject = JsonObject()
    var pos = 0
    var promoList = ArrayList<PromoCodeListResponse.Body>()
    override fun getLayoutId() : Int {
        return R.layout.activity_promo_code
    }

    override fun initViews() {
        promoCodeBinding = viewDataBinding as ActivityPromoCodeBinding
        promcodeViewModel = ViewModelProviders.of(this).get(PromoCodeViewModel::class.java)

        promoCodeBinding.commonToolBar.imgRight.visibility = View.GONE
        promoCodeBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.offer_zone)
        promoCodeBinding.promcodeViewModel = promcodeViewModel
        val userId = SharedPrefClass()!!.getPrefValue(
            MyApplication.instance,
            GlobalConstants.USERID
        ).toString()
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            //cartViewModel.getcartList(userId)
        }
        //initRecyclerView()
        UtilsFunctions.hideKeyBoard(promoCodeBinding.tvNoRecord)
        promcodeViewModel.getPromoListRes().observe(this,
            Observer<PromoCodeListResponse> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            promoList.addAll(response.data!!)
                            promoCodeBinding.rvPromo.visibility = View.VISIBLE
                            promoCodeBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            promoCodeBinding.rvPromo.visibility = View.GONE
                            promoCodeBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }

                }
            })


        promcodeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "btnApplyPromo" -> {
                    }
                }
            })
        )

    }

    private fun initRecyclerView() {
        promocodeListAdapter = PromoCodeListAdapter(this, promoList, this)
        val linearLayoutManager = LinearLayoutManager(this)
        //val gridLayoutManager = GridLayoutManager(this, 2)
        //promoCodeBinding.rvFavorite.layoutManager = gridLayoutManager
        promoCodeBinding.rvPromo.setHasFixedSize(true)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        promoCodeBinding.rvPromo.layoutManager = linearLayoutManager
        promoCodeBinding.rvPromo.adapter = promocodeListAdapter
        promoCodeBinding.rvPromo.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })
    }

    fun callApplyCouponApi(code : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            promcodeViewModel.applyPromoCode(code)
        }

    }

}
