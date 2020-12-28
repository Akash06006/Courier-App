package com.android.courier.views.orders.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.Manifest
import android.text.Editable
import android.text.TextWatcher
import com.android.courier.databinding.FragmentCreateOrdersFirstBinding
import com.android.courier.R
import com.android.courier.adapters.orders.DeliveryTypesAdapter
import com.android.courier.adapters.orders.WeightListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.common.UtilsFunctions.showToastError
import com.android.courier.common.UtilsFunctions.showToastInfo
import com.android.courier.constants.GlobalConstants
import com.android.courier.maps.FusedLocationClass
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.utils.Utils
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.contacts.ContactListActivity
import com.android.courier.views.orders.AddAddressActivity
import com.android.courier.views.orders.CreateOrderActivty
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_order.view.*
import kotlinx.android.synthetic.main.layout_toast.view.*
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class
CreateOrderFirstFragment : BaseFragment(), DialogssInterface {
    private var mFusedLocationClass : FusedLocationClass? = null
    private lateinit var orderViewModel : OrderViewModel
    val PERMISSION_ID = 42
    var orderList = ArrayList<OrdersListResponse.Data>()
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private lateinit var createOrderFirstBinding : FragmentCreateOrdersFirstBinding
    var weightAdapter : WeightListAdapter? = null
    var weightList = ArrayList<ListsResponse.WeightData>()
    var weightId = ""
    var weighValue = ""
    var orderId = ""
    var vehicleList = ArrayList<ListsResponse.VehicleData>()
    val createOrderInput = CreateOrdersInput()
    var i = -1
    var addressList = ArrayList<CreateOrdersInput.PickupAddress>()
    private var confirmationDialog : Dialog? = null
    private var mDialogClass : DialogClass? = null
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
    var mobSelected = 0
    var clickedForLocation = ""
    var edtDelAddress : EditText? = null
    var edtDeliveryMob : EditText? = null
    var latLongArrayList = ArrayList<LatLng>()
    var selectedAddress = ""
    var selectedlatLong = LatLng(0.0, 0.0)
    var distance = "0"
    var deliveryType = "1"
    var deliveryTypeList = ArrayList<ListsResponse.DeliveryOptionData>()
    var deliveryTypeAdapter : DeliveryTypesAdapter? = null
    var deliveryTypeId = ""
    private var isUsableLoyalty = false
    var discountApplied = ""
    var cancelOrderIds = ""
    var pos = 0
    private lateinit var cancelOrderDetail : CancelOrder
    var time = ArrayList<String>()
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_create_orders_first
    }

    override fun onResume() {
        super.onResume()

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        //time = slot(0)
        createOrderFirstBinding = viewDataBinding as FragmentCreateOrdersFirstBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        createOrderFirstBinding.orderViewModel = orderViewModel
        // categoriesList=List<Service>()
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        createOrderFirstBinding.imgPriceDetails.isEnabled = false
        var adapter1 = ArrayAdapter(
            activity!!,
            R.layout.spinner_item, time
        )
        val days = ArrayList<String>()
        days.add("Today")
        days.add("Tomorrow")
        val adapter = ArrayAdapter(
            activity!!,
            R.layout.spinner_item, days
        )
        createOrderFirstBinding.txtDate.adapter = adapter
        createOrderFirstBinding.txtDate.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                time = slot(position)
                createOrderFirstBinding.txtTime.setSelection(0)
                if (position == 0) {
                    val date = Utils(activity!!).getDateLocal(
                        "EEE MMM dd HH:mm:ss zzzz yyyy",
                        getDaysAgo(0).toString(),
                        "dd/MM/YYYY"
                    )
                    // MyApplication.createOrdersInput.pickupAddress?.date = date
                    pickupDate = date
                } else {
                    val date = Utils(activity!!).getDateLocal(
                        "EEE MMM dd HH:mm:ss zzzz yyyy",
                        getDaysAgo(1).toString(),
                        "dd/MM/YYYY"
                    )
                    // MyApplication.createOrdersInput.pickupAddress?.date = date
                    pickupDate = date
                }
                adapter1 = ArrayAdapter(
                    activity!!,
                    R.layout.spinner_item, time
                )
                createOrderFirstBinding.txtTime.adapter = adapter1

                for (i in 0 until time.size) {
                    if (time[i].equals(pickTime)) {
                        createOrderFirstBinding.txtTime.setSelection(i)
                    }
                }

            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }

        createOrderFirstBinding.txtTime.adapter = adapter1
        createOrderFirstBinding.txtTime.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                view.background = ColorDrawable(Color.parseColor("#F6E3D2"))
                if (position == 0) {
                    // MyApplication.createOrdersInput.pickupAddress?.time = ""
                    pickTime = ""
                } else {
                    pickTime = time[position]
                    // MyApplication.createOrdersInput.pickupAddress?.time = time[position]
                }

            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }
        val name = SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERNAME).toString()
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()

        orderViewModel.getListsRes().observe(this,
            Observer<ListsResponse> { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleList.addAll(response.data?.vehicleData!!)
                            weightList.addAll(response.data?.weightData!!)
                            if (weightList.size > 0) {
                                weightList[0].selected = "true"
                                weightId = weightList[0].id!!
                                GlobalConstants.WEIGHT_ID = weightId
                                weightAdapter?.notifyDataSetChanged()
                                createOrderInput.weight = weightId
                                weighValue = weightList[0].name!!
                                MyApplication.createOrdersInput.weight = weightId
                                MyApplication.createOrdersInput.weightValue =
                                    weightList[0].name!!

                                initWeightAdapter()
                            }
                            // bannersList.addAll(response.data?.bannersData!!)
                            deliveryTypeList.addAll(response.data?.deliveryOptionData!!)
                            if (deliveryTypeList.size > 0) {
                                deliveryTypeList[0].selected = "true"
                                //  if (isSelected.equals("true")) {
                                deliveryTypeId = deliveryTypeList[0].id!!
                                GlobalConstants.DELIVERY_TYPE = deliveryTypeId
                                MyApplication.createOrdersInput.deliveryOption = deliveryTypeId
                                if (deliveryTypeList[0].title!!.contains("Reg")) {
                                    MyApplication.createOrdersInput.deliveryValue = "1"
                                } else {
                                    MyApplication.createOrdersInput.deliveryValue = "2"
                                }

                                initDeliveryTypeAdapter()
                            }

                            preFilledData()
                            //  initDiscountsAdapter()
                            /*initWeightAdapter()
                            initVehiclesAdapter()
                            */
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })




        orderViewModel.calculatePriceRes().observe(this,
            Observer<CalculatePriceResponse> { response->
                // stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            val orderPrice = response.data?.beforeDiscount.toString()
                            val payableAmount = response.data?.afterDiscount.toString()
                            var totalCancellationCharges = "0"

                            createOrderFirstBinding.imgPriceDetails.isEnabled = true


                            if (discountApplied.equals("")) {
                                /*activityCreateOrderBinding.rlOriginalPrice.visibility = View.GONE
                                activityCreateOrderBinding.txtTotalAmount.setText("")
                                activityCreateOrderBinding.txtDelCharges.setText(deliveryCharges + "₹")
                                activityCreateOrderBinding.edtPromoCode.isEnabled = true*/
                            } else {
                                /*activityCreateOrderBinding.txtDelCharges.setText(response.data?.afterDiscount + "₹")
                                activityCreateOrderBinding.edtPromoCode.isEnabled = false
                                activityCreateOrderBinding.rlOriginalPrice.visibility = View.VISIBLE
                                activityCreateOrderBinding.txtTotalAmount.setText(deliveryCharges + "₹")*/
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
                                //activityCreateOrderBinding.cancelLayout.visibility = View.VISIBLE
                                totalCancellationCharges =
                                    response.data?.cancelOrder?.totalCancelPrice as String
                                // activityCreateOrderBinding.txtCancellationCharges.text =response.data?.cancelOrder?.totalCancelPrice + "₹" + " View Details"
                                // cancellationCharges =  response.data?.cancelOrder?.totalCancelPrice as String
                            } else {
                                totalCancellationCharges = "0"
                                /*  activityCreateOrderBinding.cancelLayout.visibility = View.GONE
                                  response.data?.cancelOrder?.totalCancelPrice = ""
                                  activityCreateOrderBinding.txtCancellationCharges.text = "0"*/
                            }

                            MyApplication.createOrdersInput.deliveryCharges =
                                response.data?.deliveryFee + ""
                            MyApplication.createOrdersInput.totalOrderPrice = payableAmount
                            MyApplication.createOrdersInput.orderPrice = orderPrice
                            MyApplication.createOrdersInput.offerPrice =
                                response.data?.discountPrice + ""
                            MyApplication.createOrdersInput.securityFee =
                                response.data?.securityFee + ""
                            MyApplication.createOrdersInput.pendingCCharges =
                                totalCancellationCharges
                            MyApplication.createOrdersInput.cancelOrderIds = cancelOrderIds
                            MyApplication.createOrdersInput.notifyMe = "false"
                            MyApplication.createOrdersInput.notifyRecipient = "false"
                            MyApplication.createOrdersInput.vehicleType =
                                "9c9d2c0e-02d6-4095-a0b4-b267b736dd65"

                            (activity as CreateOrderActivty).setViewLine()

                            setPrice(
                                payableAmount,
                                response.data?.deliveryFee + "",
                                response.data?.weightFee + "",
                                response.data?.securityFee + "",
                                response.data?.discountPrice + "",
                                totalCancellationCharges,
                                "first",
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
        // var time = ArrayList<String>()/*slot(position)*/
        /*createOrderFirstBinding.edtDelMob.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                if (!TextUtils.isEmpty(createOrderFirstBinding.edtDelMob.text.toString()) && createOrderFirstBinding.edtDelMob.text.length < 10) {
                    createOrderFirstBinding.edtDelMob.requestFocus()
                    createOrderFirstBinding.edtDelMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                } else {
                    delMobile = createOrderFirstBinding.edtDelMob.text.toString()
                }
            }
        })*/
        /*createOrderFirstBinding.edtPickMob.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                if (!TextUtils.isEmpty(createOrderFirstBinding.edtPickMob.text.toString()) && createOrderFirstBinding.edtPickMob.text.length < 10) {
                    // createOrderFirstBinding.edtPickMob.requestFocus()
                    createOrderFirstBinding.edtPickMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                } else {
                    pickupMobile = createOrderFirstBinding.edtPickMob.text.toString()
                    MyApplication.createOrdersInput.pickupAddress?.phoneNumber =
                        createOrderFirstBinding.edtPickMob.text.toString()
                }
            }
        })*/
        //MyApplication.createOrdersInput.pickupAddress?.phoneNumber = pickupMobile
        /* createOrderFirstBinding.edtDelMob.setOnEditorActionListener { v, actionId, event->
             if (actionId == EditorInfo.IME_ACTION_DONE) {
                 // doSomething()
                 // createOrderFirstBinding.edtDelMob.clearFocus()
                 if (!TextUtils.isEmpty(createOrderFirstBinding.edtDelMob.text.toString()) && createOrderFirstBinding.edtDelMob.text.length < 10) {
                     // createOrderFirstBinding.edtDelMob.requestFocus()
                     createOrderFirstBinding.edtDelMob.error =
                         getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                 } else {
                       val imm =
                           activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                       imm.hideSoftInputFromWindow(v.windowToken, 0)
                     delMobile = createOrderFirstBinding.edtDelMob.text.toString()
                     *//* MyApplication.createOrdersInput.pickupAddress?.phoneNumber =
                         createOrderFirstBinding.edtDelMob.text.toString()*//*
                }
                true
            } else {
                false
            }
        }*/
        orderViewModel.isClick().observe(
            this, Observer<String>(
                function =
                fun(it : String?) {
                    when (it) {
                        "imgDelInfo" -> {
                            showToastInfo(
                                "Regular : Delivery of your Courier will be done within 1-3 hours*\n" +
                                        "Express : Delivery will be done on utmost Priority bases*\n" +
                                        "(* Timings of delivery may vary due to Distance and City Traffic )"
                            )
                        }
                        "btnProceed" -> {
                            //createOrderFirstBinding.edtDelMob.clearFocus()
                            // createOrderFirstBinding.edtPickMob.clearFocus()
                            pickupMobile = "" + createOrderFirstBinding.edtPickMob.text.trim()
                            delMobile = "" + createOrderFirstBinding.edtDelMob.text.trim()
                            if (TextUtils.isEmpty(
                                    pickupAddress
                                )
                            ) {
                                showToastError("Please select pickup location")
                            } else if (TextUtils.isEmpty(
                                    pickupMobile
                                )
                            ) {
                                createOrderFirstBinding.edtPickMob.requestFocus()
                                showToastError("Please enter pickup mobile no")
                            } else if (pickupMobile.length < 10) {
                                createOrderFirstBinding.edtPickMob.requestFocus()
                                showToastError("Please enter valid phone number")
                            } else if (TextUtils.isEmpty(
                                    pickupDate
                                )
                            ) {
                                showToastError("Please select date")
                            } else if (TextUtils.isEmpty(
                                    pickupDate
                                )
                            ) {
                                showToastError("Please select date")
                            } else if (TextUtils.isEmpty(
                                    pickTime
                                )
                            ) {
                                showToastError("Please select time")
                            } else if (TextUtils.isEmpty(
                                    delAddress
                                )
                            ) {
                                showToastError("Please select drop location")
                            } else if (TextUtils.isEmpty(
                                    delMobile
                                )
                            ) {
                                createOrderFirstBinding.edtDelMob.requestFocus()
                                showToastError("Please enter mobile no")
                            } else if (delMobile.length < 10) {
                                createOrderFirstBinding.edtDelMob.requestFocus()
                                showToastError("Please enter valid phone number")
                            }
                            /*if (TextUtils.isEmpty(
                                    pickupDate
                                ) || TextUtils.isEmpty(
                                    pickTime
                                ) || TextUtils.isEmpty(
                                    pickupMobile
                                ) || TextUtils.isEmpty(
                                    delAddress
                                ) || TextUtils.isEmpty(
                                    delMobile
                                ) || TextUtils.isEmpty(
                                    MyApplication.createOrdersInput.deliveryOption
                                ) || TextUtils.isEmpty(MyApplication.createOrdersInput.weight)
                                || TextUtils.isEmpty(MyApplication.createOrdersInput.distance) || MyApplication.createOrdersInput.distance.equals(
                                    "0"
                                )
                            ) {
                                showToastError("Please fill all details")
                            } else if (pickupMobile.length < 10) {
                                createOrderFirstBinding.edtPickMob.requestFocus()
                                showToastError("Please enter valid phone number")
                            } else if (delMobile.length < 10) {
                                createOrderFirstBinding.edtDelMob.requestFocus()
                                showToastError("Please enter valid phone number")
                            }*/ else {
                                val pickupAdd = CreateOrdersInput.PickupAddress()
                                pickupAdd.address = pickupAddress
                                pickupAdd.id = 0
                                pickupAdd.lat = pickLat
                                pickupAdd.long = pickLong
                                pickupAdd.date = pickupDate
                                pickupAdd.time = pickTime
                                pickupAdd.isComplete = "false"
                                pickupAdd.phoneNumber = pickupMobile
                                MyApplication.createOrdersInput.pickupAddress = pickupAdd
                                val deliveryAddressList =
                                    ArrayList<CreateOrdersInput.PickupAddress>()
                                var address = CreateOrdersInput.PickupAddress()
                                address.address = delAddress
                                address.lat = delLat
                                address.id = 0
                                address.long = delLong
                                address.isComplete = "false"
                                address.phoneNumber = delMobile
                                deliveryAddressList.add(address)
                                MyApplication.createOrdersInput.deliveryAddress =
                                    deliveryAddressList
                                var isAllDetailAded = true
                                for (item in addressList) {
                                    if (TextUtils.isEmpty(item.address) || TextUtils.isEmpty(
                                            item.phoneNumber
                                        ) || item.phoneNumber?.length!! < 10
                                    ) {
                                        isAllDetailAded = false
                                    }
                                }
                                MyApplication.createOrdersInput.deliveryAddress!!.addAll(
                                    addressList
                                )

                                if (isAllDetailAded) {
                                    (activity as CreateOrderActivty).callSecondFragment(2)
                                } else {
                                    showToastError("Please add details for drop locations")
                                }
                            }
                        }
                        "imgDelAddress" -> {
                            if (ContextCompat.checkSelfPermission(
                                    activity!!,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) !==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity!!,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                ) {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                } else {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                }
                            } else {
                                if (!TextUtils.isEmpty(createOrderFirstBinding.edtPickupLoc.text.toString())) {
                                    if (UtilsFunctions.isNetworkConnected()) {
                                        clickedForLocation = "delivery"
                                        val intent =
                                            Intent(activity, AddAddressActivity::class.java)
                                        startActivityForResult(intent, 200)
                                    }
                                } else {
                                    showToastError("Please select pickup address")
                                }
                            }
                        }
                        "imgPickContact" -> {
                            if (ContextCompat.checkSelfPermission(
                                    activity!!,
                                    Manifest.permission.READ_CONTACTS
                                ) !==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity!!,
                                        Manifest.permission.READ_CONTACTS
                                    )
                                ) {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.READ_CONTACTS), 1
                                    )
                                } else {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.READ_CONTACTS), 1
                                    )
                                }
                            } else {
                                if (UtilsFunctions.isNetworkConnected()) {
                                    clickedForLocation = "pickup"
                                    val intent = Intent(activity, ContactListActivity::class.java)
                                    startActivityForResult(intent, 201)
                                }
                            }

                        }
                        "imgPickupAddress" -> {
                            if (ContextCompat.checkSelfPermission(
                                    activity!!,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) !==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity!!,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    )
                                ) {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                } else {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                                    )
                                }
                            } else {
                                if (TextUtils.isEmpty(createOrderFirstBinding.edtDelAddress.text.toString())) {
                                    if (UtilsFunctions.isNetworkConnected()) {
                                        clickedForLocation = "pickup"
                                        val intent =
                                            Intent(activity, AddAddressActivity::class.java)
                                        startActivityForResult(intent, 200)
                                    }
                                } else {
                                    showCleareDataDialog(
                                        "If you change pickup address, then your delivery locations will be removed.",
                                        true
                                    )
                                }
                            }
                        }
                        "imgDelContact" -> {
                            if (ContextCompat.checkSelfPermission(
                                    activity!!,
                                    Manifest.permission.READ_CONTACTS
                                ) !==
                                PackageManager.PERMISSION_GRANTED
                            ) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        activity!!,
                                        Manifest.permission.READ_CONTACTS
                                    )
                                ) {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.READ_CONTACTS), 1
                                    )
                                } else {
                                    ActivityCompat.requestPermissions(
                                        activity!!,
                                        arrayOf(Manifest.permission.READ_CONTACTS), 1
                                    )
                                }
                            } else {
                                if (UtilsFunctions.isNetworkConnected()) {
                                    clickedForLocation = "delivery"
                                    val intent = Intent(activity, ContactListActivity::class.java)
                                    startActivityForResult(intent, 201)
                                }
                            }

                        }
                        "edtPickupLoc" -> {
                            clickedForLocation = "pickup"
                            // Set the fields to specify which types of place data to
                            // return after the user has made a selection.
                            if (TextUtils.isEmpty(createOrderFirstBinding.edtDelAddress.text.toString())) {
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
                            if (!TextUtils.isEmpty(createOrderFirstBinding.edtPickupLoc.text.toString())) {
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
                        "txtAddAddress" -> {
                            // createOrderFirstBinding.edtDelMob.clearFocus()
                            // delMobile = "9865201201"
                            delMobile = "" + createOrderFirstBinding.edtDelMob.text
                            if (TextUtils.isEmpty(delAddress) || TextUtils.isEmpty(delMobile)
                            ) {
                                showToastError("Please add all delivery details")
                            } else if (addressList.size == 0) {
                                addAddressView(null)
                            } else {
                                if (TextUtils.isEmpty(
                                        addressList[addressList.size - 1].address
                                    ) || TextUtils.isEmpty(addressList[addressList.size - 1].phoneNumber)
                                ) {
                                    showToastError("Please add all fields")
                                } else {
                                    addAddressView(null)
                                }
                            }
                        }
                        "txtRegular" -> {
                            deliveryType = "1"
                            createOrderFirstBinding.txtExpress.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_del_type_unselected
                                )
                            )
                            createOrderFirstBinding.txtRegular.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_del_type_selected
                                )
                            )
                            //slot()
                        }
                        "txtExpress" -> {
                            deliveryType = "2"
                            createOrderFirstBinding.txtRegular.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_del_type_unselected
                                )
                            )
                            createOrderFirstBinding.txtExpress.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_del_type_selected
                                )
                            )
                        }
                        "imgPriceDetails" -> {
                            if (createOrderFirstBinding.llPriceDetail.visibility == View.GONE) {
                                createOrderFirstBinding.imgPriceDetails.setImageResource(
                                    R.drawable.ic_down_arrow
                                )
                                createOrderFirstBinding.llPriceDetail.visibility = View.VISIBLE
                                createOrderFirstBinding.llPriceDetail.bringToFront()

                            } else {
                                createOrderFirstBinding.imgPriceDetails.setImageResource(
                                    R.drawable.ic_up_arrow
                                )
                                createOrderFirstBinding.llPriceDetail.visibility = View.GONE
                            }
                            ///showToastSuccess("Show Details")
                            // showCancelOrdersDialog()
                        }
                        /**/
                    }
                })
        )

    }

    private fun preFilledData() {
        //createOrderFirstBinding.orderDetailModel = response.data
        if (!TextUtils.isEmpty(MyApplication.createOrdersInput.pickupAddress?.phoneNumber)) {
            pickupMobile = MyApplication.createOrdersInput.pickupAddress?.phoneNumber as String
            pickupDate = MyApplication.createOrdersInput.pickupAddress?.date as String
            pickupAddress = MyApplication.createOrdersInput.pickupAddress?.address as String
            pickLat = MyApplication.createOrdersInput.pickupAddress?.lat as String
            pickLong = MyApplication.createOrdersInput.pickupAddress?.long as String
            pickTime = MyApplication.createOrdersInput.pickupAddress?.time as String
            createOrderFirstBinding.edtPickupLoc.setText(pickupAddress)
            createOrderFirstBinding.edtPickMob.setText(pickupMobile)
            val date = Utils(activity!!).getDateLocal(
                "EEE MMM dd HH:mm:ss zzzz yyyy",
                getDaysAgo(0).toString(),
                "MM/dd/YYYY"
            )

            if (pickupDate.equals(date)) {
                createOrderFirstBinding.txtDate.setSelection(0)
            } else {
                createOrderFirstBinding.txtDate.setSelection(1)
            }


            if (MyApplication.createOrdersInput.deliveryAddress?.size!! > 0) {
                delMobile =
                    MyApplication.createOrdersInput.deliveryAddress!![0].phoneNumber as String
                //delDate = MyApplication.createOrdersInput.deliveryAddress!![0].date as String
                delAddress =
                    MyApplication.createOrdersInput.deliveryAddress!![0].address as String
                delLat = MyApplication.createOrdersInput.deliveryAddress!![0].lat as String
                delLong = MyApplication.createOrdersInput.deliveryAddress!![0].long as String
                //delTime = MyApplication.createOrdersInput.deliveryAddress!![0].time as String
                createOrderFirstBinding.edtDelAddress.setText(MyApplication.createOrdersInput.deliveryAddress!![0].address as String)
                createOrderFirstBinding.edtDelMob.setText(MyApplication.createOrdersInput.deliveryAddress!![0].phoneNumber as String)
                for (index in 1 until MyApplication.createOrdersInput.deliveryAddress?.size!!) {
                    addAddressView(MyApplication.createOrdersInput.deliveryAddress!![index])
                }


                for (weight in weightList) {
                    weight.selected = "false"
                    if (weight.id.equals(MyApplication.createOrdersInput.weight)) {
                        weight.selected = "true"
                        weightId = weight.id.toString()
                        weighValue = weight.name!!
                        createOrderInput.weight = weightId
                    }
                }
                weightAdapter?.notifyDataSetChanged()


                for (deliveryType in deliveryTypeList) {
                    deliveryType.selected = "false"
                    if (deliveryType.id.equals(MyApplication.createOrdersInput.deliveryOption)) {
                        deliveryType.selected = "true"
                        deliveryTypeId = deliveryType.id.toString()
                        // deliveryValue = deliveryType.title!!
                        createOrderInput.deliveryOption = deliveryTypeId
                    }
                }
                deliveryTypeAdapter?.notifyDataSetChanged()


                calculatePrice()

            }
        }
    }

    //region Adapters
    private fun initWeightAdapter() {
        weightAdapter =
            WeightListAdapter(
                this,
                weightList
            )
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_bottom)
        createOrderFirstBinding.rvWeight.setLayoutAnimation(controller);
        createOrderFirstBinding.rvWeight.scheduleLayoutAnimation();
        createOrderFirstBinding.rvWeight.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        createOrderFirstBinding.rvWeight.layoutManager = linearLayoutManager
        createOrderFirstBinding.rvWeight.setHasFixedSize(true)
        createOrderFirstBinding.rvWeight.adapter = weightAdapter
        createOrderFirstBinding.rvWeight.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {
            }
        })
    }

    fun selectedWeight(position : Int, s : String) {
        for (i in 0 until weightList.count()) {
            weightList[i].selected = "false"
        }
        weightList[position].selected = "true"
        weightId = weightList[position].id!!
        GlobalConstants.WEIGHT_ID = weightId
        weightAdapter?.notifyDataSetChanged()
        createOrderInput.weight = weightId
        weighValue = weightList[position].name!!
        MyApplication.createOrdersInput.weight = weightId
        MyApplication.createOrdersInput.weightValue = weightList[position].name!!
        calculatePrice()
    }

    private fun initDeliveryTypeAdapter() {
        deliveryTypeAdapter =
            DeliveryTypesAdapter(
                this,
                deliveryTypeList
            )
        val controller =
            AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_bottom)
        createOrderFirstBinding.rvDeliveryTypes.setLayoutAnimation(controller);
        createOrderFirstBinding.rvDeliveryTypes.scheduleLayoutAnimation();
        createOrderFirstBinding.rvDeliveryTypes.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        createOrderFirstBinding.rvDeliveryTypes.layoutManager = linearLayoutManager
        createOrderFirstBinding.rvDeliveryTypes.setHasFixedSize(true)
        createOrderFirstBinding.rvDeliveryTypes.adapter = deliveryTypeAdapter
        createOrderFirstBinding.rvDeliveryTypes.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    fun selectedDeliveryType(position : Int, isSelected : String) {
        for (i in 0 until deliveryTypeList.count()) {
            deliveryTypeList[i].selected = "false"
        }
        deliveryTypeList[position].selected = "true"
        //  if (isSelected.equals("true")) {
        deliveryTypeId = deliveryTypeList[position].id!!
        GlobalConstants.DELIVERY_TYPE = deliveryTypeId
        MyApplication.createOrdersInput.deliveryOption = deliveryTypeId
        if (deliveryTypeList[position].title!!.contains("Reg")) {
            MyApplication.createOrdersInput.deliveryValue = "1"
        } else {
            MyApplication.createOrdersInput.deliveryValue = "2"
        }

        calculatePrice()
        /*  } else {
              deliveryTypeId = ""
          }*/

        deliveryTypeAdapter?.notifyDataSetChanged()
    }

    //endregion
//region ADD MULTIPLE ADDRESS
    private fun addAddressView(pickupAddress : CreateOrdersInput.PickupAddress?) {
        i = i + 1
        val addressModel = CreateOrdersInput.PickupAddress()
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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



        createOrderFirstBinding.llAddress.addView(rowView)
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
            //edtDelDateTime.setText(addressModel.date + " " + addressModel.time)

        }


        addressList.add(addressModel)

        if (addressList.size < 4) {
            createOrderFirstBinding.txtAddAddress.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.txtAddAddress.visibility = View.GONE
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
                        Intent(activity, ContactListActivity::class.java)
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
                    val intent1 = Intent(activity, AddAddressActivity::class.java)
                    startActivityForResult(intent1, 200)
                }
            }
        })


        removeAddress.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v : View?) {
                val tag = v?.id as Int
                val view1 : View = view!!.findViewById(tag)

                createOrderFirstBinding.llAddress.removeView(view1)
                for (items in addressList) {
                    if (tag == items.id) {
                        addressList.remove(items)
                        break
                    }
                }
                distance = "0"
                callDistanceAPI()
                if (addressList.size < 4) {
                    createOrderFirstBinding.txtAddAddress.visibility = View.VISIBLE
                } else {
                    createOrderFirstBinding.txtAddAddress.visibility = View.GONE
                }

            }
        })


        edtDelMob.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus->
            if (!hasFocus) { // code to execute when EditText loses focus
                val tag = v?.id as Int
                if (!TextUtils.isEmpty(edtDelMob.text.toString()) && edtDelMob.text.length < 10) {
                    // edtDelMob.requestFocus()
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

        edtDelMob.setOnEditorActionListener { v, actionId, event->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // doSomething()
                // val tag = v?.id as Int
                if (!TextUtils.isEmpty(edtDelMob.text.toString()) && edtDelMob.text.length < 10) {
                    //edtDelMob.requestFocus()
                    edtDelMob.error =
                        getString(R.string.mob_no) + " " + getString(R.string.phone_min)
                    /* } else {
                         for (items in addressList) {
                             if (tag == items.id) {
                                 // addressList.remove(items)
                                 items.phoneNumber = edtDelMob.text.toString()
                                 break
                             }
                         }*/

                } else {
                    // edtDelMob.clearFocus()
                    val imm =
                        activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
                true
            } else {
                false
            }
        }
        //setListeners(rowView, true, addressList.size.minus(1))
        var textWatcher : TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s : CharSequence,
                start : Int,
                count : Int,
                after : Int
            ) {
            }

            override fun onTextChanged(
                s : CharSequence,
                start : Int,
                before : Int,
                count : Int
            ) {
                if (!TextUtils.isEmpty(s.toString())) {
                }

            }

            override fun afterTextChanged(s : Editable) {
                if (s.hashCode() == edtDelMob.text.hashCode()) {
                    val tag = edtDelMob.id as Int

                    for (items in addressList) {
                        if (tag == items.id) {
                            // addressList.remove(items)
                            items.phoneNumber = edtDelMob.text.toString()
                            break
                        }
                    }

                }

            }
        }
        edtDelMob.addTextChangedListener(textWatcher)
    }

    fun clearAddressData() {
        if (createOrderFirstBinding.llAddress.getChildCount() > 0) {
            createOrderFirstBinding.llAddress.removeAllViews()
        }
        //activityCreateOrderBinding . llAddress . removeAllviews ()
        //createOrderFirstBinding.edtDelDateTime.setText("")
        createOrderFirstBinding.edtDelAddress.setText("")
        createOrderFirstBinding.edtDelMob.setText("")
        delAddress = ""
        delMobile = ""
        delDate = ""
        delTime = ""
        delLat = ""
        delLong = ""
        addressList.clear()
        distance = "0"
        MyApplication.createOrdersInput.distance = "0"
    }

    fun clearDataDialog(message : String) {
        confirmationDialog = mDialogClass?.setDefaultDialog(
            activity!!,
            this@CreateOrderFirstFragment,
            "clearData",
            message
        )
        confirmationDialog?.show()
    }

    fun getDaysAgo(daysAgo : Int) : Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysAgo)

        return calendar.time
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
//region PLACES API
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
            .build(activity!!)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    //endregion
//region ONACTIVITY FOR RESULT
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        // activityCreateOrderBinding.autocompleteFragment.visibility = View.GONE
        val inputManager : InputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        /* inputManager.hideSoftInputFromWindow(
             activity!!.currentFocus.windowToken,
             InputMethodManager.SHOW_FORCED
         )*/

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
                            createOrderFirstBinding.edtPickupLoc.setText(place.address.toString())
                            pickupAddress = place.address.toString()
                            pickLat = place.latLng!!.latitude.toString()
                            pickLong = place.latLng!!.longitude.toString()

                            MyApplication.createOrdersInput.pickupAddress?.address =
                                place.address.toString()
                            MyApplication.createOrdersInput.address1 =
                                place.address.toString()
                            MyApplication.createOrdersInput.pickupAddress?.lat =
                                place.latLng!!.latitude.toString()
                            MyApplication.createOrdersInput.pickupAddress?.long =
                                place.latLng!!.longitude.toString()
                            createOrderFirstBinding.edtPickMob.requestFocus()
                        } else if (clickedForLocation.equals("delivery")) {
                            calculatePrice()
                            createOrderFirstBinding.edtDelAddress.setText(place.address.toString())
                            delAddress = place.address.toString()
                            delLat = place.latLng!!.latitude.toString()
                            delLong = place.latLng!!.longitude.toString()
                            distance = "0"
                            callDistanceAPI()
                            createOrderFirstBinding.edtDelMob.requestFocus()
                            //}
                        } else {
                            calculatePrice()
                            edtDelAddress?.setText(place.address.toString())
                            val tag = edtDelAddress?.id
                            for (items in addressList) {
                                if (tag == items.id) {
                                    items.address = place.address.toString()
                                    items.lat =
                                        place.latLng!!.latitude.toString()
                                    items.long =
                                        place.latLng!!.longitude.toString()
                                    break
                                }
                            }
                            distance = "0"
                            callDistanceAPI()
                            //}
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
                    createOrderFirstBinding.edtPickupLoc.setText(address)
                    pickLat = lat as String
                    pickLong = long.toString()
                    pickupAddress = address.toString()
                    MyApplication.createOrdersInput.pickupAddress?.address = pickupAddress
                    MyApplication.createOrdersInput.address1 = pickupAddress

                    MyApplication.createOrdersInput.pickupAddress?.lat = pickLat
                    MyApplication.createOrdersInput.pickupAddress?.long = pickLong
                    createOrderFirstBinding.edtPickMob.requestFocus()
                } else if (clickedForLocation.equals("delivery")) {
                    calculatePrice()
                    createOrderFirstBinding.edtDelAddress.setText(address)
                    delLat = lat as String
                    delLong = long.toString()
                    delAddress = address.toString()
                    //}
                    distance = "0"
                    callDistanceAPI()
                    createOrderFirstBinding.edtDelMob.requestFocus()
                } else {
                    calculatePrice()
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
                    //}
                    distance = "0"
                    callDistanceAPI()
                }

            }

        } else if (requestCode == 201) {
            val num = data?.getStringExtra("num")
            if (!TextUtils.isEmpty(num)) {
                if (clickedForLocation.equals("pickup")) {
                    if (num?.length!! > 9) {
                        createOrderFirstBinding.edtPickMob.setText(num)
                        pickupMobile = num.toString()
                        MyApplication.createOrdersInput.pickupAddress?.phoneNumber = pickupMobile
                    } else {
                        showToastError("Please select valid number")
                    }
                    createOrderFirstBinding.edtPickMob.requestFocus()
                } else if (clickedForLocation.equals("delivery")) {
                    if (num?.length!! > 9) {
                        createOrderFirstBinding.edtDelMob.setText(num)
                        delMobile = num.toString()
                        // createOrderFirstBinding.edtDelMob?.clearFocus()
                    } else {
                        showToastError("Please select valid number")
                    }
                    createOrderFirstBinding.edtDelMob.requestFocus()

                } else {
                    if (num?.length!! > 9) {
                        edtDeliveryMob?.setText(num.toString())
                        val tag = edtDeliveryMob?.id
                        for (items in addressList) {
                            if (tag == items.id) {
                                items.phoneNumber = num
                                break
                            }
                        }
                        // edtDeliveryMob?.clearFocus()
                    } else {
                        showToastError("Please select valid number")
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun callDistanceAPI(/*latLng : LatLng*/) {
        var desLoc = ""
        var origin = ""
        val mapQuery = HashMap<String?, String?>()
        mapQuery.put("units", "imperial");
        if (TextUtils.isEmpty(distance) || distance.equals("0")) {
            pos = 0
            origin = pickLat + "," + pickLong
            mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
            desLoc = desLoc + "|" + delLat /*latLng . latitude*/ + "," + delLong/*latLng.longitude*/
            mapQuery.put("destinations", desLoc)
        } else {
            if (pos == 0) {
                pos += 1
                origin = delLat /*latLng . latitude*/ + "," + delLong
                mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
                val lat = addressList[0].lat
                val long = addressList[0].long
                desLoc = desLoc + "|" + lat + "," + long
                mapQuery.put("destinations", desLoc)
            } else {
                val startLat = addressList[pos - 1].lat
                val startLong = addressList[pos - 1].long
                origin = startLat /*latLng . latitude*/ + "," + startLong
                mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
                val lat = addressList[pos].lat
                val long = addressList[pos].long
                desLoc = desLoc + "|" + lat + "," + long
                mapQuery.put("destinations", desLoc)
                if (TextUtils.isEmpty(lat) || lat.equals("null")) {
                    return
                }
                pos += 1
            }

        }

        mapQuery.put("key", getString(R.string.maps_api_key))
        val requestQueue = Volley.newRequestQueue(activity)
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
                            if (rows[0] != null) {
                                if (rows[0].elements != null) {
                                    val elements = rows[0].elements
                                    for (i in 0 until elements!!.size) {
                                        if (elements[i].distance != null) {
                                            distancInMeter =
                                                distancInMeter.plus(elements[i].distance?.value!!.toDouble()/*distanceInMeterArray!![0].toDouble()*/)
                                        }

                                    }
                                }
                            }

                        }
                    }
                    val distancInKm = distancInMeter / 1000
                    // showToastSuccess(distancInKm.toString())
                    distance = distancInKm.toDouble().plus(distance.toDouble()).toString()
                    MyApplication.createOrdersInput.distance = distance
                    // distance = distancInKm.toString()
                    // if (distancInKm < 25) {
                    if (addressList != null && addressList.size > 0) {
                        if (pos < addressList.size || pos == 0) {
                            callDistanceAPI()
                        }
                    }
                    GlobalConstants.DISTANCE = distance
                    calculatePrice()
                    // latLongArrayList.add(latLng)
                    /* if (clickedForLocation.equals("pickup")) {
                         //latLongArrayList.add(place.latLng!!)
                         createOrderFirstBinding.edtPickupLoc.setText(selectedAddress)
                         pickupAddress = selectedAddress
                         pickLat = selectedlatLong.latitude.toString()
                         pickLong = selectedlatLong.longitude.toString()
                     } else if (clickedForLocation.equals("delivery")) {
                         createOrderFirstBinding.edtDelAddress.setText(selectedAddress)
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

                     }*/
                } catch (e : JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error-> error.printStackTrace() })
        requestQueue?.add(request)
        //}
    }

    //endregion
    fun showCleareDataDialog(s : String, istwoHours : Boolean) {
        var confirmationDialog = Dialog(activity, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
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
        val animation = AnimationUtils.loadAnimation(
            activity
            , R.anim.anim
        )
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()
        txtTitle.setText(s)
        yes.setOnClickListener {
            clearAddressData()
            confirmationDialog.dismiss()
        }
        no.setOnClickListener {
            confirmationDialog.dismiss()
        }
        confirmationDialog.show()
    }

    //region TIME SLOTS
    private fun slot(position : Int) : ArrayList<String> {
        val selectedDate = "2-12-2020"
        val selectedDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputDate = selectedDateFormat.parse(selectedDate)
        val cal1 = Calendar.getInstance()
        val cal = Calendar.getInstance()
        if (position == 0/*DateUtils.isToday(outputDate.time)*/) {
            if (cal.get(Calendar.MINUTE) >= 30)
                cal.add(Calendar.HOUR_OF_DAY, 2);
            else
                cal.add(Calendar.HOUR_OF_DAY, 1)

            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
        } else {
            cal.add(Calendar.DATE, 1)
            cal1.add(Calendar.DATE, 1)
            cal.set(Calendar.HOUR_OF_DAY, 6)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)

        }
        val timeList = ArrayList<String>()
        timeList.add("Select Time")


        cal1.set(Calendar.HOUR_OF_DAY, 23)
        cal1.set(Calendar.MINUTE, 0)
        cal1.set(Calendar.SECOND, 0)
        var diff = cal.timeInMillis
        while (diff < cal1.timeInMillis) {
            var startdate = Date(diff)
            val sdf = SimpleDateFormat("hh:mmaa")
            var start = sdf.format(startdate.time)
            diff += 1800000
            var enddate = Date(diff)
            var end = sdf.format(enddate.time)
            var time = start + " - " + end
            timeList.add(time)

        }
        return timeList
        Log.d("data", timeList.toString())
    }

    //endregion
//region CALCULATE PRICE
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
                Log.e("Price Calculate First", "" + mJsonObject)
                orderViewModel.calculatePrice(mJsonObject)
                //  showToastSuccess("api call")
            }
        }
    }

    //endregion
    fun setPrice(
        payableAmount : String,
        deliveryFee : String,
        weightFee : String,
        securityFee : String,
        couponDeduction : String,
        totalCancellationCharges : String,
        from : String,
        deliveryTypeCharges : String?
    ) {
        /*if (from.equals("first")) {
            createOrderFirstBinding.viewLine1.setAlpha(1f)
            createOrderFirstBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
        } else {
            createOrderFirstBinding.viewLine1.setAlpha(1f)
            createOrderFirstBinding.viewLine1.background =
                ColorDrawable(Color.parseColor("#ffffff"))
            createOrderFirstBinding.viewLine2.setAlpha(1f)
            createOrderFirstBinding.viewLine2.background =
                ColorDrawable(Color.parseColor("#ffffff"))

        }*/

        if (!deliveryFee.equals("0") && !deliveryFee.equals("")) {
            createOrderFirstBinding.txtDelivery.text = "₹ " + deliveryFee
            createOrderFirstBinding.view1.visibility = View.VISIBLE
            createOrderFirstBinding.rlDelivery.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlDelivery.visibility = View.GONE
            createOrderFirstBinding.view1.visibility = View.GONE
        }
        if (!weightFee.equals("0") && !weightFee.equals("")) {
            createOrderFirstBinding.txtWeight.text = "₹ " + weightFee
            createOrderFirstBinding.view2.visibility = View.VISIBLE
            createOrderFirstBinding.rlWeight.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlWeight.visibility = View.GONE
            createOrderFirstBinding.view2.visibility = View.GONE
        }
        if (!securityFee.equals("0") && !securityFee.equals("") && !securityFee.equals("null")) {
            createOrderFirstBinding.txtSecurity.text = "₹ " + securityFee
            createOrderFirstBinding.view3.visibility = View.VISIBLE
            createOrderFirstBinding.rlSecurity.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlSecurity.visibility = View.GONE
            createOrderFirstBinding.view3.visibility = View.GONE
        }

        if (!totalCancellationCharges.equals("0") && !totalCancellationCharges.equals("")) {
            createOrderFirstBinding.txtCancellation.text = "₹ " + totalCancellationCharges
            createOrderFirstBinding.view4.visibility = View.VISIBLE
            createOrderFirstBinding.rlCancel.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlCancel.visibility = View.GONE
            createOrderFirstBinding.view4.visibility = View.GONE
        }
        if (!couponDeduction.equals("0") && !couponDeduction.equals("")) {
            createOrderFirstBinding.txtCouponDeduction.text = "₹ " + couponDeduction
            createOrderFirstBinding.rlCouponDeduction.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlCouponDeduction.visibility = View.GONE
        }

        if (!deliveryTypeCharges.equals("0") && !deliveryTypeCharges.equals("")) {
            createOrderFirstBinding.view5.visibility = View.VISIBLE
            createOrderFirstBinding.txtDeliveryTypeCharges.text = "₹ " + deliveryTypeCharges
            createOrderFirstBinding.rlDeliveryTypeCharges.visibility = View.VISIBLE
        } else {
            createOrderFirstBinding.rlDeliveryTypeCharges.visibility = View.GONE
            createOrderFirstBinding.view5.visibility = View.GONE
        }

        createOrderFirstBinding.txtFare.text = "₹ " + payableAmount
    }

    override fun onRequestPermissionsResult(
        requestCode : Int, permissions : Array<String>,
        grantResults : IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ===
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(activity!!, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}