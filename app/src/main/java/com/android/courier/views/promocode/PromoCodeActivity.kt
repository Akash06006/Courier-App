package com.android.courier.views.promocode

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
import com.android.courier.R
import com.android.courier.adapters.PromoCodeListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityPromoCodeBinding
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.viewmodels.promocode.PromoCodeViewModel
import com.android.services.model.promocode.PromoCodeListResponse
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
                            promoCodeBinding.noRecordAnimation.visibility = View.GONE
                            promoCodeBinding.tvNoRecord.visibility = View.GONE
                            initRecyclerView()
                        }
                        else -> /*message?.let*/ {
                            //UtilsFunctions.showToastError(it)
                            promoCodeBinding.rvPromo.visibility = View.GONE
                            promoCodeBinding.tvNoRecord.visibility = View.VISIBLE
                            promoCodeBinding.noRecordAnimation.visibility = View.VISIBLE
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
        val controller =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_left)
        promoCodeBinding.rvPromo.setLayoutAnimation(controller);
        promoCodeBinding.rvPromo.scheduleLayoutAnimation();
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
