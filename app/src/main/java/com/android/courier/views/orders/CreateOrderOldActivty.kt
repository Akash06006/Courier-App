package com.android.courier.views.orders

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.orders.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.android.courier.common.UtilsFunctions
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.ActivityCreateOrderOldBinding
import com.android.courier.model.CommonModel
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.contacts.ContactListActivity
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_order.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateOrderOldActivty : BaseActivity(), DialogssInterface {
    private lateinit var loyaltyPointsData : LoyalityData
    private var isUsableLoyalty = false
    private lateinit var cancelOrderDetail : CancelOrder
    private lateinit var activityCreateOrderBinding : ActivityCreateOrderOldBinding
    private lateinit var orderViewModel : OrderViewModel
    var vehicleList = ArrayList<ListsResponse.VehicleData>()
    var bannersList = ArrayList<ListsResponse.BannersData>()

    var addressList = ArrayList<CreateOrdersInput.PickupAddress>()
    var weightList = ArrayList<ListsResponse.WeightData>()
    var vehiclesAdapter : VehiclesListAdapter? = null
    var weightAdapter : WeightListAdapter? = null
    var deliveryTypeList = ArrayList<ListsResponse.DeliveryOptionData>()
    var deliveryTypeAdapter : DeliveryTypesAdapter? = null
    var i = -1
    var addressSelected = 0
    var mobSelected = 0
    var vehicleId = ""
    var weightId = ""
    var totalPrice = ""
    var distance = "25"
    var deliveryTypeId = ""
    var deliveryCharges = ""
    var payableAmount = ""
    var isNotifyMe = false
    var isNotifyRecipent = false
    var fareCollected = "Pickup end"
    var paymentType = "2"
    var orderId = ""
    var discountApplied = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    var pickupAddress = ""
    var pickupMobile = ""
    var pickupDate = ""
    var pickTime = ""
    var pickLat = ""
    var pickLong = ""
    var delAddress = ""
    var delMobile = ""
    var delDate = ""
    var delTime = ""
    var delLat = ""
    var delLong = ""
    var clickedForLocation = ""
    var edtDelAddress : EditText? = null
    var edtDeliveryMob : EditText? = null
    var latLongArrayList = ArrayList<LatLng>()
    var selectedAddress = ""
    var selectedlatLong = LatLng(0.0, 0.0)
    var oldLatLong = LatLng(0.0, 0.0)
    var selectedPosition = 0
    var twoHours = false
    var deliveryValue = ""
    var deliveryPos = 0
    var weighValue = ""
    var vehicleValue = ""
    var lPointsTotalUsed = "0"
    var lPointsTotalValue = "0"
    var userImage = ""
    val createOrderInput = CreateOrdersInput()
    var cancellationCharges = "0"
    private var confirmationDialog : Dialog? = null
    private var mDialogClass : DialogClass? = null
    var cancelOrderIds = ""
    override fun getLayoutId() : Int {
        return R.layout.activity_create_order_old
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initViews() {
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        activityCreateOrderBinding = viewDataBinding as ActivityCreateOrderOldBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        activityCreateOrderBinding.orderViewModel = orderViewModel

        activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
            getString(R.string.create_order)
        userImage =
            SharedPrefClass().getPrefValue(this, GlobalConstants.USER_IMAGE).toString()

        Glide.with(this).load(userImage).placeholder(R.drawable.ic_user)
            .into(activityCreateOrderBinding.toolbarCommon.imgRight)
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }
        orderId = intent.extras?.get("id").toString()
        // activityCreateOrderBinding.edtParcelValue.addTextChangedListener(textWatcher)
        activityCreateOrderBinding.edtParcelValue.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                calculatePrice()
            }
        })

        activityCreateOrderBinding.chkLoyalty.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
                if (TextUtils.isEmpty(orderId) || orderId.equals("null")) {
                    if (activityCreateOrderBinding.chkLoyalty!!.isChecked) {
                        isUsableLoyalty = true
                        lPointsTotalUsed = loyaltyPointsData.usabelPoints!!
                        lPointsTotalValue =
                            (loyaltyPointsData.usabelPoints?.toDouble()?.times(loyaltyPointsData.pricePerPoint!!.toDouble())).toString()
                        calculatePrice()
                    } else {
                        isUsableLoyalty = false
                        lPointsTotalUsed = "0"
                        lPointsTotalValue = "0"
                        calculatePrice()
                    }
                }

            }
        })
        activityCreateOrderBinding.edtPickMob.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                if (!TextUtils.isEmpty(activityCreateOrderBinding.edtPickMob.text.toString()) && activityCreateOrderBinding.edtPickMob.text.length < 10) {
                    activityCreateOrderBinding.edtPickMob.requestFocus()
                    activityCreateOrderBinding.edtPickMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                } else {
                    pickupMobile = activityCreateOrderBinding.edtPickMob.text.toString()
                    createOrderInput.pickupAddress?.phoneNumber =
                        activityCreateOrderBinding.edtPickMob.text.toString()
                }
            }
        })

        activityCreateOrderBinding.edtDelMob.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                if (!TextUtils.isEmpty(activityCreateOrderBinding.edtDelMob.text.toString()) && activityCreateOrderBinding.edtDelMob.text.length < 10) {
                    activityCreateOrderBinding.edtDelMob.requestFocus()
                    activityCreateOrderBinding.edtDelMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                } else {
                    delMobile = activityCreateOrderBinding.edtDelMob.text.toString()
                }
            }
        })
        // Initialize the AutocompleteSupportFragment.
        /*   val autocompleteFragment =
               supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                       as AutocompleteSupportFragment*/

        orderViewModel.getListsRes().observe(this,
            Observer<ListsResponse> { response->
                stopProgressDialog()
                if (!TextUtils.isEmpty(orderId) && !orderId.equals("null")) {
                    if (UtilsFunctions.isNetworkConnected()) {
                        startProgressDialog()
                        orderViewModel.orderDetail(orderId)
                        //orderViewModel.cancelReason(orderId)
                    }
                }

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleList.addAll(response.data?.vehicleData!!)
                            weightList.addAll(response.data?.weightData!!)
                            bannersList.addAll(response.data?.bannersData!!)
                            deliveryTypeList.addAll(response.data?.deliveryOptionData!!)
                            initDiscountsAdapter()
                            /*initWeightAdapter()
                            initVehiclesAdapter()
                            initDeliveryTypeAdapter()*/
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


        orderViewModel.calculatePriceRes().observe(this,
            Observer<CalculatePriceResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            deliveryCharges = response.data?.beforeDiscount.toString()
                            payableAmount = response.data?.afterDiscount.toString()

                            if (discountApplied.equals("")) {
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.GONE
                                activityCreateOrderBinding.txtTotalAmount.setText("")
                                activityCreateOrderBinding.txtDelCharges.setText(deliveryCharges + "₹")
                                activityCreateOrderBinding.edtPromoCode.isEnabled = true
                            } else {
                                activityCreateOrderBinding.txtDelCharges.setText(response.data?.afterDiscount + "₹")
                                activityCreateOrderBinding.edtPromoCode.isEnabled = false
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.VISIBLE
                                activityCreateOrderBinding.txtTotalAmount.setText(deliveryCharges + "₹")
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
                                activityCreateOrderBinding.cancelLayout.visibility = View.VISIBLE
                                activityCreateOrderBinding.txtCancellationCharges.text =
                                    response.data?.cancelOrder?.totalCancelPrice + "₹" + " View Details"
                                cancellationCharges =
                                    response.data?.cancelOrder?.totalCancelPrice as String
                            } else {
                                activityCreateOrderBinding.cancelLayout.visibility = View.GONE
                                response.data?.cancelOrder?.totalCancelPrice = ""
                                activityCreateOrderBinding.txtCancellationCharges.text = "0"
                            }

                            if (TextUtils.isEmpty(orderId) || orderId.equals("null")) {
                                if (response.data?.loyalityData != null && response.data?.loyalityData?.usabelPoints!!.toDouble() > 0) {
                                    loyaltyPointsData = response.data?.loyalityData!!

                                    activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                        View.VISIBLE
                                    activityCreateOrderBinding.txtLoyaltyPoints.visibility =
                                        View.VISIBLE
                                    activityCreateOrderBinding.txtLoyaltyPoints.text =
                                        "Max. usable points " + response.data?.loyalityData?.usabelPoints + ", 1 Point = " + response.data?.loyalityData?.pricePerPoint + "₹"
                                } else {
                                    activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                        View.GONE
                                }
                            }

                        }
                        else -> message?.let {
                            activityCreateOrderBinding.txtDelCharges.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        orderViewModel.applyCouponRes().observe(this,
            Observer<ApplyCouponResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            discountApplied = response.data?.coupanDiscount.toString()
                            activityCreateOrderBinding.rlOriginalPrice.visibility = View.VISIBLE
                            activityCreateOrderBinding.txtTotalAmount.setText(deliveryCharges + "₹")
                            activityCreateOrderBinding.txtDelCharges.setText(response.data?.payableAmount + "₹")
                            activityCreateOrderBinding.txtApply.setText("Remove")
                            activityCreateOrderBinding.edtPromoCode.isEnabled = false
                        }
                        else -> message?.let {
                            // activityCreateOrderBinding.txtDelCharges.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })

        orderViewModel.removeCouponRes().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            discountApplied = ""
                            activityCreateOrderBinding.rlOriginalPrice.visibility = View.GONE
                            activityCreateOrderBinding.txtTotalAmount.setText("")
                            activityCreateOrderBinding.txtDelCharges.setText(deliveryCharges + "₹")
                            activityCreateOrderBinding.txtApply.setText("Apply")
                            activityCreateOrderBinding.edtPromoCode.isEnabled = true
                            activityCreateOrderBinding.edtPromoCode.setText("")
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

        orderViewModel.createOrderRes().observe(this,
            Observer<CreateOrderResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            //   orderId = response.data?.id.toString()
                            // if (paymentType.equals("2")) {
                            showPaymentSuccessDialog()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

        orderViewModel.orderDetailRes().observe(this,
            Observer<OrdersDetailResponse> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            activityCreateOrderBinding.orderDetailModel = response.data
                            pickupMobile = response.data?.pickupAddress?.phoneNumber as String
                            pickupDate = response.data?.pickupAddress?.date as String
                            pickupAddress = response.data?.pickupAddress?.address as String
                            pickLat = response.data?.pickupAddress?.lat as String
                            pickLong = response.data?.pickupAddress?.long as String
                            pickTime = response.data?.pickupAddress?.time as String

                            activityCreateOrderBinding.edtPickDateTime.setText(
                                response.data?.pickupAddress?.date as String + " " + response.data?.pickupAddress?.time as String
                            )

                            createOrderInput.usedLPoints = lPointsTotalUsed
                            createOrderInput.lPointsPrice
                            if (!TextUtils.isEmpty(response.data?.usedLPoints) && !response.data?.usedLPoints.equals(
                                    "0"
                                )
                            ) {
                                activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                    View.VISIBLE
                                isUsableLoyalty = true
                                activityCreateOrderBinding.txtLoyaltyPoints.visibility =
                                    View.GONE
                                activityCreateOrderBinding.chkLoyalty.setChecked(true)
                                activityCreateOrderBinding.chkLoyalty.text =
                                    response.data?.usedLPoints + " Loyalty Points Used"
                                activityCreateOrderBinding.chkLoyalty.isEnabled = false
                            } else {
                                activityCreateOrderBinding.rlLoyaltyPoints.visibility =
                                    View.GONE
                            }


                            if (response.data?.deliveryAddress?.size!! > 0) {
                                delMobile =
                                    response.data?.deliveryAddress!![0].phoneNumber as String
                                delDate = response.data?.deliveryAddress!![0].date as String
                                delAddress = response.data?.deliveryAddress!![0].address as String
                                delLat = response.data?.deliveryAddress!![0].lat as String
                                delLong = response.data?.deliveryAddress!![0].long as String
                                delTime = response.data?.deliveryAddress!![0].time as String


                                activityCreateOrderBinding.edtDelAddress.setText(response.data?.deliveryAddress!![0].address as String)
                                activityCreateOrderBinding.edtDelMob.setText(response.data?.deliveryAddress!![0].phoneNumber as String)

                                activityCreateOrderBinding.edtDelDateTime.setText(
                                    response.data?.deliveryAddress!![0].date as String + " " + response.data?.deliveryAddress!![0].time as String
                                )

                                for (index in 1 until response.data?.deliveryAddress?.size!!) {
                                    addAddressView(response.data?.deliveryAddress!![index])
                                }
                            }

                            payableAmount = response.data?.deliveryCharges as String
                            deliveryCharges = response.data?.totalOrderPrice as String
                            for (vehicle in vehicleList) {
                                vehicle.selected = "false"
                                if (vehicle.id.equals(response.data?.vehicleType)) {
                                    vehicle.selected = "true"
                                    vehicleId = vehicle.id.toString()
                                    vehicleValue = vehicle.name!!
                                    createOrderInput.vehicleType = vehicleId
                                }
                            }
                            vehiclesAdapter?.notifyDataSetChanged()

                            for (weight in weightList) {
                                weight.selected = "false"
                                if (weight.id.equals(response.data?.weightId)) {
                                    weight.selected = "true"
                                    weightId = weight.id.toString()
                                    weighValue = weight.name!!
                                    createOrderInput.weight = weightId
                                }
                            }
                            weightAdapter?.notifyDataSetChanged()


                            for (deliveryType in deliveryTypeList) {
                                deliveryType.selected = "false"
                                if (deliveryType.id.equals(response.data?.deliveryOption)) {
                                    deliveryType.selected = "true"
                                    deliveryTypeId = deliveryType.id.toString()
                                    deliveryValue = deliveryType.title!!
                                    createOrderInput.deliveryOption = deliveryTypeId
                                }
                            }
                            deliveryTypeAdapter?.notifyDataSetChanged()

                            if (response.data?.notifyRecipient.equals("true")) {
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
                            }

                            if (!TextUtils.isEmpty(response.data?.promoCode)) {
                                activityCreateOrderBinding.edtPromoCode.isEnabled = false
                                activityCreateOrderBinding.txtApply.text = "Remove"
                                activityCreateOrderBinding.txtTotalAmount.text =
                                    response.data?.totalOrderPrice + "₹"
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.VISIBLE
                            } else {
                                activityCreateOrderBinding.edtPromoCode.isEnabled = true
                                activityCreateOrderBinding.txtApply.text = "Apply"
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.GONE
                            }

                            fareCollected = response.data?.fareCollected as String
                            if (fareCollected.equals("Pickup end")) {
                                activityCreateOrderBinding.rdPickup.setChecked(true)
                            } else {
                                activityCreateOrderBinding.rdDelivery.setChecked(true)
                            }

                            if (response.data?.paymentType.equals("2")) {
                                paymentType = "2"
                                activityCreateOrderBinding.rdCash.setChecked(true)
                                activityCreateOrderBinding.rdCash.setTextColor(getColor(R.color.colorPrimary))
                                activityCreateOrderBinding.rdNonCash.setTextColor(getColor(R.color.colorBlack))
                            } else {
                                paymentType = "1"
                                activityCreateOrderBinding.rdNonCash.setChecked(true)
                                activityCreateOrderBinding.rdNonCash.setTextColor(getColor(R.color.colorPrimary))
                                activityCreateOrderBinding.rdCash.setTextColor(getColor(R.color.colorBlack))
                            }

                            activityCreateOrderBinding.edtParcelValue.setText(response.data?.parcelValue as String)
                            if (TextUtils.isEmpty(response.data?.pendingCCharges) || response.data?.pendingCCharges.equals(
                                    "0"
                                )
                            ) {
                                activityCreateOrderBinding.cancelLayout.visibility = View.GONE
                            } else {
                                cancelOrderIds = response.data?.cancelOrderId as String
                                activityCreateOrderBinding.cancelLayout.visibility = View.VISIBLE
                                activityCreateOrderBinding.txtCancellationCharges.text =
                                    response.data?.pendingCCharges + "₹" + " View Details"
                                cancellationCharges =
                                    response.data?.pendingCCharges as String
                                //showToastSuccess("call api")
                                calculatePrice()
                            }
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


        activityCreateOrderBinding.chkNotificationMsg.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
                // whatever...
                isNotifyMe = isChecked
            }
        })

        activityCreateOrderBinding.chkNotificationReceipentMsg.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView : CompoundButton, isChecked : Boolean) {
                // whatever...
                isNotifyRecipent = isChecked
            }
        })

        activityCreateOrderBinding.rgFair.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId->
                val radio : RadioButton = findViewById(checkedId)
                if (radio == rd_pickup) {
                    fareCollected = "Pickup end"
                } else {
                    fareCollected = "Delivery end"
                }
            })

        activityCreateOrderBinding.rgCash.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId->
                val radio : RadioButton = findViewById(checkedId)
                if (radio == rd_cash) {
                    paymentType = "2"
                    radio.setTextColor(getColor(R.color.colorPrimary))
                    activityCreateOrderBinding.rdNonCash.setTextColor(getColor(R.color.colorBlack))
                } else {
                    radio.setTextColor(getColor(R.color.colorPrimary))
                    activityCreateOrderBinding.rdCash.setTextColor(getColor(R.color.colorBlack))
                    paymentType = "1"
                }
            })


        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "txtCancellationCharges" -> {
                        ///showToastSuccess("Show Details")
                        // showCancelOrdersDialog()
                    }
                    "imgDelAddress" -> {
                        if (UtilsFunctions.isNetworkConnected()) {
                            clickedForLocation = "delivery"
                            val intent = Intent(this, AddAddressActivity::class.java)
                            startActivityForResult(intent, 200)
                        }
                    }
                    "imgPickupAddress" -> {
                        if (UtilsFunctions.isNetworkConnected()) {
                            clickedForLocation = "pickup"
                            val intent = Intent(this, AddAddressActivity::class.java)
                            startActivityForResult(intent, 200)
                        }
                    }
                    "imgPickContact" -> {
                        if (TextUtils.isEmpty(activityCreateOrderBinding.edtDelAddress.text.toString())) {
                            if (UtilsFunctions.isNetworkConnected()) {
                                clickedForLocation = "pickup"
                                val intent = Intent(this, ContactListActivity::class.java)
                                startActivityForResult(intent, 201)
                            }
                        } else {
                            showCleareDataDialog(
                                "If you change pickup address, then your delivery locations will be removed.",
                                true
                            )
                        }

                    }
                    "imgDelContact" -> {
                        if (UtilsFunctions.isNetworkConnected()) {
                            clickedForLocation = "delivery"
                            val intent = Intent(this, ContactListActivity::class.java)
                            startActivityForResult(intent, 201)
                        }
                    }
                    "edtPickupLoc" -> {
                        clickedForLocation = "pickup"
                        // Set the fields to specify which types of place data to
                        // return after the user has made a selection.
                        if (TextUtils.isEmpty(activityCreateOrderBinding.edtDelAddress.text.toString())) {
                            if (UtilsFunctions.isNetworkConnected()) {
                                callAutoPlaceApi()
                            }
                        } else {
                            showCleareDataDialog(
                                "If you change pickup address, then your delivery locations will be removed.",
                                true
                            )
                        }

                    }
                    "edtDelAddress" -> {
                        if (!TextUtils.isEmpty(activityCreateOrderBinding.edtPickupLoc.text.toString())) {
                            clickedForLocation = "delivery"
                            // Set the fields to specify which types of place data to
                            // return after the user has made a selection.
                            if (UtilsFunctions.isNetworkConnected()) {
                                callAutoPlaceApi()
                            }
                        } else {
                            showToastError("Please select pickup address")
                        }

                    }
                    "txtApply" -> {
                        if (activityCreateOrderBinding.txtApply.text.toString().equals("Remove")) {
                            val mJsonObject = JsonObject()
                            mJsonObject.addProperty(
                                "promoCode",
                                activityCreateOrderBinding.edtPromoCode.text.toString()
                            )
                            mJsonObject.addProperty(
                                "discountPrice",
                                activityCreateOrderBinding.edtPromoCode.text.toString()
                            )
                            if (UtilsFunctions.isNetworkConnected()) {
                                orderViewModel.removeCoupon(mJsonObject)
                            }
                        } else {
                            if (!TextUtils.isEmpty(activityCreateOrderBinding.edtPromoCode.text.toString())) {
                                val mJsonObject = JsonObject()
                                mJsonObject.addProperty(
                                    "promoCode",
                                    activityCreateOrderBinding.edtPromoCode.text.toString()
                                )
                                mJsonObject.addProperty("totalPrice", deliveryCharges)
                                if (UtilsFunctions.isNetworkConnected()) {
                                    orderViewModel.applyCoupon(mJsonObject)
                                }
                            }
                        }

                    }
                    "edtPickDateTime" -> {
                        dateTimePicker(activityCreateOrderBinding.edtPickDateTime, "pickup")
                    }
                    "edtDelDateTime" -> {
                        dateTimePicker(activityCreateOrderBinding.edtDelDateTime, "delivery")
                    }
                    "txtAddAddress" -> {
                        activityCreateOrderBinding.edtDelMob.clearFocus()
                        if (TextUtils.isEmpty(delAddress) || TextUtils.isEmpty(delMobile) || TextUtils.isEmpty(
                                delTime
                            )
                        ) {
                            showToastError("Please add all delivery details")
                        } else if (addressList.size == 0) {
                            addAddressView(null)
                        } else {
                            if (TextUtils.isEmpty(addressList[addressList.size - 1].date) || TextUtils.isEmpty(
                                    addressList[addressList.size - 1].time
                                ) || TextUtils.isEmpty(addressList[addressList.size - 1].time) || TextUtils.isEmpty(
                                    addressList[addressList.size - 1].address
                                ) /*|| TextUtils.isEmpty(addressList[addressList.size - 1].phoneNumber)*/
                            ) {
                                showToastError("Please add all fields")
                            } else {
                                addAddressView(null)
                            }
                        }

                    }
                    "btnCreateOrder" -> {
                        val pickAddress = activityCreateOrderBinding.edtPickupLoc.text.toString()
                        val pickMobile = activityCreateOrderBinding.edtPickMob.text.toString()
                        val pickupDateTime =
                            activityCreateOrderBinding.edtPickDateTime.text.toString()
                        val deliveryAddress =
                            activityCreateOrderBinding.edtDelAddress.text.toString()
                        val deliveryMobile =
                            activityCreateOrderBinding.edtDelMob.text.toString()
                        val deliveryDateTime =
                            activityCreateOrderBinding.edtDelDateTime.text.toString()
                        val itemName =
                            activityCreateOrderBinding.edtItemName.text.toString()
                        val parcelValue =
                            activityCreateOrderBinding.edtParcelValue.text.toString()
                        val deliveryChargesPayable =
                            activityCreateOrderBinding.txtDelCharges.text.toString()

                        when {
                            vehicleId.isEmpty() -> showToastError("Please select vehicle")
                            pickAddress.isEmpty() -> showToastError("Please select pickup address")
                            pickMobile.isEmpty() -> showError(
                                activityCreateOrderBinding.edtPickMob,
                                getString(R.string.empty) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            pickupDateTime.isEmpty() -> showToastError("Please select pickup date and time")
                            deliveryAddress.isEmpty() -> showToastError("Please select delivery address")
                            deliveryMobile.isEmpty() -> showError(
                                activityCreateOrderBinding.edtDelMob,
                                getString(R.string.empty) + " " + getString(
                                    R.string.mob_no
                                )
                            )
                            deliveryDateTime.isEmpty() -> showToastError("Please select delivery date and time")
                            itemName.isEmpty() -> showError(
                                activityCreateOrderBinding.edtItemName,
                                getString(R.string.empty) + " " + getString(
                                    R.string.item_name
                                )
                            )
                            weightId.isEmpty() -> showToastError("Please select weight")
                            parcelValue.isEmpty() -> showError(
                                activityCreateOrderBinding.edtParcelValue,
                                getString(R.string.empty) + " " + getString(
                                    R.string.parcel_value
                                )
                            )
                            deliveryChargesPayable.isEmpty() -> showToastError("Please calculate delivery charges")
                            else -> {
                                val deliveryAddressList =
                                    ArrayList<CreateOrdersInput.PickupAddress>()
                                var address = CreateOrdersInput.PickupAddress()
                                address.address = delAddress
                                address.lat = delLat
                                address.long = delLong
                                address.date = delDate
                                address.time = delTime
                                address.phoneNumber = delMobile
                                deliveryAddressList.add(address)
                                createOrderInput.deliveryAddress = deliveryAddressList

                                createOrderInput.deliveryAddress!!.addAll(addressList)
                                val pickupAdd = CreateOrdersInput.PickupAddress()
                                pickupAdd.address = pickupAddress
                                pickupAdd.lat = pickLat
                                pickupAdd.long = pickLong
                                pickupAdd.date = pickupDate
                                pickupAdd.time = pickTime
                                pickupAdd.phoneNumber = pickupMobile
                                createOrderInput.pickupAddress = pickupAdd
                                /*createOrderInput.pickupAddress?.phoneNumber = pickupMobile
                                createOrderInput.pickupAddress?.address = pickupAddress
                                createOrderInput.pickupAddress?.date = pickupDate
                                createOrderInput.pickupAddress?.time = pickTime
                                createOrderInput.pickupAddress?.lat = pickLat
                                createOrderInput.pickupAddress?.long = pickLong*/

                                createOrderInput.itemName =
                                    activityCreateOrderBinding.edtItemName.text.toString()
                                createOrderInput.parcelValue =
                                    activityCreateOrderBinding.edtParcelValue.text.toString()
                                createOrderInput.promoCode =
                                    activityCreateOrderBinding.edtPromoCode.text.toString()
                                createOrderInput.totalOrderPrice = deliveryCharges
                                // activityCreateOrderBinding.txtDelCharges.text.toString()
                                createOrderInput.deliveryCharges = payableAmount
                                createOrderInput.orderPrice = deliveryCharges
                                createOrderInput.offerPrice = payableAmount
                                createOrderInput.distance = "25"
                                createOrderInput.fareCollected = fareCollected
                                createOrderInput.notifyRecipient = isNotifyRecipent.toString()
                                createOrderInput.notifyMe = isNotifyMe.toString()
                                createOrderInput.paymentType = paymentType
                                createOrderInput.orderId = orderId
                                createOrderInput.pendingCCharges = cancellationCharges
                                createOrderInput.cancelOrderIds = cancelOrderIds

                                createOrderInput.usedLPoints = lPointsTotalUsed
                                createOrderInput.lPointsPrice = lPointsTotalValue
                                //showOrderPreviewDialog()
                                // showToastSuccess("Success")
//                                val intent = Intent(this, LandingActivty::class.java)
//                                startActivity(intent)
                            }
                        }
                    }
                }
            })
        )

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finish()
    }

    private fun callAutoPlaceApi() {
        // activityCreateOrderBinding.autocompleteFragment.visibility = View.VISIBLE
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN,
            fields
        )
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    //region DATE TIME PICKER
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun dateTimePicker(edtDateTime : EditText, selectFrom : String) : String {
        var dateTime = ""
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this@CreateOrderOldActivty,
            DatePickerDialog.OnDateSetListener
            { view, year, monthOfYear, dayOfMonth->
                // activityCreateOrderBinding.edtDelMob.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                if (selectFrom.equals("pickup")) {
                    pickupDate = "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                    createOrderInput.pickupAddress?.date = pickupDate
                } else {
                    delDate = "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                }
                val date =
                    "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                val timeSetListener =
                    TimePickerDialog.OnTimeSetListener { timePicker, hour, minute->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        edtDateTime.setText(
                            date + " " + SimpleDateFormat(
                                "HH:mm"
                            ).format(calendar.time)
                        )
                        if (selectFrom.equals("pickup")) {
                            pickTime = SimpleDateFormat(
                                "HH:mm"
                            ).format(calendar.time)
                            createOrderInput.pickupAddress?.time = pickTime
                        } else {
                            delTime = SimpleDateFormat(
                                "HH:mm"
                            ).format(calendar.time)
                        }
                    }
                TimePickerDialog(
                    this,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            year,
            month,
            day
        )
        datePickerDialog.getDatePicker().setMinDate(Date().getTime())
        datePickerDialog.show()
        return dateTime
    }

    //endregion
    //region ADD MULTIPLE ADDRESS
    private fun addAddressView(pickupAddress : OrdersDetailResponse.PickupAddress?) {
        i = i + 1
        val addressModel = CreateOrdersInput.PickupAddress()
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView : View = inflater.inflate(R.layout.add_address_item, null)
        val edtAddress = rowView.findViewById<EditText>(R.id.edtDelAddress)
        val removeAddress = rowView.findViewById<TextView>(R.id.txtRemoveAddress)
        val edtDelMob = rowView.findViewById<EditText>(R.id.edtDelMob)
        val edtDelDateTime = rowView.findViewById<EditText>(R.id.edtDelDateTime)
        val imgDelContact = rowView.findViewById<ImageView>(R.id.imgDelContact)
        val imgDelMap = rowView.findViewById<ImageView>(R.id.imgDelMap)

        edtDelDateTime.id = i
        edtAddress?.id = i
        edtDelMob.id = i
        removeAddress.id = i
        rowView.id = i
        imgDelContact.id = i
        imgDelMap.id = i

        activityCreateOrderBinding.llAddress.addView(rowView)
        addressModel.id = i
        if (pickupAddress != null) {
            addressModel.address =
                pickupAddress.address
            addressModel.phoneNumber =
                pickupAddress.phoneNumber
            addressModel.date =
                pickupAddress.date
            addressModel.id = i
            addressModel.lat =
                pickupAddress.lat
            addressModel.long =
                pickupAddress.long
            addressModel.time =
                pickupAddress.time

            edtAddress.setText(addressModel.address)
            edtDelMob.setText(addressModel.phoneNumber)
            edtDelDateTime.setText(addressModel.date + " " + addressModel.time)

        }


        addressList.add(addressModel)

        if (addressList.size < 4) {
            activityCreateOrderBinding.txtAddAddressLay.visibility = View.VISIBLE
        } else {
            activityCreateOrderBinding.txtAddAddressLay.visibility = View.GONE
        }
        edtAddress?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                mobSelected = v?.id as Int
                edtDelAddress = edtAddress
                clickedForLocation = "other"
                callAutoPlaceApi()
            }
        })
        imgDelContact?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                // addressSelected = v?.id as Int
                edtDeliveryMob = edtDelMob
                clickedForLocation = "other"
                if (UtilsFunctions.isNetworkConnected()) {
                    val intent1 =
                        Intent(this@CreateOrderOldActivty, ContactListActivity::class.java)
                    startActivityForResult(intent1, 201)
                }
            }
        })

        imgDelMap?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                // addressSelected = v?.id as Int
                mobSelected = v?.id as Int
                edtDelAddress = edtAddress
                clickedForLocation = "other"
                if (UtilsFunctions.isNetworkConnected()) {
                    val intent1 = Intent(this@CreateOrderOldActivty, AddAddressActivity::class.java)
                    startActivityForResult(intent1, 200)
                }
            }
        })


        removeAddress.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                val tag = v?.id as Int
                val view1 : View = findViewById(tag)

                activityCreateOrderBinding.llAddress.removeView(view1)
                for (items in addressList) {
                    if (tag == items.id) {
                        addressList.remove(items)
                        break
                    }
                }
                if (addressList.size < 4) {
                    activityCreateOrderBinding.txtAddAddressLay.visibility = View.VISIBLE
                } else {
                    activityCreateOrderBinding.txtAddAddressLay.visibility = View.GONE
                }

            }
        })


        edtDelMob.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                val tag = v?.id as Int
                if (!TextUtils.isEmpty(edtDelMob.text.toString()) && edtDelMob.text.length < 10) {
                    edtDelMob.requestFocus()
                    edtDelMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                } else {
                    for (items in addressList) {
                        if (tag == items.id) {
                            // addressList.remove(items)
                            items.phoneNumber = edtDelMob.text.toString()
                            break
                        }
                    }

                }
            }
        })
        edtDelDateTime.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                val tag = v?.id as Int
                val view1 : View = findViewById(tag)
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(
                    this@CreateOrderOldActivty,
                    DatePickerDialog.OnDateSetListener
                    { view, year, monthOfYear, dayOfMonth->
                        edtDelDateTime.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)
                        for (items in addressList) {
                            if (tag == items.id) {
                                // addressList.remove(items)
                                // items.phoneNumber = edtDelMob.text.toString()
                                items.date = "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year
                                break
                            }
                        }
                        val date =
                            "" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                        val timeSetListener =
                            TimePickerDialog.OnTimeSetListener { timePicker, hour, minute->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                /*  addressList[i].time = SimpleDateFormat(
                                      "HH:mm"
                                  ).format(calendar.time)*/

                                for (items in addressList) {
                                    if (tag == items.id) {
                                        // addressList.remove(items)
                                        // items.phoneNumber = edtDelMob.text.toString()
                                        items.time = SimpleDateFormat(
                                            "HH:mm"
                                        ).format(calendar.time)
                                        break
                                    }
                                }
                                edtDelDateTime.setText(
                                    date + " " + SimpleDateFormat(
                                        "HH:mm"
                                    ).format(calendar.time)
                                )
                            }
                        TimePickerDialog(
                            this@CreateOrderOldActivty,
                            timeSetListener,
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.getDatePicker().setMinDate(Date().getTime());
                datePickerDialog.show()
            }
        })
        //setListeners(rowView, true, addressList.size.minus(1))
    }

    fun clearAddressData() {
        if (activityCreateOrderBinding.llAddress.getChildCount() > 0) {
            activityCreateOrderBinding.llAddress.removeAllViews()
        }
        //activityCreateOrderBinding . llAddress . removeAllviews ()
        activityCreateOrderBinding.edtDelDateTime.setText("")
        activityCreateOrderBinding.edtDelAddress.setText("")
        activityCreateOrderBinding.edtDelMob.setText("")
        delAddress = ""
        delMobile = ""
        delDate = ""
        delTime = ""
        delLat = ""
        delLong = ""
        addressList.clear()
    }

    fun clearDataDialog(message : String) {
        confirmationDialog = mDialogClass?.setDefaultDialog(
            this,
            this,
            "clearData",
            message
        )
        confirmationDialog?.show()
    }

    override fun onDialogConfirmAction(mView : View?, mKey : String?) {
        when (mKey) {
            "clearData" -> {
                clearAddressData()
                confirmationDialog?.dismiss()
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String?) {
        confirmationDialog?.dismiss()
    }

    //endregion
    //region CALL ADAPTERS
    private fun initDiscountsAdapter() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        /* val discountAdapter =
             DiscountListAdapter(
                 this,
                 bannersList,
                 this
             )
         val linearLayoutManager = LinearLayoutManager(this)
         linearLayoutManager.orientation = RecyclerView.HORIZONTAL
         //val gridLayoutManager = GridLayoutManager(this, 4)
         activityCreateOrderBinding.rvDiscounts.layoutManager = linearLayoutManager
         activityCreateOrderBinding.rvDiscounts.setHasFixedSize(true)
         activityCreateOrderBinding.rvDiscounts.adapter = discountAdapter
         activityCreateOrderBinding.rvDiscounts.addOnScrollListener(object :
             RecyclerView.OnScrollListener() {
             override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

             }
         })*/

    }

    /*private fun initDeliveryTypeAdapter() {
        deliveryTypeAdapter =
            DeliveryTypesAdapter(
                this,
                deliveryTypeList,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        activityCreateOrderBinding.rvDeliveryTypes.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvDeliveryTypes.setHasFixedSize(true)
        activityCreateOrderBinding.rvDeliveryTypes.adapter = deliveryTypeAdapter
        activityCreateOrderBinding.rvDeliveryTypes.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    private fun initVehiclesAdapter() {
        vehiclesAdapter =
            VehiclesListAdapter(
                this,
                vehicleList,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        activityCreateOrderBinding.rvVehicles.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvVehicles.setHasFixedSize(true)
        activityCreateOrderBinding.rvVehicles.adapter = vehiclesAdapter
        activityCreateOrderBinding.rvVehicles.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    private fun initWeightAdapter() {
        weightAdapter =
            WeightListAdapter(
                this,
                weightList,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        activityCreateOrderBinding.rvWeight.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvWeight.setHasFixedSize(true)
        activityCreateOrderBinding.rvWeight.adapter = weightAdapter
        activityCreateOrderBinding.rvWeight.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
            }
        })
    }
*/
    //endregion
    //region ADAPTER CLICk LISTENER
    fun selectedVehicle(position : Int, s : String) {
        for (i in 0 until vehicleList.count()) {
            vehicleList[i].selected = "false"
        }
        vehicleList[position].selected = "true"
        vehicleId = vehicleList[position].id!!
        createOrderInput.vehicleType = vehicleId
        vehiclesAdapter?.notifyDataSetChanged()
        vehicleValue = vehicleList[position].name!!
        calculatePrice()
    }

    fun selectedWeight(position : Int, s : String) {
        for (i in 0 until weightList.count()) {
            weightList[i].selected = "false"
        }
        weightList[position].selected = "true"
        weightId = weightList[position].id!!
        weightAdapter?.notifyDataSetChanged()
        createOrderInput.weight = weightId
        weighValue = weightList[position].name!!
        calculatePrice()
    }

    fun selectedDeliveryType(position : Int, isSelected : String) {
        for (i in 0 until deliveryTypeList.count()) {
            deliveryTypeList[i].selected = "false"
        }
        deliveryTypeList[position].selected = isSelected
        if (isSelected.equals("true")) {
            deliveryTypeId = deliveryTypeList[position].id!!
        } else {
            deliveryTypeId = ""
        }
        if (deliveryTypeList[position].title!!.contains("2 ")) {
            deliveryPos = position
            if (!TextUtils.isEmpty(activityCreateOrderBinding.edtDelAddress.text.toString())) {
                showCleareDataDialog(
                    "If you change delivery type, then your delivery locations will be removed.",
                    true
                )
            } else {
                twoHours = true
                deliveryValue = deliveryTypeList[position].title!!
                createOrderInput.deliveryOption = deliveryTypeList[position].id!!
                calculatePrice()
                deliveryTypeAdapter?.notifyDataSetChanged()
            }
            //showToastSuccess("2 hours")
        } else {
            twoHours = false
            deliveryValue = deliveryTypeList[position].title!!
            createOrderInput.deliveryOption = deliveryTypeList[position].id!!
            calculatePrice()
            deliveryTypeAdapter?.notifyDataSetChanged()
        }

    }

    //endregion
    private fun showError(textView : EditText, error : String) {
        textView.requestFocus()
        textView.error = error
    }

    //region CALL PRICE CALCULATION API
    fun calculatePrice() {
        if (!TextUtils.isEmpty(vehicleId) && !TextUtils.isEmpty(distance) && !TextUtils.isEmpty(
                weightId
            ) && !TextUtils.isEmpty(activityCreateOrderBinding.edtParcelValue.text.toString())
        ) {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "parcelValue",
                activityCreateOrderBinding.edtParcelValue.text.toString()
            )
            if (orderId.equals("null")) {
                orderId = ""
            }
            mJsonObject.addProperty("orderId", orderId)
            mJsonObject.addProperty("vehicleId", vehicleId)
            mJsonObject.addProperty("deliveryId", deliveryTypeId)
            mJsonObject.addProperty("weightId", weightId)
            mJsonObject.addProperty("distance", distance)
            mJsonObject.addProperty("isUseLoyality", isUsableLoyalty)

            mJsonObject.addProperty(
                "discount",
                discountApplied
            )
            if (UtilsFunctions.isNetworkConnected()) {
                orderViewModel.calculatePrice(mJsonObject)
                //  showToastSuccess("api call")
            }
        }
    }

    //endregion
    //region ONACTIVITY FOR RESULT
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        // activityCreateOrderBinding.autocompleteFragment.visibility = View.GONE
        val inputManager : InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus.windowToken,
            InputMethodManager.SHOW_FORCED
        )


        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.e("Create Order", "Place: ${place.name}, ${place.id}")
                        selectedAddress = place.address.toString()
                        selectedlatLong = place.latLng!!
                        if (clickedForLocation.equals("pickup")) {
                            createOrderInput.pickupAddress?.address = place.address.toString()
                            createOrderInput.pickupAddress?.lat = place.latLng!!.latitude.toString()
                            createOrderInput.pickupAddress?.long =
                                place.latLng!!.longitude.toString()

                            latLongArrayList.add(place.latLng!!)
                            activityCreateOrderBinding.edtPickupLoc.setText(place.address.toString())
                            pickupAddress = place.address.toString()
                            pickLat = place.latLng!!.latitude.toString()
                            pickLong = place.latLng!!.longitude.toString()

                        } else if (clickedForLocation.equals("delivery")) {
                            if (twoHours) {
                                selectedAddress = place.address.toString()
                                selectedlatLong = place.latLng!!
                                callDistanceAPI(place.latLng!!)
                            } else {
                                activityCreateOrderBinding.edtDelAddress.setText(place.address.toString())
                                delAddress = place.address.toString()
                                delLat = place.latLng!!.latitude.toString()
                                delLong = place.latLng!!.longitude.toString()
                            }

                        } else {
                            if (twoHours) {
                                callDistanceAPI(place.latLng!!)
                            } else {
                                edtDelAddress?.setText(place.address.toString())
                                val tag = edtDelAddress?.id
                                for (items in addressList) {
                                    if (tag == items.id) {
                                        // addressList.remove(items)
                                        // items.phoneNumber = edtDelMob.text.toString()
                                        items.address = place.address.toString()
                                        items.lat =
                                            place.latLng!!.latitude.toString()
                                        items.long =
                                            place.latLng!!.longitude.toString()
                                        break
                                    }
                                }

                            }

                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e("Create Order", status.statusMessage)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }

            return
        } else if (requestCode == 200) {
            val lat = data?.getStringExtra("lat")
            val long = data?.getStringExtra("long")
            val address = data?.getStringExtra("address")
            if (!TextUtils.isEmpty(lat)) {
                if (clickedForLocation.equals("pickup")) {
                    activityCreateOrderBinding.edtPickupLoc.setText(address)
                    pickLat = lat as String
                    pickLong = long.toString()
                    pickupAddress = address.toString()
                } else if (clickedForLocation.equals("delivery")) {
                    if (twoHours) {
                        val latLong = LatLng(lat!!.toDouble(), long!!.toDouble())
                        selectedAddress = address.toString()
                        selectedlatLong = latLong
                        callDistanceAPI(latLong)
                    } else {
                        activityCreateOrderBinding.edtDelAddress.setText(address)
                        delLat = lat as String
                        delLong = long.toString()
                        delAddress = address.toString()
                    }

                } else {
                    if (twoHours) {
                        val latLong = LatLng(lat!!.toDouble(), long!!.toDouble())
                        selectedAddress = address.toString()
                        selectedlatLong = latLong
                        callDistanceAPI(latLong)
                    } else {
                        edtDelAddress?.setText(address)
                        val tag = edtDelAddress?.id
                        for (items in addressList) {
                            if (tag == items.id) {
                                items.address = address
                                items.lat = lat as String
                                items.long = long.toString()
                                break
                            }
                        }

                    }
                }
            }

        } else if (requestCode == 201) {
            val num = data?.getStringExtra("num")
            if (!TextUtils.isEmpty(num)) {
                if (clickedForLocation.equals("pickup")) {
                    activityCreateOrderBinding.edtPickMob.setText(num)
                    pickupMobile = num.toString()
                } else if (clickedForLocation.equals("delivery")) {
                    activityCreateOrderBinding.edtDelMob.setText(num)
                    delMobile = num.toString()
                    activityCreateOrderBinding.edtDelMob?.clearFocus()
                } else {
                    edtDeliveryMob?.setText(num.toString())
                    val tag = edtDeliveryMob?.id
                    for (items in addressList) {
                        if (tag == items.id) {
                            items.phoneNumber = num
                            break
                        }
                    }
                    edtDeliveryMob?.clearFocus()

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun callDistanceAPI(latLng : LatLng) {
        var desLoc = ""
        val mapQuery = HashMap<String?, String?>()
        mapQuery.put("units", "imperial");
        mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
        desLoc = desLoc + "|" + latLng.latitude + "," + latLng.longitude
        mapQuery.put("destinations", desLoc)
        mapQuery.put("key", getString(R.string.maps_api_key))
        val origin = pickLat + "," + pickLong
        val requestQueue = Volley.newRequestQueue(this)
        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=${origin}&destinations=${desLoc}&key=${getString(
                R.string.maps_api_key
            )}"
        Log.e("url", url)
        val request =
            JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response->
                try {
                    val gson = GsonBuilder().serializeNulls().create()
                    val objecat = response
                    val distanceResponse = gson.fromJson<DistanceResponse>(
                        "" + response,
                        DistanceResponse::class.java
                    )

                    Log.e("url", distanceResponse.toString())
                    var distancInMeter = 0.0
                    if (distanceResponse.status.equals("OK")) {
                        val rows = distanceResponse.rows
                        if (rows?.size!! > 0) {
                            val elements = rows[0].elements
                            for (i in 0 until elements!!.size) {
                                distancInMeter =
                                    distancInMeter.plus(elements[i].distance?.value!!.toDouble()/*distanceInMeterArray!![0].toDouble()*/)
                            }
                        }
                    }
                    val distancInKm = distancInMeter / 1000
                    // showToastSuccess(distancInKm.toString())
                    distance = distancInKm.toString()

                    if (distancInKm < 25) {
                        calculatePrice()
                        latLongArrayList.add(latLng)
                        if (clickedForLocation.equals("pickup")) {
                            //latLongArrayList.add(place.latLng!!)
                            activityCreateOrderBinding.edtPickupLoc.setText(selectedAddress)
                            pickupAddress = selectedAddress
                            pickLat = selectedlatLong.latitude.toString()
                            pickLong = selectedlatLong.longitude.toString()
                        } else if (clickedForLocation.equals("delivery")) {
                            activityCreateOrderBinding.edtDelAddress.setText(selectedAddress)
                            delAddress = selectedAddress//place.address.toString()
                            delLat = selectedlatLong.latitude.toString()
                            delLong = selectedlatLong.longitude.toString()

                        } else {
                            edtDelAddress?.setText(selectedAddress)
                            val tag = edtDelAddress?.id
                            for (items in addressList) {
                                if (tag == items.id) {
                                    items.address =
                                        selectedAddress//place.address.toString()
                                    items.lat = selectedlatLong.latitude.toString()
                                    items.long = selectedlatLong.longitude.toString()
                                    break
                                }
                            }

                        }
                    } else {
                        showToastError("This location is not deliverable in 2 hours delivery, try some more near point within 25 km")
                    }
                } catch (e : JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error-> error.printStackTrace() })
        requestQueue?.add(request)
        //}
    }

    //endregion
//region DIALOGS
    public fun showOfferInformation(pos : Int) {
        var confirmationDialog = Dialog(this, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.layout_offer_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)
        val imgOffer = confirmationDialog?.findViewById<ImageView>(R.id.imgOffer)
        val txtCouponName = confirmationDialog?.findViewById<TextView>(R.id.txtCouponName)
        val txtCouponCode = confirmationDialog?.findViewById<TextView>(R.id.txtCouponCode)
        val txtCouponDiscount = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDiscount)
        val txtCouponDesc = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDesc)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
        val animation = AnimationUtils.loadAnimation(this!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()

        txtCouponName.setText("Offer Name: " + bannersList[pos].name)
        txtCouponCode.setText(bannersList[pos].code)
        txtCouponDesc.setText(Html.fromHtml(bannersList[pos].description).toString())
        txtCouponDiscount.setText(bannersList[pos].discount + "% OFF")

        Glide.with(this).load(bannersList[pos].icon).into(imgOffer)
        btnSubmit?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }

    private fun showPaymentSuccessDialog() {
        val confirmationDialog = Dialog(this, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.order_place_success_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancel = confirmationDialog?.findViewById<Button>(R.id.btnDone)
        cancel?.setOnClickListener {
            confirmationDialog?.dismiss()
            /* val intent = Intent(this, LandingActivty::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
             startActivity(intent)*/
            finish()
        }
        confirmationDialog?.show()
    }

    /*public fun showOrderPreviewDialog() {
        var confirmationDialog = Dialog(this, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.activity_order_preview,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtDeliveryOption = confirmationDialog?.findViewById<TextView>(R.id.txtDeliveryOption)
        val txtFare = confirmationDialog?.findViewById<TextView>(R.id.txtFare)
        val txtWeight = confirmationDialog?.findViewById<TextView>(R.id.txtWeight)
        val txtValue = confirmationDialog?.findViewById<TextView>(R.id.txtValue)
        val txtFareCollected = confirmationDialog?.findViewById<TextView>(R.id.txtFareCollected)
        val txtVehicleType = confirmationDialog?.findViewById<TextView>(R.id.txtVehicleType)
        val btnConfirm = confirmationDialog?.findViewById<Button>(R.id.btnConfirm)
        val imgBack = confirmationDialog?.findViewById<ImageView>(R.id.imagBack)
        val imgUser = confirmationDialog?.findViewById<ImageView>(R.id.img_right)
        val rvAddress = confirmationDialog?.findViewById<RecyclerView>(R.id.rvAddress)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
        val animation = AnimationUtils.loadAnimation(this!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()

        txtDeliveryOption.setText(deliveryValue)
        txtFare.setText(payableAmount)
        txtWeight.setText(weighValue)
        txtValue.setText(createOrderInput.parcelValue)
        txtFareCollected.setText(createOrderInput.fareCollected)
        txtVehicleType.setText(vehicleValue)
        val addressAdapter =
            AddressListAdapter(
                this,
                createOrderInput.deliveryAddress, createOrderInput.pickupAddress,
                this
            )
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rvAddress.layoutManager = linearLayoutManager
        rvAddress.setHasFixedSize(true)
        rvAddress.adapter = addressAdapter
        rvAddress.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
            }
        })

        Glide.with(this).load(userImage).placeholder(R.drawable.ic_user).into(imgUser)
        imgBack?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        btnConfirm?.setOnClickListener {
            if (UtilsFunctions.isNetworkConnected()) {
                startProgressDialog()
                orderViewModel.createOrder(createOrderInput)
                confirmationDialog?.dismiss()
            }
        }

        confirmationDialog?.show()
    }*/
    fun showCleareDataDialog(s : String, istwoHours : Boolean) {
        var confirmationDialog = Dialog(this, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.custom_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yes = confirmationDialog.findViewById<Button>(R.id.yes)
        val no = confirmationDialog.findViewById<Button>(R.id.no)
        val txtTitle = confirmationDialog.findViewById<TextView>(R.id.txt_dia)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
        val animation = AnimationUtils.loadAnimation(this!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()
        txtTitle.setText(s)
        yes.setOnClickListener {
            // mInterface.onDialogConfirmAction(null, mKey)
            if (istwoHours) {
                deliveryValue = deliveryTypeList[deliveryPos].title!!
                createOrderInput.deliveryOption = deliveryTypeList[deliveryPos].id!!
                calculatePrice()
                deliveryTypeAdapter?.notifyDataSetChanged()
            }
            twoHours = istwoHours
            clearAddressData()
            confirmationDialog.dismiss()
        }
        /* if (!ValidationsClass().checkStringNull(cancelString))
             no.visibility = View.VISIBLE*/
        no.setOnClickListener {
            confirmationDialog.dismiss()
            //  mInterface.onDialogCancelAction(null, mKey)
        }


        confirmationDialog?.show()
    }
    /* @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
     private fun showCancelOrdersDialog() {
         confirmationDialog = Dialog(this, R.style.transparent_dialog)
         confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)


         confirmationDialog?.setContentView(R.layout.cancel_order)
         confirmationDialog?.setCancelable(true)

         confirmationDialog?.window!!.setLayout(
             LinearLayout.LayoutParams.MATCH_PARENT,
             LinearLayout.LayoutParams.MATCH_PARENT
         )
         confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         val recyclerView = confirmationDialog?.findViewById<RecyclerView>(R.id.rvList)
         val txtCharges = confirmationDialog?.findViewById<TextView>(R.id.txtCharges)
         val btnDone = confirmationDialog?.findViewById<TextView>(R.id.btnDone)

         txtCharges?.text = cancelOrderDetail.totalCancelPrice + "₹"
         val addressAdapter =
             CancelOrdersListAdapter(
                 this,
                 cancelOrderDetail.orders,
                 this
             )
         val linearLayoutManager = LinearLayoutManager(this)
         linearLayoutManager.orientation = RecyclerView.VERTICAL
         recyclerView?.layoutManager = linearLayoutManager
         recyclerView?.setHasFixedSize(true)
         recyclerView?.adapter = addressAdapter
         recyclerView?.addOnScrollListener(object :
             RecyclerView.OnScrollListener() {
             override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
             }
         })

         btnDone?.setOnClickListener {
             confirmationDialog?.dismiss()
             //  mInterface.onDialogCancelAction(null, mKey)
         }


         if (!confirmationDialog?.isShowing()!!) {
             confirmationDialog?.show()
         }

     }*/
//endregion
}
