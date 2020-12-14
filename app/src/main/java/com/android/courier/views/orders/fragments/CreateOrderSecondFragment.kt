package com.android.courier.views.orders.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.courier.databinding.FragmentCreateOrdersSecondBinding
import com.android.courier.R
import com.android.courier.adapters.orders.OrdersListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.common.UtilsFunctions.showToastError
import com.android.courier.common.UtilsFunctions.showToastSuccess
import com.android.courier.constants.GlobalConstants
import com.android.courier.maps.FusedLocationClass
import com.android.courier.model.CommonModel
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.home.LandingActivty
import com.android.courier.views.orders.CreateOrderActivty
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_create_orders_second.*

class
CreateOrderSecondFragment : BaseFragment() {
    private lateinit var orderViewModel : OrderViewModel
    private lateinit var fragmentCreateOrdersSecondBinding : FragmentCreateOrdersSecondBinding
    //var categoriesList = null
    private var isUsableLoyalty = false
    var discountApplied = ""
    var orderId = ""
    private lateinit var cancelOrderDetail : CancelOrder
    var cancelOrderIds = ""
    var payableAmount = ""
    var lPointsTotalUsed = "0"
    var lPointsTotalValue = "0"
    var paymentType = "2"
    private lateinit var loyaltyPointsData : LoyalityData

    override fun getLayoutResId() : Int {
        return R.layout.fragment_create_orders_second
    }

    override fun onResume() {
        super.onResume()

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentCreateOrdersSecondBinding = viewDataBinding as FragmentCreateOrdersSecondBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        fragmentCreateOrdersSecondBinding.orderViewModel = orderViewModel
        // categoriesList=List<Service>()
        calculatePrice()

        orderViewModel.orderListRes().observe(this,
            Observer<OrdersListResponse> { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })
        if (TextUtils.isEmpty(MyApplication.createOrdersInput.fareCollected)) {
            MyApplication.createOrdersInput.fareCollected = "Pickup Location"
        }
        preFillData()


        orderViewModel.calculatePriceRes().observe(this,
            Observer<CalculatePriceResponse> { response->
                // stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            val orderPrice = response.data?.beforeDiscount.toString()
                            payableAmount = response.data?.afterDiscount.toString()
                            var totalCancellationCharges = "0"

                            if (/*discountApplied*/GlobalConstants.discountApplied.equals("")) {
                                fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = true
                            } else {
                                fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = false

                            }

                            if (TextUtils.isEmpty(orderId) || orderId.equals("null")) {
                                if (response.data?.loyalityData != null && response.data?.loyalityData?.usabelPoints!!.toDouble() > 0) {
                                    loyaltyPointsData = response.data?.loyalityData!!

                                    fragmentCreateOrdersSecondBinding.rlLoyaltyPoints.visibility =
                                        View.VISIBLE
                                    fragmentCreateOrdersSecondBinding.txtLoyaltyPoints.visibility =
                                        View.VISIBLE
                                    fragmentCreateOrdersSecondBinding.txtLoyaltyPoints.text =
                                        "Max. usable points " + response.data?.loyalityData?.usabelPoints + ", 1 Point = " + response.data?.loyalityData?.pricePerPoint + "₹"
                                } else {
                                    fragmentCreateOrdersSecondBinding.rlLoyaltyPoints.visibility =
                                        View.GONE
                                }
                            }
                            if (response.data?.cancelOrder != null && response.data?.cancelOrder?.orders!!.size > 0) {
                                cancelOrderDetail = response.data?.cancelOrder!!
                                cancelOrderIds = ""
                                for (item in response.data?.cancelOrder?.orders!!) {
                                    if (TextUtils.isEmpty(cancelOrderIds)) {
                                        cancelOrderIds = item.orderId as String
                                    } else {
                                        cancelOrderIds =
                                            cancelOrderIds + "," + item.orderId as String
                                    }

                                }
                                totalCancellationCharges =
                                    response.data?.cancelOrder?.totalCancelPrice as String
                            } else {
                                totalCancellationCharges = "0"
                            }


                            MyApplication.createOrdersInput.deliveryCharges =
                                response.data?.deliveryFee + ""
                            MyApplication.createOrdersInput.totalOrderPrice = orderPrice
                            MyApplication.createOrdersInput.orderPrice = payableAmount
                            MyApplication.createOrdersInput.offerPrice =
                                response.data?.discountPrice + ""
                            MyApplication.createOrdersInput.securityFee =
                                response.data?.securityFee + ""
                            MyApplication.createOrdersInput.pendingCCharges =
                                totalCancellationCharges
                            MyApplication.createOrdersInput.cancelOrderIds = cancelOrderIds
                            //(activity as CreateOrderActivty).
                            setPrice(
                                payableAmount,
                                response.data?.deliveryFee + "",
                                response.data?.weightFee + "",
                                response.data?.securityFee + "",
                                response.data?.discountPrice + "",
                                totalCancellationCharges, "second",
                                response.data?.deliveryTypeCharges + ""
                            )
                        }
                        else -> message?.let {
                            // activityCreateOrderBinding.txtDelCharges.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })
        fragmentCreateOrdersSecondBinding.edtParcelValue.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                calculatePrice()
            }
        })

        fragmentCreateOrdersSecondBinding.edtParcelValue.setOnEditorActionListener { v, actionId, event->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // doSomething()
                fragmentCreateOrdersSecondBinding.edtParcelValue.clearFocus()
                val imm =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                MyApplication.createOrdersInput.parcelValue =
                    fragmentCreateOrdersSecondBinding.edtParcelValue.text.toString()
                fragmentCreateOrdersSecondBinding.edtPromoCode.setText("")
                MyApplication.createOrdersInput.promoCode = ""
                fragmentCreateOrdersSecondBinding.btnApplyCoupon.setText("Apply Now")
                discountApplied = ""
                GlobalConstants.discountApplied = ""
                calculatePrice()
                /* MyApplication.createOrdersInput.pickupAddress?.phoneNumber =
                     fragmentCreateOrdersSecondBinding.edtDelMob.text.toString()*/
                //}
                true
            } else {
                false
            }
        }
        fragmentCreateOrdersSecondBinding.edtItemName.setOnEditorActionListener { v, actionId, event->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // doSomething()
                fragmentCreateOrdersSecondBinding.edtItemName.clearFocus()
                val imm =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                MyApplication.createOrdersInput.itemName =
                    fragmentCreateOrdersSecondBinding.edtItemName.text.toString()
                /* MyApplication.createOrdersInput.pickupAddress?.phoneNumber =
                     fragmentCreateOrdersSecondBinding.edtDelMob.text.toString()*/
                //}
                true
            } else {
                false
            }
        }


        fragmentCreateOrdersSecondBinding.chkLoyalty.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
                if (TextUtils.isEmpty(orderId) || orderId.equals("null")) {
                    if (TextUtils.isEmpty(GlobalConstants.discountApplied/*discountApplied*/)) {
                        if (fragmentCreateOrdersSecondBinding.chkLoyalty!!.isChecked) {
                            GlobalConstants.isUsableLoyalty = true
                            lPointsTotalUsed = loyaltyPointsData.usabelPoints!!
                            lPointsTotalValue =
                                (loyaltyPointsData.usabelPoints?.toDouble()?.times(loyaltyPointsData.pricePerPoint!!.toDouble())).toString()
                            MyApplication.createOrdersInput.usedLPoints = lPointsTotalUsed
                            MyApplication.createOrdersInput.lPointsPrice = lPointsTotalValue
                            calculatePrice()
                        } else {
                            GlobalConstants.isUsableLoyalty = false
                            lPointsTotalUsed = "0"
                            lPointsTotalValue = "0"
                            MyApplication.createOrdersInput.usedLPoints = lPointsTotalUsed
                            MyApplication.createOrdersInput.lPointsPrice = lPointsTotalValue
                            calculatePrice()
                        }
                    } else {
                        showToastError("You can not use loyalty point as you already applied promo code. If you want to use please remove applied promo code")
                        fragmentCreateOrdersSecondBinding.chkLoyalty.setChecked(false)
                    }
                }

            }
        })

        orderViewModel.applyCouponRes().observe(this,
            Observer<ApplyCouponResponse>
            { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            discountApplied = response.data?.coupanDiscount.toString()
                            GlobalConstants.discountApplied = discountApplied
                            // fragmentCreateOrdersSecondBinding.rlOriginalPrice.visibility = View.VISIBLE
                            // fragmentCreateOrdersSecondBinding.txtTotalAmount.setText(deliveryCharges + "₹")
                            // fragmentCreateOrdersSecondBinding.txtDelCharges.setText(response.data?.payableAmount + "₹")
                            fragmentCreateOrdersSecondBinding.btnApplyCoupon.setText("Remove")
                            fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = false
                            MyApplication.createOrdersInput.promoCode =
                                fragmentCreateOrdersSecondBinding.edtPromoCode.text.toString()

                            calculatePrice()
                        }
                        else -> message?.let {
                            // activityCreateOrderBinding.txtDelCharges.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        orderViewModel.removeCouponRes().observe(this,
            Observer<CommonModel>
            { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            discountApplied = ""
                            GlobalConstants.discountApplied = ""
                            //fragmentCreateOrdersSecondBinding.rlOriginalPrice.visibility = View.GONE
                            // fragmentCreateOrdersSecondBinding.txtTotalAmount.setText("")
                            // fragmentCreateOrdersSecondBinding.txtDelCharges.setText(deliveryCharges + "₹")
                            fragmentCreateOrdersSecondBinding.btnApplyCoupon.setText("Apply")
                            fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = true
                            fragmentCreateOrdersSecondBinding.edtPromoCode.setText("")
                            MyApplication.createOrdersInput.promoCode = ""

                            calculatePrice()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


        fragmentCreateOrdersSecondBinding.rgCashCollectType.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId->
                val radio : RadioButton = view!!.findViewById(checkedId)

                if (radio == rdPickup) {
                    MyApplication.createOrdersInput.fareCollected = "Pickup Location"
                } else {
                    MyApplication.createOrdersInput.fareCollected = "Drop Location"
                }
            })

        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgParcelValue" -> {
                        UtilsFunctions.showToastInfo("Info Regarding Insurance/Parcel Value")
                    }
                    "imgPaymentInfo" -> {
                        UtilsFunctions.showToastInfo(
                            "Amount for your order is to be paid to the Rider directly by means of Cash or Mobile wallets\n" +
                                    "available with the Rider."
                        )
                    }
                    "btnProceed" -> {
                        if (TextUtils.isEmpty(
                                MyApplication.createOrdersInput.itemName
                            ) || TextUtils.isEmpty(MyApplication.createOrdersInput.parcelValue)
                            || TextUtils.isEmpty(MyApplication.createOrdersInput.paymentType)
                            || TextUtils.isEmpty(MyApplication.createOrdersInput.fareCollected)
                        ) {
                            showToastError("Please Select all the values")
                        } else {
                            MyApplication.createOrdersInput.usedLPoints = lPointsTotalUsed
                            MyApplication.createOrdersInput.lPointsPrice = lPointsTotalValue
                            (activity as CreateOrderActivty).callSecondFragment(3)

                        }

                    }
                    "imgPriceDetails" -> {
                        if (fragmentCreateOrdersSecondBinding.llPriceDetail.visibility == View.GONE) {
                            fragmentCreateOrdersSecondBinding.imgPriceDetails.setImageResource(
                                R.drawable.ic_down_arrow
                            )
                            fragmentCreateOrdersSecondBinding.llPriceDetail.visibility =
                                View.VISIBLE
                            fragmentCreateOrdersSecondBinding.llPriceDetail.bringToFront()

                        } else {
                            fragmentCreateOrdersSecondBinding.imgPriceDetails.setImageResource(
                                R.drawable.ic_up_arrow
                            )
                            fragmentCreateOrdersSecondBinding.llPriceDetail.visibility = View.GONE
                        }
                        ///showToastSuccess("Show Details")
                        // showCancelOrdersDialog()
                    }
                    "toolbar" -> {
                        (activity as LandingActivty).openCloseDrawer()
                    }
                    "txtOnline" -> {
                        paymentType = "1"
                        fragmentCreateOrdersSecondBinding.txtOnline.setAlpha(1f)
                        fragmentCreateOrdersSecondBinding.txtCash.setAlpha(0.5f)

                        fragmentCreateOrdersSecondBinding.imgOnline.visibility = View.VISIBLE
                        fragmentCreateOrdersSecondBinding.imgCash.visibility = View.INVISIBLE
                        MyApplication.createOrdersInput.paymentType = paymentType
                    }
                    "txtCash" -> {
                        paymentType = "2"
                        fragmentCreateOrdersSecondBinding.txtCash.setAlpha(1f)
                        fragmentCreateOrdersSecondBinding.txtOnline.setAlpha(0.5f)

                        fragmentCreateOrdersSecondBinding.imgCash.visibility = View.VISIBLE
                        fragmentCreateOrdersSecondBinding.imgOnline.visibility = View.INVISIBLE
                        MyApplication.createOrdersInput.paymentType = paymentType
                    }
                    "btnApplyCoupon" -> {
                        if (fragmentCreateOrdersSecondBinding.btnApplyCoupon.text.toString().equals(
                                "Remove"
                            )
                        ) {
                            val mJsonObject = JsonObject()
                            mJsonObject.addProperty(
                                "promoCode",
                                fragmentCreateOrdersSecondBinding.edtPromoCode.text.toString()
                            )
                            mJsonObject.addProperty(
                                "discountPrice",
                                payableAmount
                            )
                            mJsonObject.addProperty(
                                "companyId",
                                "25cbf58b-46ba-4ba2-b25d-8f8f653e9f11"
                            )

                            if (UtilsFunctions.isNetworkConnected()) {
                                orderViewModel.removeCoupon(mJsonObject)
                            }
                        } else {
                            if (GlobalConstants.isUsableLoyalty) {
                                showToastError("You can not apply promocode as you already use loyalty points. If you want to use please remove used loyalty points")
                            } else {
                                if (!TextUtils.isEmpty(fragmentCreateOrdersSecondBinding.edtPromoCode.text.toString())) {
                                    val mJsonObject = JsonObject()
                                    mJsonObject.addProperty(
                                        "promoCode",
                                        fragmentCreateOrdersSecondBinding.edtPromoCode.text.toString()
                                    )
                                    mJsonObject.addProperty(
                                        "totalPrice",
                                        payableAmount
                                    )
                                    mJsonObject.addProperty(
                                        "companyId",
                                        "25cbf58b-46ba-4ba2-b25d-8f8f653e9f11"
                                    )
                                    //mJsonObject.addProperty("totalPrice", deliveryCharges)
                                    if (UtilsFunctions.isNetworkConnected()) {
                                        orderViewModel.applyCoupon(mJsonObject)
                                    }
                                }
                            }

                        }

                    }
                }
            })
        )

    }

    private fun preFillData() {
        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.itemName)) {
            fragmentCreateOrdersSecondBinding.edtItemName.setText(MyApplication.createOrdersInput.itemName)
        }
        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.parcelValue)) {
            fragmentCreateOrdersSecondBinding.edtParcelValue.setText(MyApplication.createOrdersInput.parcelValue)
        }
        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.promoCode)) {
            fragmentCreateOrdersSecondBinding.edtPromoCode.setText(MyApplication.createOrdersInput.promoCode)
            fragmentCreateOrdersSecondBinding.btnApplyCoupon.setText("Remove")
            fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = false
        }


        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.paymentType)) {
            if (MyApplication.createOrdersInput.paymentType.equals("2")) {
                paymentType = "2"
                fragmentCreateOrdersSecondBinding.txtCash.setAlpha(1f)
                fragmentCreateOrdersSecondBinding.txtOnline.setAlpha(0.5f)

                fragmentCreateOrdersSecondBinding.imgCash.visibility = View.VISIBLE
                fragmentCreateOrdersSecondBinding.imgOnline.visibility = View.INVISIBLE
                MyApplication.createOrdersInput.paymentType = paymentType
            } else {
                paymentType = "1"
                fragmentCreateOrdersSecondBinding.txtOnline.setAlpha(1f)
                fragmentCreateOrdersSecondBinding.txtCash.setAlpha(0.5f)

                fragmentCreateOrdersSecondBinding.imgOnline.visibility = View.VISIBLE
                fragmentCreateOrdersSecondBinding.imgCash.visibility = View.INVISIBLE
                MyApplication.createOrdersInput.paymentType = paymentType
            }
        } else {
            MyApplication.createOrdersInput.paymentType = paymentType
        }
        if (MyApplication.createOrdersInput.fareCollected.equals("Pickup Location")) {
            fragmentCreateOrdersSecondBinding.rgCashCollectType.check(R.id.rdPickup)
        } else {
            fragmentCreateOrdersSecondBinding.rgCashCollectType.check(R.id.rdDelivery)
        }
//radioGroup.check(R.id.rdPickup)
        //  radioGroup.check(R.id.rdDelivery)
        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.usedLPoints) && !MyApplication.createOrdersInput.usedLPoints.equals(
                "0"
            )
        ) {
            fragmentCreateOrdersSecondBinding.rlLoyaltyPoints.visibility =
                View.VISIBLE
            GlobalConstants.isUsableLoyalty = true
            fragmentCreateOrdersSecondBinding.txtLoyaltyPoints.visibility =
                View.GONE
            fragmentCreateOrdersSecondBinding.chkLoyalty.setChecked(true)
            val isUpdated = true
            if (!TextUtils.isEmpty(MyApplication.createOrdersInput.orderId) && !MyApplication.createOrdersInput.orderId.equals(
                    "null"
                )
            ) {
                fragmentCreateOrdersSecondBinding.chkLoyalty.text =
                    MyApplication.createOrdersInput.usedLPoints + " Loyalty Points Used"
                fragmentCreateOrdersSecondBinding.chkLoyalty.isEnabled = false
                fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = false
            } else {
            }

        } else {
            fragmentCreateOrdersSecondBinding.rlLoyaltyPoints.visibility =
                View.GONE
        }

        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.orderId)) {
            fragmentCreateOrdersSecondBinding.edtPromoCode.isEnabled = false
            fragmentCreateOrdersSecondBinding.edtPromoCode.setFocusable(false)
            fragmentCreateOrdersSecondBinding.txtLoyaltyPoints.visibility = View.GONE
            //fragmentCreateOrdersSecondBinding.edtPromoCode.isEditable = false
            fragmentCreateOrdersSecondBinding.btnApplyCoupon.isEnabled = false
            fragmentCreateOrdersSecondBinding.chkLoyalty.isEnabled = false
        }
        calculatePrice()
    }

    fun calculatePrice() {
        if (/*!TextUtils.isEmpty(vehicleId) && */!TextUtils.isEmpty(MyApplication.createOrdersInput.deliveryOption) && !TextUtils.isEmpty(
                MyApplication.createOrdersInput.distance
            ) && !TextUtils.isEmpty(
                MyApplication.createOrdersInput.weight
            ) /*&& !TextUtils.isEmpty(activityCreateOrderBinding.edtParcelValue.text.toString())*/
        ) {
            if (MyApplication.createOrdersInput.orderId.equals("null") || TextUtils.isEmpty(
                    MyApplication.createOrdersInput.orderId
                )
            ) {
                MyApplication.createOrdersInput.orderId = ""
            }
            if (MyApplication.createOrdersInput.parcelValue.equals("null") || TextUtils.isEmpty(
                    MyApplication.createOrdersInput.parcelValue
                )
            ) {
                MyApplication.createOrdersInput.parcelValue = ""
            }
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "parcelValue",
                MyApplication.createOrdersInput.parcelValue/* activityCreateOrderBinding.edtParcelValue.text.toString()*/
            )
            mJsonObject.addProperty("orderId", MyApplication.createOrdersInput.orderId)
            mJsonObject.addProperty("vehicleId", "9c9d2c0e-02d6-4095-a0b4-b267b736dd65")
            /*mJsonObject.addProperty("deliveryId", GlobalConstants.DELIVERY_TYPE)
            mJsonObject.addProperty("weightId", GlobalConstants.WEIGHT_ID)
            mJsonObject.addProperty("distance", GlobalConstants.DISTANCE)*/
            mJsonObject.addProperty("deliveryId", MyApplication.createOrdersInput.deliveryOption)
            mJsonObject.addProperty("weightId", MyApplication.createOrdersInput.weight)
            mJsonObject.addProperty("distance", MyApplication.createOrdersInput.distance)
            mJsonObject.addProperty("isUseLoyality", GlobalConstants.isUsableLoyalty)

            mJsonObject.addProperty(
                "discount",
                GlobalConstants.discountApplied
            )
            if (UtilsFunctions.isNetworkConnected()) {
                //  (activity as CreateOrderActivty).callPriceCalculationApi(mJsonObject)
                orderViewModel.calculatePrice(mJsonObject)
                //  showToastSuccess("api call")
            }
        }
    }

    fun setPrice(
        payableAmount : String,
        deliveryFee : String,
        weightFee : String,
        securityFee : String,
        couponDeduction : String,
        totalCancellationCharges : String,
        from : String, deliveryTypeCharges : String?
    ) {
        /*if (from.equals("first")) {
            fragmentCreateOrdersSecondBinding.viewLine1.setAlpha(1f)
            fragmentCreateOrdersSecondBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
        } else {
            fragmentCreateOrdersSecondBinding.viewLine1.setAlpha(1f)
            fragmentCreateOrdersSecondBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
            fragmentCreateOrdersSecondBinding.viewLine2.setAlpha(1f)
            fragmentCreateOrdersSecondBinding.viewLine2.background =
                ColorDrawable(Color.parseColor("#ffffff"))

        }*/

        if (!deliveryFee.equals("0") && !deliveryFee.equals("")) {
            fragmentCreateOrdersSecondBinding.txtDelivery.text = "₹ " + deliveryFee
            fragmentCreateOrdersSecondBinding.view1.visibility = View.VISIBLE
            fragmentCreateOrdersSecondBinding.rlDelivery.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlDelivery.visibility = View.GONE
            fragmentCreateOrdersSecondBinding.view1.visibility = View.GONE
        }
        if (!weightFee.equals("0") && !weightFee.equals("")) {
            fragmentCreateOrdersSecondBinding.txtWeight.text = "₹ " + weightFee
            fragmentCreateOrdersSecondBinding.view2.visibility = View.VISIBLE
            fragmentCreateOrdersSecondBinding.rlWeight.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlWeight.visibility = View.GONE
            fragmentCreateOrdersSecondBinding.view2.visibility = View.GONE
        }
        if (!securityFee.equals("0") && !securityFee.equals("") && !securityFee.equals("null")) {
            fragmentCreateOrdersSecondBinding.txtSecurity.text = "₹ " + securityFee
            fragmentCreateOrdersSecondBinding.view3.visibility = View.VISIBLE
            fragmentCreateOrdersSecondBinding.rlSecurity.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlSecurity.visibility = View.GONE
            fragmentCreateOrdersSecondBinding.view3.visibility = View.GONE
        }

        if (!totalCancellationCharges.equals("0") && !totalCancellationCharges.equals("")) {
            fragmentCreateOrdersSecondBinding.txtCancellation.text = "₹ " + totalCancellationCharges
            fragmentCreateOrdersSecondBinding.view4.visibility = View.VISIBLE
            fragmentCreateOrdersSecondBinding.rlCancel.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlCancel.visibility = View.GONE
            fragmentCreateOrdersSecondBinding.view4.visibility = View.GONE
        }
        if (!couponDeduction.equals("0") && !couponDeduction.equals("")) {
            fragmentCreateOrdersSecondBinding.txtCouponDeduction.text = "₹ " + couponDeduction
            fragmentCreateOrdersSecondBinding.rlCouponDeduction.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlCouponDeduction.visibility = View.GONE
        }

        if (!deliveryTypeCharges.equals("0") && !deliveryTypeCharges.equals("")) {
            fragmentCreateOrdersSecondBinding.view5.visibility = View.VISIBLE
            fragmentCreateOrdersSecondBinding.txtDeliveryTypeCharges.text =
                "₹ " + deliveryTypeCharges
            fragmentCreateOrdersSecondBinding.rlDeliveryTypeCharges.visibility = View.VISIBLE
        } else {
            fragmentCreateOrdersSecondBinding.rlDeliveryTypeCharges.visibility = View.GONE
            fragmentCreateOrdersSecondBinding.view5.visibility = View.GONE
        }



        fragmentCreateOrdersSecondBinding.txtFare.text = "₹ " + payableAmount
    }
}