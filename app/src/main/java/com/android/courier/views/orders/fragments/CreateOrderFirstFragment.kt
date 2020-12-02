package com.android.courier.views.orders.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.courier.databinding.FragmentCreateOrdersFirstBinding
import com.android.courier.R
import com.android.courier.adapters.orders.DeliveryTypesAdapter
import com.android.courier.adapters.orders.WeightListAdapter
import com.android.courier.common.UtilsFunctions
import com.android.courier.common.UtilsFunctions.showToastError
import com.android.courier.constants.GlobalConstants
import com.android.courier.maps.FusedLocationClass
import com.android.courier.model.order.*
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.contacts.ContactListActivity
import com.android.courier.views.home.LandingActivty
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
    var distance = "25"
    var deliveryType = "1"
    var deliveryTypeList = ArrayList<ListsResponse.DeliveryOptionData>()
    var deliveryTypeAdapter : DeliveryTypesAdapter? = null
    var deliveryTypeId = ""
    private var isUsableLoyalty = false
    var discountApplied = ""
    var cancelOrderIds = ""
    private lateinit var cancelOrderDetail : CancelOrder
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_create_orders_first
    }

    override fun onResume() {
        super.onResume()

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        createOrderFirstBinding = viewDataBinding as FragmentCreateOrdersFirstBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        createOrderFirstBinding.orderViewModel = orderViewModel
        // categoriesList=List<Service>()
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        val name = SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERNAME).toString()
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()

        orderViewModel.getListsRes().observe(this,
            Observer<ListsResponse> { response->
                baseActivity.stopProgressDialog()
                /* if (!TextUtils.isEmpty(orderId) && !orderId.equals("null")) {
                     if (UtilsFunctions.isNetworkConnected()) {
                         startProgressDialog()
                         orderViewModel.orderDetail(orderId)
                         //orderViewModel.cancelReason(orderId)
                     }
                 }*/

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            vehicleList.addAll(response.data?.vehicleData!!)
                            weightList.addAll(response.data?.weightData!!)
                            initWeightAdapter()
                            // bannersList.addAll(response.data?.bannersData!!)
                            deliveryTypeList.addAll(response.data?.deliveryOptionData!!)
                            initDeliveryTypeAdapter()
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
                            val deliveryCharges = response.data?.beforeDiscount.toString()
                            val payableAmount = response.data?.afterDiscount.toString()
                            var totalCancellationCharges = "0"

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

                            (activity as CreateOrderActivty).setPrice(
                                payableAmount,
                                "deliveryFee",
                                "weightFee",
                                "securityFee",
                                "couponDeduction",
                                totalCancellationCharges
                            )
                        }
                        else -> message?.let {
                            // activityCreateOrderBinding.txtDelCharges.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })
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
                /* if (position != 0) {
                 } else {
                 }*/

            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }
        val time = slot()
        val adapter1 = ArrayAdapter(
            activity!!,
            R.layout.spinner_item, time
        )
        createOrderFirstBinding.txtTime.adapter = adapter1
        createOrderFirstBinding.txtTime.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                /* if (position != 0) {
                 } else {
                 }*/

            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }

        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgDelAddress" -> {
                        if (UtilsFunctions.isNetworkConnected()) {
                            clickedForLocation = "delivery"
                            val intent = Intent(activity, AddAddressActivity::class.java)
                            startActivityForResult(intent, 200)
                        }
                    }
                    "imgPickupAddress" -> {
                        if (UtilsFunctions.isNetworkConnected()) {
                            clickedForLocation = "pickup"
                            val intent = Intent(activity, AddAddressActivity::class.java)
                            startActivityForResult(intent, 200)
                        }
                    }
                    "imgPickContact" -> {
                        if (TextUtils.isEmpty(createOrderFirstBinding.edtDelAddress.text.toString())) {
                            if (UtilsFunctions.isNetworkConnected()) {
                                clickedForLocation = "pickup"
                                val intent = Intent(activity, ContactListActivity::class.java)
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
                            val intent = Intent(activity, ContactListActivity::class.java)
                            startActivityForResult(intent, 201)
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
                        createOrderFirstBinding.edtDelMob.clearFocus()
                        if (TextUtils.isEmpty(delAddress) || TextUtils.isEmpty(delMobile)
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
                }
            })
        )

    }

    //region Adapters
    private fun initWeightAdapter() {
        weightAdapter =
            WeightListAdapter(
                this,
                weightList
            )
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
        weightAdapter?.notifyDataSetChanged()
        createOrderInput.weight = weightId
        weighValue = weightList[position].name!!
        calculatePrice()
    }

    private fun initDeliveryTypeAdapter() {
        deliveryTypeAdapter =
            DeliveryTypesAdapter(
                this,
                deliveryTypeList
            )
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
        calculatePrice()
        /*  } else {
              deliveryTypeId = ""
          }*/

        deliveryTypeAdapter?.notifyDataSetChanged()
    }

    //endregion
    //region ADD MULTIPLE ADDRESS
    private fun addAddressView(pickupAddress : OrdersDetailResponse.PickupAddress?) {
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
            edtDelDateTime.setText(addressModel.date + " " + addressModel.time)

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
        //setListeners(rowView, true, addressList.size.minus(1))
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
        inputManager.hideSoftInputFromWindow(
            activity!!.currentFocus.windowToken,
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
                            createOrderFirstBinding.edtPickupLoc.setText(place.address.toString())
                            pickupAddress = place.address.toString()
                            pickLat = place.latLng!!.latitude.toString()
                            pickLong = place.latLng!!.longitude.toString()

                        } else if (clickedForLocation.equals("delivery")) {
                            calculatePrice()
                            createOrderFirstBinding.edtDelAddress.setText(place.address.toString())
                            delAddress = place.address.toString()
                            delLat = place.latLng!!.latitude.toString()
                            delLong = place.latLng!!.longitude.toString()
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
                } else if (clickedForLocation.equals("delivery")) {
                    calculatePrice()
                    createOrderFirstBinding.edtDelAddress.setText(address)
                    delLat = lat as String
                    delLong = long.toString()
                    delAddress = address.toString()
                    //}

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
                }
            }

        } else if (requestCode == 201) {
            val num = data?.getStringExtra("num")
            if (!TextUtils.isEmpty(num)) {
                if (clickedForLocation.equals("pickup")) {
                    createOrderFirstBinding.edtPickMob.setText(num)
                    pickupMobile = num.toString()
                } else if (clickedForLocation.equals("delivery")) {
                    createOrderFirstBinding.edtDelMob.setText(num)
                    delMobile = num.toString()
                    createOrderFirstBinding.edtDelMob?.clearFocus()
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
        if (distance.isEmpty()) {
            mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
            desLoc = desLoc + "|" + latLng.latitude + "," + latLng.longitude
            mapQuery.put("destinations", desLoc)
        } else {
            var pos = 0
            if (pos == 0) {
            }
            mapQuery.put("origins", pickLat + "," + pickLong/*latLongArrayList[0].toString()*/)
            desLoc = desLoc + "|" + latLng.latitude + "," + latLng.longitude
            mapQuery.put("destinations", desLoc)
        }

        mapQuery.put("key", getString(R.string.maps_api_key))
        val origin = pickLat + "," + pickLong
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
                    // if (distancInKm < 25) {
                    //TODO--calculatePrice()
                    latLongArrayList.add(latLng)
                    if (clickedForLocation.equals("pickup")) {
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

                    }
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
    }

    //region TIME SLOTS
    private fun slot() : ArrayList<String> {
        val selectedDate = "2-12-2020"
        val selectedDateFormat = SimpleDateFormat("dd-MM-yyyy")
        val outputDate = selectedDateFormat.parse(selectedDate)
        val cal1 = Calendar.getInstance()
        val cal = Calendar.getInstance()
        if (DateUtils.isToday(outputDate.time)) {
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
            var time = start + "-" + end
            timeList.add(time)

        }
        return timeList
        Log.d("data", timeList.toString())
    }

    //endregion
    //region CALCULATE PRICE
    fun calculatePrice() {
        if (/*!TextUtils.isEmpty(vehicleId) && */!TextUtils.isEmpty(deliveryTypeId) && !TextUtils.isEmpty(
                distance
            ) && !TextUtils.isEmpty(
                weightId
            ) /*&& !TextUtils.isEmpty(activityCreateOrderBinding.edtParcelValue.text.toString())*/
        ) {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "parcelValue",
                "100"/* activityCreateOrderBinding.edtParcelValue.text.toString()*/
            )
            if (orderId.equals("null")) {
                orderId = ""
            }
            mJsonObject.addProperty("orderId", orderId)
            mJsonObject.addProperty("vehicleId", "9c9d2c0e-02d6-4095-a0b4-b267b736dd65")
            mJsonObject.addProperty("deliveryId", deliveryTypeId)
            mJsonObject.addProperty("weightId", weightId)
            mJsonObject.addProperty("distance", distance)
            mJsonObject.addProperty("isUseLoyality", isUsableLoyalty)

            mJsonObject.addProperty(
                "discount",
                discountApplied
            )
            if (UtilsFunctions.isNetworkConnected()) {
                //  (activity as CreateOrderActivty).callPriceCalculationApi(mJsonObject)
                orderViewModel.calculatePrice(mJsonObject)
                //  showToastSuccess("api call")
            }
        }
    }
    //endregion
}