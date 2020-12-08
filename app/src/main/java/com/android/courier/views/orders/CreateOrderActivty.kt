package com.android.courier.views.orders

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.courier.R
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityCreateOrderBinding
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.home.fragments.HomeFragment
import com.android.courier.views.orders.fragments.CreateOrderFirstFragment
import com.android.courier.views.orders.fragments.CreateOrderPreviewFragment
import com.android.courier.views.orders.fragments.CreateOrderSecondFragment
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
    var fragment = 1
    private var confirmationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    override fun getLayoutId() : Int {
        return R.layout.activity_create_order
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        // Initialize the SDK
        GlobalConstants.discountApplied = ""
        GlobalConstants.isUsableLoyalty = false
        MyApplication.createOrdersInput = CreateOrdersInput()
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
        orderId = intent.extras?.get("id").toString()
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
        if (TextUtils.isEmpty(orderId) || orderId.equals("null")) {
            val fragment = CreateOrderFirstFragment()
            this.callFragments(
                fragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
        } else {
            if (!TextUtils.isEmpty(orderId) && !orderId.equals("null")) {
                if (UtilsFunctions.isNetworkConnected()) {
                    startProgressDialog()
                    MyApplication.createOrdersInput.orderId = orderId
                    orderViewModel.orderDetail(orderId)
                    //orderViewModel.cancelReason(orderId)
                }
            }
        }


        orderViewModel.orderDetailRes().observe(this,
            Observer<OrdersDetailResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            activityCreateOrderBinding.orderDetailModel = response.data

                            MyApplication.createOrdersInput.distance = response.data?.distance
                            MyApplication.createOrdersInput.itemName = response.data?.itemName
                            val pickupAdd = CreateOrdersInput.PickupAddress()
                            pickupAdd.address = response.data?.pickupAddress?.address as String
                            pickupAdd.id = 0
                            pickupAdd.lat = response.data?.pickupAddress?.lat as String
                            pickupAdd.long = response.data?.pickupAddress?.long as String
                            pickupAdd.date = response.data?.pickupAddress?.date as String
                            pickupAdd.time = response.data?.pickupAddress?.time as String
                            pickupAdd.isComplete = "false"
                            pickupAdd.phoneNumber =
                                response.data?.pickupAddress?.phoneNumber as String
                            MyApplication.createOrdersInput.pickupAddress = pickupAdd
                            /*  pickupMobile = response.data?.pickupAddress?.phoneNumber as String
                              pickupDate = response.data?.pickupAddress?.date as String
                              pickupAddress = response.data?.pickupAddress?.address as String
                              pickLat = response.data?.pickupAddress?.lat as String
                              pickLong = response.data?.pickupAddress?.long as String
                              pickTime = response.data?.pickupAddress?.time as String*/
                            /*activityCreateOrderBinding.edtPickDateTime.setText(
                                response.data?.pickupAddress?.date as String + " " + response.data?.pickupAddress?.time as String
                            )*/

                            MyApplication.createOrdersInput.usedLPoints =
                                response.data?.usedLPoints/*lPointsTotalUsed*/
                            if (!TextUtils.isEmpty(response.data?.usedLPoints) && !response.data?.usedLPoints.equals(
                                    "0"
                                )
                            ) {
                                /* activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                     View.VISIBLE*/
                                GlobalConstants.isUsableLoyalty = true
                                /* activityCreateOrderBinding.txtLoyaltyPoints.visibility =
                                     View.GONE
                                 activityCreateOrderBinding.chkLoyalty.setChecked(true)
                                 activityCreateOrderBinding.chkLoyalty.text =
                                     response.data?.usedLPoints + " Loyalty Points Used"
                                 activityCreateOrderBinding.chkLoyalty.isEnabled = false*/
                            } else {
                                /* activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                     View.GONE*/
                                GlobalConstants.isUsableLoyalty = false
                            }

                            if (response.data?.deliveryAddress?.size!! > 0) {
                                val deliveryAddressList =
                                    ArrayList<CreateOrdersInput.PickupAddress>()
                                var address = CreateOrdersInput.PickupAddress()
                                /* address.address =
                                     response.data?.deliveryAddress!![0].address as String
                                 address.lat = response.data?.deliveryAddress!![0].lat as String
                                 address.id = 0
                                 address.long = response.data?.deliveryAddress!![0].long as String
                                 address.isComplete = "false"
                                 address.phoneNumber =
                                     response.data?.deliveryAddress!![0].phoneNumber as String
                                 deliveryAddressList.add(address)
                                 MyApplication.createOrdersInput.deliveryAddress =
                                     deliveryAddressList*/
                                /* delMobile =
                                     response.data?.deliveryAddress!![0].phoneNumber as String
                                 delAddress = response.data?.deliveryAddress!![0].address as String
                                 delLat = response.data?.deliveryAddress!![0].lat as String
                                 delLong = response.data?.deliveryAddress!![0].long as String
                                 delTime = response.data?.deliveryAddress!![0].time as String*/
/*
                                activityCreateOrderBinding.edtDelAddress.setText(
                                    response.data?.deliveryAddress!![0].address as String
                                )
                                activityCreateOrderBinding.edtDelMob.setText(response.data?.deliveryAddress!![0].phoneNumber as String)
                                activityCreateOrderBinding.edtDelDateTime.setText(
                                    response.data?.deliveryAddress!![0].date as String + " " + response.data?.deliveryAddress!![0].time as String
                                )*/

                                for (index in 0 until response.data?.deliveryAddress?.size!!) {
                                    //addAddressView(response.data?.deliveryAddress!![index])
                                    var address = CreateOrdersInput.PickupAddress()
                                    address.address =
                                        response.data?.deliveryAddress!![0].address as String
                                    address.lat = response.data?.deliveryAddress!![0].lat as String
                                    address.id = 0
                                    address.long =
                                        response.data?.deliveryAddress!![0].long as String
                                    address.isComplete = "false"
                                    address.phoneNumber =
                                        response.data?.deliveryAddress!![0].phoneNumber as String
                                    deliveryAddressList.add(address)
                                    MyApplication.createOrdersInput.deliveryAddress =
                                        deliveryAddressList
                                }
                            }

                            MyApplication.createOrdersInput.deliveryCharges =
                                response.data?.deliveryCharges as String
                            MyApplication.createOrdersInput.totalOrderPrice =
                                response.data?.totalOrderPrice as String
                            /*for (vehicle in vehicleList) {
                                vehicle.selected = "false"
                                if (vehicle.id.equals(response.data?.vehicleType)) {
                                    vehicle.selected = "true"
                                    vehicleId = vehicle.id.toString()
                                    vehicleValue = vehicle.name!!
                                    createOrderInput.vehicleType = vehicleId
                                }
                            }
                            vehiclesAdapter?.notifyDataSetChanged()*/
                            MyApplication.createOrdersInput.weight = response.data?.weightId
                            /* for (weight in weightList) {
                                 weight.selected = "false"
                                 if (weight.id.equals(response.data?.weightId)) {
                                     weight.selected = "true"
                                     weightId = weight.id.toString()
                                     weighValue = weight.name!!
                                     createOrderInput.weight = weightId
                                 }
                             }
                             weightAdapter?.notifyDataSetChanged()*/
                            MyApplication.createOrdersInput.deliveryOption =
                                response.data?.deliveryOption
                            /*for (deliveryType in deliveryTypeList) {
                                deliveryType.selected = "false"
                                if (deliveryType.id.equals(response.data?.deliveryOption)) {
                                    deliveryType.selected = "true"
                                    deliveryTypeId = deliveryType.id.toString()
                                    deliveryValue = deliveryType.title!!
                                    createOrderInput.deliveryOption = deliveryTypeId
                                }
                            }
                            deliveryTypeAdapter?.notifyDataSetChanged()*/

                            MyApplication.createOrdersInput.notifyRecipient = "false"
                            MyApplication.createOrdersInput.notifyMe = "false"
                            /*  if (response.data?.notifyRecipient.equals("true")) {
                                 activityCreateOrderBinding.chkNotificationMsg.setChecked(true)
                                 // true
                                 isNotifyMe = true
                             } else {
                                 activityCreateOrderBinding.chkNotificationMsg.setChecked(false)
                                 isNotifyMe = false
                             }

                             if (response.data?.notifyRecipient.equals("true")) {
                                 activityCreateOrderBinding.chkNotificationReceipentMsg.setChecked(
                                     true
                                 )

                                 isNotifyRecipent = true
                             } else {
                                 activityCreateOrderBinding.chkNotificationReceipentMsg.setChecked(
                                     false
                                 )
                                 //false
                                 isNotifyRecipent = false
                             }*/



                            if (!TextUtils.isEmpty(response.data?.promoCode)) {
                                /* activityCreateOrderBinding.edtPromoCode.isEnabled = false
                                 activityCreateOrderBinding.txtApply.text = "Remove"
                                 activityCreateOrderBinding.txtTotalAmount.text =
                                     response.data?.totalOrderPrice + "₹"
                                 activityCreateOrderBinding.rlOriginalPrice.visibility = View.VISIBLE*/
                                GlobalConstants.discountApplied = response.data?.discountPercent!!
                                MyApplication.createOrdersInput.promoCode = response.data?.promoCode

                            } else {
                                /*activityCreateOrderBinding.edtPromoCode.isEnabled = true
                                activityCreateOrderBinding.txtApply.text = "Apply"
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.GONE*/
                                GlobalConstants.discountApplied = ""
                                MyApplication.createOrdersInput.promoCode = ""
                            }

                            MyApplication.createOrdersInput.fareCollected =
                                response.data?.fareCollected as String
                            /* if (fareCollected.equals("Pickup end")) {
                                 activityCreateOrderBinding.rdPickup.setChecked(true)
                             } else {
                                 activityCreateOrderBinding.rdDelivery.setChecked(true)
                             }*/

                            MyApplication.createOrdersInput.paymentType = response.data?.paymentType
                            /*if (response.data?.paymentType.equals("2")) {
                                paymentType = "2"
                                activityCreateOrderBinding.rdCash.setChecked(true)
                                activityCreateOrderBinding.rdCash.setTextColor(getColor(R.color.colorPrimary))
                                activityCreateOrderBinding.rdNonCash.setTextColor(getColor(R.color.colorBlack))
                            } else {
                                paymentType = "1"
                                activityCreateOrderBinding.rdNonCash.setChecked(true)
                                activityCreateOrderBinding.rdNonCash.setTextColor(getColor(R.color.colorPrimary))
                                activityCreateOrderBinding.rdCash.setTextColor(getColor(R.color.colorBlack))
                            }*/
                            MyApplication.createOrdersInput.parcelValue = response.data?.parcelValue
                            // activityCreateOrderBinding.edtParcelValue.setText(response.data?.parcelValue as String)
                            if (TextUtils.isEmpty(response.data?.pendingCCharges) || response.data?.pendingCCharges.equals(
                                    "0"
                                )
                            ) {
                                activityCreateOrderBinding.cancelLayout.visibility = View.GONE
                            } else {
                                MyApplication.createOrdersInput.cancelOrderIds =
                                    response.data?.cancelOrderId as String
                                /* activityCreateOrderBinding.cancelLayout.visibility = View.VISIBLE
                                 activityCreateOrderBinding.txtCancellationCharges.text =
                                     response.data?.pendingCCharges + "₹" + " View Details"
                                 cancellationCharges =
                                     response.data?.pendingCCharges as String
                                 //showToastSuccess("call api")
                                 calculatePrice()*/

                            }
                            val fragment = CreateOrderFirstFragment()
                            this.callFragments(
                                fragment,
                                supportFragmentManager,
                                false,
                                "send_data",
                                ""
                            )
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })









        orderId = intent.extras?.get("id").toString()
        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "txtDiscard" -> {
                        confirmationDialog = mDialogClass.setDefaultDialog(
                            this,
                            this,
                            "discard",
                            "Do you really want to discard this order?"
                        )
                        confirmationDialog?.show()
                    }
                    "toolbar" -> {
                        onBackPressed()
                    }
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
                    "btnProceed" -> {
                        if (TextUtils.isEmpty(MyApplication.createOrdersInput.pickupAddress?.address) || TextUtils.isEmpty(
                                MyApplication.createOrdersInput.pickupAddress?.date
                            ) || TextUtils.isEmpty(
                                MyApplication.createOrdersInput.pickupAddress?.phoneNumber
                            ) || TextUtils.isEmpty(MyApplication.createOrdersInput.pickupAddress?.time) || TextUtils.isEmpty(
                                MyApplication.createOrdersInput.deliveryOption
                            ) || TextUtils.isEmpty(MyApplication.createOrdersInput.weightId)
                            || TextUtils.isEmpty(MyApplication.createOrdersInput.distance) || MyApplication.createOrdersInput.distance.equals(
                                "0"
                            )
                        ) {
                            showToastError("Please Select all the vlaues")
                        } else {
                            val fragment = CreateOrderSecondFragment()
                            this.callFragments(
                                fragment,
                                supportFragmentManager,
                                false,
                                "send_data",
                                ""
                            )
                        }

                    }
                }
            })
        )

    }

    override fun onDialogConfirmAction(mView : View?, mKey : String?) {
        when (mKey) {
            "discard" -> {
                //  clearAddressData()
                confirmationDialog?.dismiss()
                finish()
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
        totalCancellationCharges : String,
        from : String
    ) {
        if (from.equals("first")) {
            activityCreateOrderBinding.viewLine1.setAlpha(1f)
            activityCreateOrderBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
        } else {
            activityCreateOrderBinding.viewLine1.setAlpha(1f)
            activityCreateOrderBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
            activityCreateOrderBinding.viewLine2.setAlpha(1f)
            activityCreateOrderBinding.viewLine2.background =
                ColorDrawable(Color.parseColor("#ffffff"))

        }

        if (!deliveryFee.equals("0") && !deliveryFee.equals("")) {
            activityCreateOrderBinding.txtDelivery.text = "₹ " + deliveryFee
            activityCreateOrderBinding.view1.visibility = View.VISIBLE
            activityCreateOrderBinding.rlDelivery.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.rlDelivery.visibility = View.GONE
            activityCreateOrderBinding.view1.visibility = View.GONE
        }
        if (!weightFee.equals("0") && !weightFee.equals("")) {
            activityCreateOrderBinding.txtWeight.text = "₹ " + weightFee
            activityCreateOrderBinding.view2.visibility = View.VISIBLE
            activityCreateOrderBinding.rlWeight.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.rlWeight.visibility = View.GONE
            activityCreateOrderBinding.view2.visibility = View.GONE
        }
        if (!securityFee.equals("0") && !securityFee.equals("")) {
            activityCreateOrderBinding.txtSecurity.text = "₹ " + securityFee
            activityCreateOrderBinding.view3.visibility = View.VISIBLE
            activityCreateOrderBinding.rlSecurity.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.rlSecurity.visibility = View.GONE
            activityCreateOrderBinding.view3.visibility = View.GONE
        }

        if (!totalCancellationCharges.equals("0") && !totalCancellationCharges.equals("")) {
            activityCreateOrderBinding.txtCancellation.text = "₹ " + totalCancellationCharges
            activityCreateOrderBinding.view4.visibility = View.VISIBLE
            activityCreateOrderBinding.rlCancel.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.rlCancel.visibility = View.GONE
            activityCreateOrderBinding.view4.visibility = View.GONE
        }
        if (!couponDeduction.equals("0") && !couponDeduction.equals("")) {
            activityCreateOrderBinding.txtCouponDeduction.text = "₹ " + couponDeduction
            activityCreateOrderBinding.rlCouponDeduction.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.rlCouponDeduction.visibility = View.GONE
        }

        activityCreateOrderBinding.txtFare.text = "₹ " + payableAmount
    }

    fun setViewLine() {
        activityCreateOrderBinding.viewLine1.setAlpha(1f)
        activityCreateOrderBinding.viewLine1.background =
            ColorDrawable(Color.parseColor("#ffffff"))
    }

    fun callSecondFragment(fragmenNumber : Int) {
        fragment = fragmenNumber
        activityCreateOrderBinding.viewLine1.setAlpha(1f)
        activityCreateOrderBinding.viewLine1.background =
            ColorDrawable(Color.parseColor("#ffffff"))
        if (fragmenNumber == 2) {
            activityCreateOrderBinding.imgToolbarText.setText("Add more Details")
            activityCreateOrderBinding.imgTwo.setImageResource(R.drawable.ic_tick)
            val fragment = CreateOrderSecondFragment()
            this.callFragments(
                fragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
        } else {
            activityCreateOrderBinding.imgToolbarText.setText("Review Order")
            activityCreateOrderBinding.viewLine1.setAlpha(1f)
            activityCreateOrderBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
            activityCreateOrderBinding.viewLine2.setAlpha(1f)
            activityCreateOrderBinding.viewLine2.background =
                ColorDrawable(Color.parseColor("#ffffff"))
            activityCreateOrderBinding.imgThree.setImageResource(R.drawable.ic_tick)
            val fragment = CreateOrderPreviewFragment()
            this.callFragments(
                fragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (fragment == 1) {
            finish()
        } else if (fragment == 2) {
            activityCreateOrderBinding.imgToolbarText.text =
                getString(R.string.create_order)
            activityCreateOrderBinding.viewLine1.setAlpha(0.5f)
            activityCreateOrderBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#D0D0D0"))
            activityCreateOrderBinding.imgTwo.setImageResource(R.drawable.ic_stepper_unselected)
            activityCreateOrderBinding.imgThree.setImageResource(R.drawable.ic_stepper_unselected)
            val createOrderFirstFragment = CreateOrderFirstFragment()
            this.callFragments(
                createOrderFirstFragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
            fragment = 1
        } else {
            activityCreateOrderBinding.imgToolbarText.setText("Add More Details")
            activityCreateOrderBinding.viewLine2.setAlpha(0.5f)
            activityCreateOrderBinding.viewLine2.background =
                ColorDrawable(Color.parseColor("#D0D0D0"))
            val createOrderFirstFragment = CreateOrderSecondFragment()
            activityCreateOrderBinding.imgTwo.setImageResource(R.drawable.ic_tick)
            activityCreateOrderBinding.imgThree.setImageResource(R.drawable.ic_stepper_unselected)
            this.callFragments(
                createOrderFirstFragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
            fragment = 2
        }

    }

}
