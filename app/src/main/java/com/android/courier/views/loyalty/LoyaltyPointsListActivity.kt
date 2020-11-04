package com.android.courier.views.refer

import android.app.Dialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.loyalty.LoyaltyListAdapter
import com.android.courier.common.UtilsFunctions
import com.google.gson.JsonObject
import com.android.courier.databinding.ActivityLoyaltyBinding
import com.android.courier.model.loyalty.LoyaltyResponse
import com.android.courier.model.loyalty.User
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.android.courier.viewmodels.loyalty.LoyaltyViewModel

class LoyaltyPointsListActivity : BaseActivity() {
    lateinit var loyaltyBinding : ActivityLoyaltyBinding
    lateinit var loyaltyViewModel : LoyaltyViewModel
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private val mJsonObject = JsonObject()
    var notificationList = ArrayList<User>()
    override fun getLayoutId() : Int {
        return R.layout.activity_loyalty
    }

    override fun onBackPressed() {
        finish()
    }

    override fun initViews() {
        loyaltyBinding = viewDataBinding as ActivityLoyaltyBinding
        loyaltyViewModel = ViewModelProviders.of(this).get(LoyaltyViewModel::class.java)
        //loyaltyBinding.commonToolBar.imgRight.visibility = View.GONE
        //loyaltyBinding.commonToolBar.imgToolbarText.text =    resources.getString(R.string.refer_earn)
        loyaltyBinding.loyaltyViewModel = loyaltyViewModel
        //UtilsFunctions.hideKeyBoard(reviewsBinding.tvNoRecord)
        loyaltyViewModel.getList().observe(this,
            Observer<LoyaltyResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            notificationList.clear()

                            notificationList.addAll(response.data?.users!!)
                            if (notificationList.size > 0) {
                                initLoyaltyAdapter()
                                loyaltyBinding.llPoints.visibility = View.VISIBLE
                                if (response.data?.totalPoints!!.toInt() > 0) {
                                    loyaltyBinding.txtPoints.text = response.data?.totalPoints
                                } else {
                                    loyaltyBinding.txtPoints.text = "0"
                                }

                                loyaltyBinding.txtNoRecord.visibility = View.GONE
                            } else {
                                loyaltyBinding.llPoints.visibility = View.GONE
                                loyaltyBinding.txtNoRecord.visibility = View.VISIBLE
                            }

                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            loyaltyBinding.txtNoRecord.visibility = View.VISIBLE
                            loyaltyBinding.llPoints.visibility = View.GONE
                        }
                    }
                }
            })
        loyaltyViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "toolbar" -> {
                        finish()
                    }
                }
            })
        )

    }

    private fun initLoyaltyAdapter() {
        val ordersAdapter =
            LoyaltyListAdapter(
                this@LoyaltyPointsListActivity,
                notificationList,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        loyaltyBinding.rvLoyalty.layoutManager = linearLayoutManager
        loyaltyBinding.rvLoyalty.setHasFixedSize(true)
        loyaltyBinding.rvLoyalty.adapter = ordersAdapter
        loyaltyBinding.rvLoyalty.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

}
