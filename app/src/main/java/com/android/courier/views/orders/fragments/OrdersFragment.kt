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
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.orders.OrdersListAdapter
import com.android.courier.chatSocket.ConnectionListener
import com.android.courier.chatSocket.SocketConnectionManager
import com.android.courier.common.UtilsFunctions
import com.android.courier.common.UtilsFunctions.showToastError
import com.android.courier.common.UtilsFunctions.showToastSuccess
import com.android.courier.constants.GlobalConstants
import com.android.courier.databinding.FragmentOrdersBinding
import com.android.courier.maps.FusedLocationClass
import com.android.courier.model.CommonModel
import com.android.courier.model.order.CancelReasonsListResponse
import com.android.courier.model.order.OrdersListResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.home.LandingActivty
import com.bumptech.glide.Glide
import com.example.services.socket.SocketClass
import com.example.services.socket.SocketInterface
import com.google.android.gms.location.*
import com.google.gson.JsonObject
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class OrdersFragment : BaseFragment(), ConnectionListener, SocketInterface {
    private var mFusedLocationClass : FusedLocationClass? = null
    private lateinit var orderViewModel : OrderViewModel
    val PERMISSION_ID = 42
    var orderList = ArrayList<OrdersListResponse.Data>()
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var isActive = "true"
    var orderType = "active"
    private var socket = SocketClass.socket
    var reasons = java.util.ArrayList<String>()
    private lateinit var fragmentOrdersBinding : FragmentOrdersBinding
    var isFirstTime = false
    private var orderListClicked = 0 // 0 - Active , 1 - History

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
        isFirstTime = false
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

        fragmentOrdersBinding.imgToolbarText.text =
            "Orders"/*Welcome, " + firstName + " " + lastName*/
        socket.updateSocketInterface(this)
        socket.onConnect()

        Handler().postDelayed({
            callSocketMethods("getLocation")
        }, 2000)

        callSocketMethods("updateOrderStatus")

        try {
            val socketConnectionManager : SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e : URISyntaxException) {
            e.printStackTrace()
        }
        SocketConnectionManager.getInstance()
            .addEventListener("updateOrderStatus") { args->
                //val data = args[0] as JSONObject
                try {
                    Log.d("updateOrderStatus", "updateOrderStatus")
                    // val orderStatus = data.getString("orderStatus")
                    if (UtilsFunctions.isNetworkConnected()) {
                        //baseActivity.startProgressDialog()
                        orderViewModel.getOrderList(orderType)
                    }
                } catch (e : Exception) {
                }
            }



        reasons.add("Select Reason")
        if (UtilsFunctions.isNetworkConnected()) {
            // baseActivity.startProgressDialog()
            // orderViewModel.getOrderList(orderType)
            // orderViewModel.cancelReason("reason")
        }


        fragmentOrdersBinding.itemsswipetorefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(activity!!, R.color.colorPrimary)
        )
        fragmentOrdersBinding.itemsswipetorefresh.setColorSchemeColors(Color.WHITE)

        fragmentOrdersBinding.itemsswipetorefresh.setOnRefreshListener {
            if (UtilsFunctions.isNetworkConnected()) {
                //baseActivity.startProgressDialog()
                orderViewModel.getOrderList(orderType)
            }
            fragmentOrdersBinding.itemsswipetorefresh.isRefreshing = false
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
                                fragmentOrdersBinding.noRecordAnimation.visibility = View.GONE
                                fragmentOrdersBinding.llOrderList.visibility = View.VISIBLE
                                /*if (isActive.equals("false")) {
                                    fragmentOrdersBinding.txtActiveOrders.setText("Total Orders: " + orderList.size)
                                } else {
                                    fragmentOrdersBinding.txtActiveOrders.setText("Active Orders: " + orderList.size)
                                }*/

                                fragmentOrdersBinding.txtActiveOrders.visibility = View.GONE

                                initOrdersAdapter()
                            } else {
                                fragmentOrdersBinding.noRecordAnimation.visibility = View.VISIBLE
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
                        else -> /*message?.let*/ {
                            fragmentOrdersBinding.noRecordAnimation.visibility = View.VISIBLE
                            fragmentOrdersBinding.txtNoRecord.visibility = View.VISIBLE
                            fragmentOrdersBinding.edtSearch.visibility = View.GONE
                            fragmentOrdersBinding.rvOrders.visibility = View.GONE
                            fragmentOrdersBinding.txtActiveOrders.setText("")
                            //UtilsFunctions.showToastError(it)
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
                            isFirstTime = false
                            unselectButtons()
                            isActive = "true"
                            orderType = "active"
                            fragmentOrdersBinding.txtActive.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_active_selected
                                )
                            )
                            orderListClicked = 0
                            if (UtilsFunctions.isNetworkConnected()) {
                                baseActivity.startProgressDialog()
                                orderViewModel.getOrderList("active")
                            }
                        }

                    }
                    "txtCompleted" -> {
                        if (isActive.equals("true")) {
                            isFirstTime = false
                            unselectButtons()
                            isActive = "false"
                            orderType = "complete"
                            fragmentOrdersBinding.txtCompleted.setBackground(
                                activity!!.resources.getDrawable(
                                    R.drawable.ic_completed_selected
                                )
                            )
                            orderListClicked = 1
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
        if (isFirstTime) {
            isFirstTime = true
            val controller =
                AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_animation_from_left)
            fragmentOrdersBinding.rvOrders.setLayoutAnimation(controller);
            fragmentOrdersBinding.rvOrders.scheduleLayoutAnimation();
        }
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

    override fun onConnectError() {
        Log.e("Socket", "OnConnectedError")

    }

    override fun onConnected() {
        Log.e("Socket", "OnConnected")

    }

    override fun onDisconnected() {
        activity!!.runOnUiThread {
            Toast.makeText(activity!!, "disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun callSocketMethods(methodName : String) {
        val object5 = JSONObject()
        when (methodName) {
/*
            "getLocation" -> try {
                socket.sendDataToServer(methodName, object5)
            } catch (e : Exception) {
                e.printStackTrace()
            }
*/
            "updateOrderStatus" -> {
                try {
                    socket.sendDataToServer(methodName, object5)
                } catch (e : Exception) {
                    e.printStackTrace()
                }
            }
/*
            "trackYourRider"->
            {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("orderId", jobId)
                    jsonObject.put("empId", driverId)
                    jsonObject.put("onGoing", "false")
                    SocketConnectionManager.getInstance()
                        .socket.emit("trackYourRider", jsonObject)
                } catch (e : Exception) {
                    e.printStackTrace()
                }

                SocketConnectionManager.getInstance()
                    .addEventListener("trackYourRider") { args ->
                        val data = args[0] as JSONObject
                        try {
                        } catch (e: JSONException) {
                        }
                    }

            }
*/
        }
    }

    override fun onSocketCall(onMethadCall : String, vararg jsonObject : Any) {
        /*val serverResponse = jsonObject[0] as JSONObject
        var methodName = serverResponse.get("method")
        try {
            baseActivity.runOnUiThread {
                when (methodName) {
                    "updateOrderStatus" -> try {
                      when(orderListClicked)
                      {
                          0->
                          {
                              if (UtilsFunctions.isNetworkConnected()) {
                                  baseActivity.startProgressDialog()
                                  orderViewModel.getOrderList("active")
                              }
                          }
                          1->
                          {
                              if (UtilsFunctions.isNetworkConnected()) {
                                  baseActivity.startProgressDialog()
                                  orderViewModel.getOrderList("complete")
                              }
                          }
                      }
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }*/
    }

    override fun onSocketConnect(vararg args : Any) {
        Log.e("Socket", "Socket onSocketConnect")
    }

    override fun onSocketDisconnect(vararg args : Any) {
        Log.e("Socket", "Socket onSocketDisconnect")
    }

}