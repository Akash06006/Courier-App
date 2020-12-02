package com.android.courier.views.orders

import android.app.Dialog
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.courier.R
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityCreateOrderBinding
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogssInterface
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.home.fragments.HomeFragment
import com.android.courier.views.orders.fragments.CreateOrderFirstFragment
import com.bumptech.glide.Glide
import com.google.android.libraries.places.api.Places
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_order.view.*
import kotlin.collections.ArrayList

class CreateOrderActivty : BaseActivity(), DialogssInterface {
    private lateinit var activityCreateOrderBinding : ActivityCreateOrderBinding
    private lateinit var orderViewModel : OrderViewModel
    var addressList = ArrayList<CreateOrdersInput.PickupAddress>()
    var i = -1
    var orderId = ""
    var pickupAddress = ""
    var userImage = ""
    private var confirmationDialog : Dialog? = null
    override fun getLayoutId() : Int {
        return R.layout.activity_create_order
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        activityCreateOrderBinding = viewDataBinding as ActivityCreateOrderBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        activityCreateOrderBinding.orderViewModel = orderViewModel

        activityCreateOrderBinding.imgToolbarText.text =
            getString(R.string.create_order)
        userImage =
            SharedPrefClass().getPrefValue(this, GlobalConstants.USER_IMAGE).toString()


        if (UtilsFunctions.isNetworkConnected()) {
            //  startProgressDialog()
        }
        val fragment = CreateOrderFirstFragment()
        this.callFragments(
            fragment,
            supportFragmentManager,
            false,
            "send_data",
            ""
        )





        orderId = intent.extras?.get("id").toString()
        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgPriceDetails" -> {
                        if (activityCreateOrderBinding.llPriceDetail.visibility == View.GONE) {
                            activityCreateOrderBinding.imgPriceDetails.setImageResource(
                                R.drawable.ic_down_arrow
                            )
                            activityCreateOrderBinding.llPriceDetail.visibility = View.VISIBLE
                        } else {
                            activityCreateOrderBinding.imgPriceDetails.setImageResource(
                                R.drawable.ic_up_arrow
                            )
                            activityCreateOrderBinding.llPriceDetail.visibility = View.GONE
                        }
                        ///showToastSuccess("Show Details")
                        // showCancelOrdersDialog()
                    }
                    "llPriceDetail" -> {
                    }
                }
            })
        )

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String?) {
        when (mKey) {
            "clearData" -> {
                //  clearAddressData()
                confirmationDialog?.dismiss()
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String?) {
        confirmationDialog?.dismiss()
    }

    /*(activity as CreateOrderActivty).setPrice(
                                    payableAmount,
                                    "deliveryFee",
                                    "weightFee",
                                    "securityFee",
                                    "couponDeduction",
                                    totalCancellationCharges
                                )*/
    fun setPrice(
        payableAmount : String,
        deliveryFee : String,
        weightFee : String,
        securityFee : String,
        couponDeduction : String,
        totalCancellationCharges : String
    ) {
        if (!totalCancellationCharges.equals("0")) {
        }
        activityCreateOrderBinding.txtFare.text = "₹ " + payableAmount
    }

    fun callPriceCalculationApi(mJsonObject : JsonObject) {
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        orderViewModel.calculatePrice(mJsonObject)
    }

}
