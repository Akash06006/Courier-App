package com.example.courier.views.orders.fragments

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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.courier.databinding.FragmentOrdersBinding
import com.example.courier.R
import com.example.courier.adapters.orders.OrdersListAdapter
import com.example.courier.common.UtilsFunctions
import com.example.courier.common.UtilsFunctions.showToastError
import com.example.courier.common.UtilsFunctions.showToastSuccess
import com.example.courier.constants.GlobalConstants
import com.example.courier.maps.FusedLocationClass
import com.example.courier.model.CommonModel
import com.example.courier.model.order.CancelReasonsListResponse
import com.example.courier.model.order.OrdersListResponse
import com.example.courier.sharedpreference.SharedPrefClass
import com.example.courier.utils.BaseFragment
import com.example.courier.viewmodels.order.OrderViewModel
import com.example.courier.views.home.LandingActivty
import com.google.android.gms.location.*
import com.google.gson.JsonObject

class
OrdersFragment : BaseFragment() {
    private var mFusedLocationClass : FusedLocationClass? = null
    private lateinit var orderViewModel : OrderViewModel
    val PERMISSION_ID = 42
    var orderList = ArrayList<OrdersListResponse.Data>()
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var isActive = "true"
    var orderType = "active"
    var reasons = java.util.ArrayList<String>()
    private lateinit var fragmentOrdersBinding : FragmentOrdersBinding
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_orders
    }

    override fun onResume() {
        super.onResume()

        if (UtilsFunctions.isNetworkConnected()) {
            baseActivity.startProgressDialog()
            orderViewModel.getOrderList(orderType)
           // orderViewModel.cancelReason("reason")
        }

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentOrdersBinding = viewDataBinding as FragmentOrdersBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        fragmentOrdersBinding.orderViewModel = orderViewModel
        // categoriesList=List<Service>()
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        val name = SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERNAME).toString()
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()
        Glide.with(activity!!).load(userImage).placeholder(R.drawable.ic_user)
            .into(fragmentOrdersBinding.imgRight)

        fragmentOrdersBinding.txtWelcome.setText("Welcome, " + name)

        reasons.add("Select Reason")
        if (UtilsFunctions.isNetworkConnected()) {
            // baseActivity.startProgressDialog()
            // orderViewModel.getOrderList(orderType)
            orderViewModel.cancelReason("reason")
        }
        orderViewModel.orderListRes().observe(this,
            Observer<OrdersListResponse> { response->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            orderList.clear()
                            orderList.addAll(response.data!!)
                            if (orderList.size > 0) {
                                fragmentOrdersBinding.rvOrders.visibility = View.VISIBLE
                                //fragmentOrdersBinding.edtSearch.visibility = View.VISIBLE
                                fragmentOrdersBinding.txtNoRecord.visibility = View.GONE
                                fragmentOrdersBinding.llOrderList.visibility = View.VISIBLE
                                if (isActive.equals("false")) {
                                    fragmentOrdersBinding.txtActiveOrders.setText("Total Orders: " + orderList.size)
                                } else {
                                    fragmentOrdersBinding.txtActiveOrders.setText("Active Orders: " + orderList.size)
                                }

                                initOrdersAdapter()
                            } else {
                                fragmentOrdersBinding.txtNoRecord.visibility = View.VISIBLE
                                fragmentOrdersBinding.edtSearch.visibility = View.GONE
                                fragmentOrdersBinding.rvOrders.visibility = View.GONE
                                fragmentOrdersBinding.txtActiveOrders.setText("")
                                /* if (isActive.equals("true")) {
                                     fragmentOrdersBinding.llOrderList.visibility = View.GONE
                                     fragmentOrdersBinding.llWelcomeScreen.visibility = View.VISIBLE
                                 }
 */
                            }
                            /*  vehicleList.addAll(response.data?.vehicleData!!)
                              weightList.addAll(response.data?.weightData!!)
                              bannersList.addAll(response.data?.bannersData!!)
                              deliveryTypeList.addAll(response.data?.deliveryOptionData!!)
                              initDiscountsAdapter()
                              initWeightAdapter()
                              initVehiclesAdapter()
                              initDeliveryTypeAdapter()*/
                        }
                        else -> message?.let {
                            fragmentOrdersBinding.txtNoRecord.visibility = View.VISIBLE
                            fragmentOrdersBinding.edtSearch.visibility = View.GONE
                            fragmentOrdersBinding.rvOrders.visibility = View.GONE
                            fragmentOrdersBinding.txtActiveOrders.setText("")
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })


        orderViewModel.cancelReasonRes().observe(this,
            Observer<CancelReasonsListResponse> { response->
                //stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            for (count in 0 until response.data!!.size) {
                                reasons.add(response.data!![count].reason!!)

                            }
                            // reasons.add("Other Reason")
                            // activityCreateOrderBinding.orderDetailModel = response.data
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

        orderViewModel.cancelOrderRes().observe(this,
            Observer<CommonModel> { response->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            showToastSuccess(
                                "Order Cancelled Successfully"
                            )

                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                orderViewModel.getOrderList("active")
                            }
                            //finish()
                            // activityCreateOrderBinding.orderDetailModel = response.data
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

        fragmentOrdersBinding.toolbar.setImageResource(R.drawable.ic_side_menu)

        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "toolbar" -> {
                        (activity as LandingActivty).openCloseDrawer()
                    }
                    "txtActive" -> {
                        if (isActive.equals("false")) {
                            unselectButtons()
                            isActive = "true"
                            orderType = "active"
                            fragmentOrdersBinding.txtActive.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_active_selected
                                )
                            )
                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                orderViewModel.getOrderList("active")
                            }
                        }

                    }
                    "txtCompleted" -> {
                        if (isActive.equals("true")) {
                            unselectButtons()
                            isActive = "false"
                            orderType = "complete"
                            fragmentOrdersBinding.txtCompleted.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_completed_selected
                                )
                            )
                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                orderViewModel.getOrderList("complete")
                            }
                        }

                    }
                }
            })
        )

    }

    private fun unselectButtons() {
        fragmentOrdersBinding.txtCompleted.setBackground(activity!!.resources.getDrawable(R.drawable.ic_completed_unselected))
        fragmentOrdersBinding.txtActive.setBackground(activity!!.resources.getDrawable(R.drawable.ic_active_unselected))
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(activity!!) { task->
                    var location : Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLat = location.latitude.toString()
                        currentLong = location.longitude.toString()
                        /*  Handler().postDelayed({
                              callSocketMethods("updateVehicleLocation")
                          }, 2000)
  */
                    }
                }
            } else {
                Toast.makeText(activity, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled() : Boolean {
        var locationManager : LocationManager =
            activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission", "RestrictedApi")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult : LocationResult) {
            var mLastLocation : Location = locationResult.lastLocation
            currentLat = mLastLocation.latitude.toString()
            currentLong = mLastLocation.longitude.toString()
            /*Handler().postDelayed({
                callSocketMethods("updateVehicleLocation")
            }, 2000)*/

        }
    }

    private fun initOrdersAdapter() {
        val ordersAdapter =
            OrdersListAdapter(
                this@OrdersFragment,
                orderList,
                activity!!,
                isActive
            )
        val linearLayoutManager = LinearLayoutManager(activity!!)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentOrdersBinding.rvOrders.layoutManager = linearLayoutManager
        fragmentOrdersBinding.rvOrders.setHasFixedSize(true)
        fragmentOrdersBinding.rvOrders.adapter = ordersAdapter
        fragmentOrdersBinding.rvOrders.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun showCancelReasonDialog(orderId : String, cancelledCharges : String?) {
        val confirmationDialog = Dialog(activity, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)


        confirmationDialog?.setContentView(R.layout.cancel_dialog)
        confirmationDialog?.setCancelable(true)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val edtReason = confirmationDialog?.findViewById<EditText>(R.id.edtReason)
        val llCancelCharges = confirmationDialog?.findViewById<LinearLayout>(R.id.llCancelCharges)
        val btnSend = confirmationDialog?.findViewById<Button>(R.id.btnSend)
        val txtCharges = confirmationDialog?.findViewById<TextView>(R.id.txtCharges)
        val spReason = confirmationDialog?.findViewById<Spinner>(R.id.spReason)
        val adapter = ArrayAdapter(
            activity,
            R.layout.spinner_item, reasons
        )

        adapter.setDropDownViewResource(R.layout.spinner_item);

        if (!cancelledCharges.equals("0") || !TextUtils.isEmpty(cancelledCharges)) {
            llCancelCharges.visibility = View.VISIBLE
            txtCharges.text = cancelledCharges
        } else {
            llCancelCharges.visibility = View.GONE
        }
        spReason?.adapter = adapter
        var pos = 0
        var otherReason = "false"
        spReason?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent : AdapterView<*>,
                view : View, position : Int, id : Long
            ) {
                //if (position != 0) {
                pos = position
                edtReason?.setText("")
                otherReason = "false"
                if (reasons[pos].equals("Other")) {
                    otherReason = "true"
                    edtReason?.visibility = View.VISIBLE
                } else {
                    otherReason = "false"
                    edtReason?.visibility = View.GONE
                }
                /* } else {
                     //regionId = "0"
                     //regionPos = position
                 }
 */
            }

            override fun onNothingSelected(parent : AdapterView<*>) {
                // write code to perform some action
            }
        }


        btnSend?.setOnClickListener {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "orderId", orderId
            )
            if (otherReason.equals("true")) {
                mJsonObject.addProperty(
                    "cancellationReason",
                    edtReason?.text.toString().trim() //completedorder?.empId
                )
                mJsonObject.addProperty(
                    "otherReason", edtReason?.text.toString().trim()
                )

            } else {
                mJsonObject.addProperty(
                    "cancellationReason", reasons[pos]//completedorder?.empId
                )
                mJsonObject.addProperty(
                    "otherReason", reasons[pos]
                )
            }

            if (pos != 0) {
                if (otherReason.equals("true")) {
                    if (TextUtils.isEmpty(edtReason?.text.toString().trim())) {
                        edtReason?.error = "Please enter reason"
                    } else {
                        if (UtilsFunctions.isNetworkConnected()) {
                            baseActivity.startProgressDialog()
                            orderViewModel.cancelOrder(mJsonObject)
                            confirmationDialog?.dismiss()
                        }
                    }
                } else {
                    if (UtilsFunctions.isNetworkConnected()) {
                        baseActivity.startProgressDialog()
                        orderViewModel.cancelOrder(mJsonObject)
                        confirmationDialog?.dismiss()
                    }
                }

            } else {
                showToastError("Please select reason")
            }
        }
        if (!confirmationDialog?.isShowing()!!) {
            confirmationDialog?.show()
        }

    }

}