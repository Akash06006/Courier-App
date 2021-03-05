package com.courierdriver.views.orders

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.orders.DeliveryAddressAdapter
import com.courierdriver.adapters.orders.DetailDeliveryAddressAdapter
import com.courierdriver.adapters.orders.NavigationAddressAdapter
import com.courierdriver.application.MyApplication
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.chatSocket.ConnectionListener
import com.courierdriver.chatSocket.SocketConnectionManager
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityOrderDetailsBinding
import com.courierdriver.model.CancelReasonModel
import com.courierdriver.model.CommonModel
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.*
import com.courierdriver.utils.offlineSyncing.NetworkChangeReceiver
import com.courierdriver.utils.service.NetworkChangeCallback
import com.courierdriver.utils.service.NotiFyRestartTrackingReceiver
import com.courierdriver.utils.service.RestartTrackingBroadcastReceiver
import com.courierdriver.utils.service.TrackingService
import com.courierdriver.viewmodels.order.OrderDetailViewModel
import com.courierdriver.views.chat.ChatActivity
import com.courierdriver.views.chat.CustomerChatActivity
import com.example.fleet.socket.SocketInterface
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.layout_custom_alert.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URISyntaxException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class OrderDetailsActivity : BaseActivity(), OnMapReadyCallback, LocationListener,
    ConnectionListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnCameraIdleListener, DialogssInterface, SelfieCallBack,
    NetworkChangeCallback, NotiFyRestartTrackingReceiver {
    private var marker: Marker? = null
    private var deliveryList: ArrayList<OrdersDetailResponse.PickupAddress>? = ArrayList()
    private var orderDetail: OrdersDetailResponse.Data? = OrdersDetailResponse.Data()
    private lateinit var activityCreateOrderBinding: ActivityOrderDetailsBinding
    private lateinit var orderViewModel: OrderDetailViewModel
    private var selfieDialog: Dialog? = null
    private var cancellationReason: String? = null
    var fileUri = ""
    private var mGoogleMap: GoogleMap? = null
    private var mPermissionCheck = false
    private var dialog: Dialog? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var clickSettings = 1
    var imageFile: File? = null
    private var scan = 0
    private var cancelOrderAlertDialog: Dialog? = null
    private var submitCancelReasonDialog: Dialog? = null
    private var cancelStringReasonList = ArrayList<String?>()
    private var cancelReasonList: ArrayList<CancelReasonModel.Body>? = ArrayList()
    private var mDialogClass = DialogClass()
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var mContext: Context? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var selfieImage = ""
    var orderId = ""
    var userId = ""
    private var photoFile: File? = null
    private val CAMERA_REQUEST = 1808
    private lateinit var mLastLocation: Location
    private lateinit var mLocationCallback: LocationCallback
    private var mCurrLocationMarker: Marker? = null
    private lateinit var mLocationRequest: LocationRequest
    private var reasons = java.util.ArrayList<String>()
    private var cancelledCharges = "0"
    private var selfieAction = 0 // 1 - Pickup Selfie , 2 - Drop Selfie
    private var selectDeliveryAddressDailog: Dialog? = null
    private var setNavigationIconDialog: Dialog? = null
    private var dialogDelAddressList: ArrayList<OrdersDetailResponse.PickupAddress>? = null
    private var addressId = "0"
    private var orderStatus = ""
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var mLocationPermissionGranted: Boolean = false
    private var currentLatitude: Double = 0.toDouble()
    private var currentLongitude: Double = 0.toDouble()
    private var builder: LatLngBounds.Builder = LatLngBounds.Builder()
    var mSocket: Socket? = null
    private var mNetworkReceiver: BroadcastReceiver? = null
    private var trackingService: TrackingService? = null
    private var TRACK_RIDE = 0
    private var isConnected = false
    private var otherReasonSelected = false
    private var isTimerStarted = false
    var isFirst = true
    var customerId = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_order_details
    }

    override fun initViews() {
        setBindingAndViewModel()
        isFirst = true
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        dialog = Dialog(this)
        reasons.add("Select Reason")

        mContext = this
        setToolbar()
        initMap()
        getIntentData()
        orderViewModel.orderDetail(orderId)
        cancelReasonObserver()
        cancelOrderObserver()
        orderDetailObserver()
        acceptOrderObserver()
        pickupOrderObserver()
        completeOrderObserver()
        loaderObserver()
        viewClicks()
        networkChangeCheckReciever()
        subscribeTrackingBroadcastReceiver()
        uploadSelfieObserver()

        initiateSocket()
        //  startTimer()
        if (UtilsFunctions.isNetworkConnected()) {
            //baseActivity.startProgressDialog()
            orderViewModel.orderDetail(orderId)
        }


        try {
            val socketConnectionManager: SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, io.socket.emitter.Emitter.Listener>()
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        SocketConnectionManager.getInstance()
            .addEventListener("updateOrderStatus") { args ->
                //val data = args[0] as JSONObject

//                {"result":1,"message":"Success","method":"updateOrderStatus","data":{"orderNo":"ORDER#2102254","id":"87c1e776-61c3-41e2-8f3b-0a5a61e9c447","progressStatus":1,"orderStatus":"4"}}
                try {
                    val obj = args[0] as JSONObject
                    Log.d("updateOrderStatus", "updateOrderStatus")
                    val data = obj.getJSONObject("data")
                    val orderStatus = data.getString("orderStatus")
                    if (UtilsFunctions.isNetworkConnected()) {
                        //baseActivity.startProgressDialog()
                        if (orderStatus == "4") {
                            runOnUiThread {
                                showCancelledOrderAlert("Order is cancelled by customer")
                            }
                        } else {
                            orderViewModel.orderDetail(orderId)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        trackingService = TrackingService()
        val mServiceIntent = Intent(this, trackingService!!::class.java)
        if (!isMyServiceRunning(trackingService!!::class.java)) {
            startService(mServiceIntent)
        }
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i("timer going", "=========  ")
                orderViewModel.orderDetail(orderId)
            }
        }
        timer!!.schedule(timerTask, 10000, 10000) //
    }

    fun stoptimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    //region SOCKET
    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    override fun onDestroy() {
/*
        if (mSocket != null && mSocket!!.connected()) {
            mSocket!!.disconnect()
        }
*/
        //stopService(mServiceIntent);

        stoptimertask()

        val broadcastIntent = Intent()
        broadcastIntent.action = "restartService"
        broadcastIntent.setClass(this, RestartTrackingBroadcastReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    private fun subscribeTrackingBroadcastReceiver() {
        val restartTrackingBroadcastReceiver = RestartTrackingBroadcastReceiver(this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(restartTrackingBroadcastReceiver, IntentFilter("restartService"))
    }

    override fun refreshRestartTrackingData() {
        //   Toast.makeText(this, "Working", Toast.LENGTH_SHORT).show()
        setLocation()
    }

    private fun networkChangeCheckReciever() {
        mNetworkReceiver =
            NetworkChangeReceiver(this)
        registerNetworkBroadcastForNougat()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mNetworkReceiver, intentFilter)
    }

    private fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    @Throws(IOException::class)
    fun initiateSocket() {
        try {
            val app = (MyApplication)
            mSocket = app.instance.getSocketInstance()
            if (mSocket!!.connected()) {
                //mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
                /* mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
                 mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                 mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)*/
                mSocket!!.on("socketFromClient", object : Emitter.Listener {
                    override fun call(vararg args: Any) {
                        val obj = args[0] as JSONObject
                        Log.i("data", "" + obj)
                    }
                })
                //mSocket!!.on("trackDriver",trackDriver)
            }
        } catch (e: JSONException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private val onConnect = Emitter.Listener {
        runOnUiThread(Runnable {
            if (!isConnected) {
                try {
                    Log.i("isConnected", "" + isConnected)
                    initiateSocket()
                } catch (e: IOException) {
                    try {
                        if (e is SocketTimeoutException) {
                            throw SocketTimeoutException()
                        }
                    } catch (f: SocketTimeoutException) {
                        if (mSocket!!.connected()) {
                            mSocket!!.disconnect()
                        }
                        mSocket!!.connect()
                    }
                }
                isConnected = true
            }
        })
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread(Runnable {
            isConnected = false
        })
    }

    private val onConnectError = Emitter.Listener {
        runOnUiThread(Runnable {

        })
    }

    private fun emitLocation(mSocket: Socket, mLatitude: Double, mLongitude: Double) {
        /*  if (mSocket.connected()) {
              Toast.makeText(this, mLatitude.toString() + "," + mLongitude.toString(), Toast.LENGTH_SHORT).show()
          }
          else
          {
              Toast.makeText(this, "socket disconnected", Toast.LENGTH_SHORT).show()
          }*/
        Log.d("TAG", "emitingSocket")
        val mJsonObject = JSONObject()
        mJsonObject.put("driverId", userId)

        val jsonObjectLatLng = JSONObject()
        jsonObjectLatLng.put("lat", currentLatitude)
        jsonObjectLatLng.put("long", currentLongitude)
        val latLngArray = JSONArray()
        latLngArray.put(jsonObjectLatLng)

        mJsonObject.put("latLong", latLngArray)
        mJsonObject.put("methodName", "updateLocation")
        mJsonObject.put("orderId", orderId)
        mJsonObject.put("empId", userId)
        mSocket.emit("socketFromClient", mJsonObject)
        //  method name  "updateLocation", "updateVehicleLocation", "getLocation"

        //updateOrderStatus orderSttaus1-1 accept , 2 - cancel


        // updateOrderStatus from customer
    }
    //endregion


    private fun updateOrderStatus(orderStatus: String?) {
        Log.d("TAG", "emitingSocket")
        val mJsonObject = JSONObject()
        mJsonObject.put("methodName", "updateOrderStatus")
        mJsonObject.put("orderId", orderId)
        mJsonObject.put("driverId", userId)
        mJsonObject.put("orderStatus", orderStatus)
        mSocket!!.emit("socketFromClient", mJsonObject)
    }


    private fun initMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationRequest()
        setLocation()
    }

    private fun viewClicks() {
        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_call" -> {
                        if (orderDetail!!.pickupAddress!!.phoneNumber != null) {
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data =
                                Uri.parse("tel:" + orderDetail!!.pickupAddress!!.phoneNumber)
                            mContext!!.startActivity(dialIntent)
                        }
                    }
                    "rel_navigation" -> {
                        val intent = Intent(this, TrackMapActivity::class.java)
                        intent.putExtra("orderDetails", orderDetail)
                        startActivity(intent)
                        //settNavigationIconAddress()
                    }
                    "tv_available_accept_order" -> {
                        orderViewModel.acceptOrder(orderId)
                    }
                    "tv_available_cancel_order" -> {
                        showCancelOrderAlert()
                    }
                    "tv_accepted_cancel_order" -> {
                        showCancelOrderAlert()
                    }
                    "tv_accepted_take_order" -> {
                        selfieAction = 1
                        showTakeSelfieAlert("take_order")
                    }
                    "tv_complete_order" -> {
                        selfieAction = 2
                        if (dialogDelAddressList!!.size == 1) {
                            showTakeSelfieAlert("complete_order")
                        } else
                            selectDeliveryAddress()
                        /* selfieAction = 2
                         showTakeSelfieAlert("complete_order")*/
                    }
                    "tv_chat" -> {
                        val intent = Intent(this, CustomerChatActivity::class.java)
                        intent.putExtra("cust_id", customerId)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                    }
                    "tv_help" -> {
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                    }
                }
            })
        )
    }

    private fun completeOrderObserver() {
        isFirst = true
        orderViewModel.completeOrderData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (selectDeliveryAddressDailog != null)
                    selectDeliveryAddressDailog!!.dismiss()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {

                            if (deliveryList!!.size > 1) {
                                for (i in 0 until deliveryList!!.size) {
                                    if (!deliveryList!![i].isComplete!!) {
                                        initiateSocket()
                                    }
                                }
                            }
                            addressId = "0"
                            if (dialogDelAddressList!!.size == 1) {
                                //TODO------
                                val jsonObjectLatLng = JSONObject()
                                jsonObjectLatLng.put("methodName", "leaveSocket")
                                jsonObjectLatLng.put("orderId", orderId)
                                jsonObjectLatLng.put("empId", userId)
                                mSocket!!.emit("socketFromClient", jsonObjectLatLng)
                            }
                            orderViewModel.orderDetail(orderId)

                            if (mSocket!!.connected()) {
                                mSocket!!.disconnect()
                            }
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun pickupOrderObserver() {
        isFirst = true
        orderViewModel.pickupOrderData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            orderViewModel.orderDetail(orderId)
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun acceptOrderObserver() {
        isFirst = true
        orderViewModel.acceptOrderData().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {

                            val mJsonObject = JSONObject()
                            mJsonObject.put("driverId", userId)
                            mJsonObject.put("currentLat", currentLatitude)
                            mJsonObject.put("currentLong", currentLongitude)
                            //  mJsonObject.put("methodName", "updateRiderLocation")
                            mJsonObject.put("empId", userId)
                            mSocket!!.emit("updateRiderLocation", mJsonObject)

                            emitLocation(mSocket!!, currentLatitude, currentLongitude)
                            updateOrderStatus("1")
                            orderViewModel.orderDetail(orderId)
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun orderDetailObserver() {
        orderViewModel.orderDetailRes().observe(this,
            Observer<OrdersDetailResponse> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            activityCreateOrderBinding.orderDetailModel = response.data
                            orderDetail = response.data
                            orderId = response.data!!.id!!
                            orderStatus = response.data!!.orderStatus?.status!!
                            cancelledCharges = response.data?.cancellationCharges!!
                            setDeliveryAddressAdapter(response.data?.deliveryAddress)
                            hideUnhideButtons(response.data!!)


                            if (orderDetail!!.additionalCharges!!.cancellationCharges != null &&
                                orderDetail!!.additionalCharges!!.cancellationCharges != "0"
                            )
                                activityCreateOrderBinding.linAdditionalCharges.visibility =
                                    View.VISIBLE
                            else
                                activityCreateOrderBinding.linAdditionalCharges.visibility =
                                    View.GONE

                            if (orderDetail!!.additionalCharges!!.securityFee != null &&
                                orderDetail!!.additionalCharges!!.securityFee != "0"
                            )
                                activityCreateOrderBinding.linSecuirtyCharges.visibility =
                                    View.VISIBLE
                            else
                                activityCreateOrderBinding.linSecuirtyCharges.visibility = View.GONE

                            if (orderStatus == "1")
                                activityCreateOrderBinding.linValuesEarningsAvailable.visibility =
                                    View.GONE
                            else
                                activityCreateOrderBinding.linValuesEarningsAvailable.visibility =
                                    View.VISIBLE

                            if (response.data!!.deliveryoption!!.title == "Regular") {
                                activityCreateOrderBinding.tvDeliveryOption.setTextColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.colorPrimary
                                    )
                                )
                            } else if (response.data!!.deliveryoption!!.title == "Express") {
                                activityCreateOrderBinding.tvDeliveryOption.setTextColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.colorRed
                                    )
                                )
                            }


                            /* var currentLatLng = LatLng(currentLatitude, currentLongitude)
                             for (i in 0 until response.data?.deliveryAddress!!.size) {
                                 var destLatLng = LatLng(
                                     response!!.data!!.deliveryAddress!![i].lat!!.toDouble(),
                                     response!!.data!!.deliveryAddress!![i].long!!.toDouble()
                                 )
                                 drawPolyline(currentLatLng, destLatLng, false)
                             }*/

                            setPickupDestinationMarker(response)
                            /*  if (isFirst) {
                                  isFirst = false*/
                            deliveryList = response.data!!.deliveryAddress!!
                            customerId = response.data!!.userId!!
                            dialogDelAddressList = ArrayList()
                            if (dialogDelAddressList!!.size > 0)
                                dialogDelAddressList!!.clear()
                            var count = 0
                            Log.d("TAG", "deliveryList=---- " + deliveryList!!.count())
                            for (i in 0 until deliveryList!!.count()) {
                                if (deliveryList!![i].isComplete == false) {
                                    count += 1
                                    Log.d("TAG", "count=---- $count")
                                    Log.d("TAG", "address=---- " + deliveryList!![i].address)

                                    dialogDelAddressList!!.add(deliveryList!![i])
                                }
                            }
//                            }

                        }
                        203 -> {
                            showOrderTakenAlert()
                        }
                        else ->
                            message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })
    }

    fun showOrderTakenAlert() {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.layout_custom_alert,
                null,
                false
            )
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(getString(R.string.app_name))
        // set the custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.text) as TextView
        text.text = "Order has been taken by other rider."
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        val dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel) as Button
        dialogButtonCancel.visibility = View.GONE
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {

            dialog.dismiss()
            finish()
        }
        dialog.show()

    }

    private fun setPickupDestinationMarker(response: OrdersDetailResponse) {
        if (mGoogleMap != null)
            mGoogleMap!!.clear()

        val icPickup = bitmapDescriptorFromVector(
            this,
            R.drawable.ic_pickup
        )
        val icPickupPending = bitmapDescriptorFromVector(
            this,
            R.drawable.ic_pickup_pending
        )
        // if(response.pick)
        if (response.data!!.pickedUp == false) {
            marker = mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            response.data?.pickupAddress!!.lat!!.toDouble(),
                            response.data?.pickupAddress!!.long!!.toDouble()
                        )
                    )
                    .icon(icPickupPending)
            )
        } else {
            marker = mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            response.data?.pickupAddress!!.lat!!.toDouble(),
                            response.data?.pickupAddress!!.long!!.toDouble()
                        )
                    )
                    .icon(icPickup)
            )
        }

        builder.include(marker!!.position)

        val icDestinationDelivered = bitmapDescriptorFromVector(
            this,
            R.drawable.drop_completed
        )
        val icDestination = bitmapDescriptorFromVector(
            this,
            R.drawable.drop_pendig
        )

        for (i in 0 until response.data?.deliveryAddress!!.size) {
            if (response.data?.deliveryAddress!![i].isComplete == false) {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.data?.deliveryAddress!![i].lat!!.toDouble(),
                                response.data?.deliveryAddress!![i].long!!.toDouble()
                            )
                        )
                        .icon(icDestination)
                )
            } else {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.data?.deliveryAddress!![i].lat!!.toDouble(),
                                response.data?.deliveryAddress!![i].long!!.toDouble()
                            )
                        )
                        .icon(icDestinationDelivered)
                )
            }

            builder.include(marker!!.position)
        }
        val bounds = builder.build()
        val padding = 150 // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mGoogleMap!!.moveCamera(cu)
    }

    //region CURRENT_LOCATION
    private fun getLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 10000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun setLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 10 * 1000.toLong() // 10 seconds
        locationRequest!!.fastestInterval = 5 * 1000.toLong() // 5 seconds
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            }

            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null && currentLatitude != location.latitude) {
                        currentLatitude = location.latitude
                        currentLongitude = location.longitude
                        if (orderId != "0")
                            emitLocation(mSocket!!, currentLatitude, currentLongitude)
                    }
                }
                //show location on map
                showCurrentLocationOnMap()
                //Location fetched, update listener can be removed
                //fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
            }
        }
        startLocationUpdates()
    }

    private fun showCurrentLocationOnMap() {
        if (checkAndRequestPermissions()) {
            @SuppressLint("MissingPermission")
            val lastLocation = fusedLocationProviderClient!!.lastLocation
            lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    //  mGoogleMap!!.clear()

                    //Go to Current Location
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude

                    /* val icDestination = bitmapDescriptorFromVector(
                         this,
                         R.drawable.ic_current_location
                     )

                     val marker = mGoogleMap!!.addMarker(
                         MarkerOptions()
                             .position(LatLng(currentLatitude, currentLongitude))
                             .icon(icDestination)
                     )

                     builder.include(marker.position)
                     val bounds = builder.build()
                     val padding = 20 // offset from edges of the map in pixels
                     val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                     mGoogleMap!!.moveCamera(cu)*/

//                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLatitude, currentLongitude), 15f))
                } else {
                    //Gps not enabled if loc is null
                    getSettingsLocation()
                }
            }
            lastLocation.addOnFailureListener {
                //If perm provided then gps not enabled
                //                getSettingsLocation();
            }
        }
    }

    override fun onNetworkChanged(status: Boolean) {
        if (status) {
            initiateSocket()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(TRACK_RIDE, intent)
        finish()
    }

    private fun getSettingsLocation() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                //...
                if (response != null) {
                    val locationSettingsStates = response.locationSettingsStates
                    Log.d("TAG", "getSettingsLocation: $locationSettingsStates")
                    this.startLocationUpdates()

                }
            } catch (exception: ApiException) {
                Log.d("TAG", "getSettingsLocation: $exception")
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // If unavailable
                    }
                }// Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.
                //...
            }
        }
    }

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 265
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback!!, null/* Looper */
        )
            .addOnSuccessListener { Log.d("TAG", "startLocationUpdates: onSuccess: ") }
            .addOnFailureListener { e ->
                Log.d("TAG", "startLocationUpdates: " + e.message)
            }
    }


    //endregion

    private fun cancelOrderObserver() {
        isFirst = true
        orderViewModel.cancelOrderRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            showToastSuccess(
                                "Order Cancelled Successfully"
                            )
                            updateOrderStatus("2")
                            finish()
                            // activityCreateOrderBinding.orderDetailModel = response.data
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })
    }

    private fun setBindingAndViewModel() {
        activityCreateOrderBinding = viewDataBinding as ActivityOrderDetailsBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        activityCreateOrderBinding.orderDetailViewModel = orderViewModel
        activityCreateOrderBinding.currencySign = GlobalConstants.CURRENCY_SIGN
    }

    private fun getCurrentTime(): String? {
        val cal = Calendar.getInstance()
        val currentLocalTime = cal.time
        val date: DateFormat = SimpleDateFormat("hh:mm a")
        date.timeZone = TimeZone.getTimeZone("GMT+1:00")

        return date.format(currentLocalTime)
    }

    private fun cancelReasonObserver() {

        orderViewModel.cancelReason()
        orderViewModel.cancelReasonData().observe(this,
            Observer<CancelReasonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                cancelReasonList = response.body
                            }
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun showCancelOrderAlert() {
        cancelOrderAlertDialog = mDialogClass.setDefaultDialog(
            this,
            this,
            "cancelOrderAlert",
            getString(R.string.no),
            getString(R.string.yes),
            getString(R.string.do_you_want_to_cancel_order)
        )
        cancelOrderAlertDialog!!.show()
    }

    private fun showTakeSelfieAlert(orderStatus: String) {
        if (checkAndRequestPermissions()) {
            selfieDialog = mDialogClass.setUploadProductConfirmationDialog(
                this,
                this, orderStatus
            )
        }
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        cancelOrderAlertDialog!!.dismiss()
        showCancelReasonDialog()
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        cancelOrderAlertDialog!!.dismiss()
    }

    /*private fun hideUnhideButtons(data: OrdersDetailResponse.Data) {
        val orderStatus = data.orderStatus?.status // 1 available, 2 active, 3 completed
        val orderStatusName = data.orderStatus?.statusName
        if (orderStatus.equals("1")) {
            activityCreateOrderBinding.linChatHelp.visibility = View.GONE
            activityCreateOrderBinding.llAvailable.visibility = View.VISIBLE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        } else if (orderStatus.equals("2")) {
            if (orderStatusName.equals("Picked Up")) {
                activityCreateOrderBinding.linChatHelp.visibility = View.VISIBLE
                activityCreateOrderBinding.tvTimer.visibility = View.GONE
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.VISIBLE
            } else {
                activityCreateOrderBinding.linChatHelp.visibility = View.VISIBLE
                activityCreateOrderBinding.tvTimer.visibility = View.VISIBLE
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.VISIBLE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
            }

        } else if (orderStatus.equals("3")) {
            activityCreateOrderBinding.linChatHelp.visibility = View.GONE
            activityCreateOrderBinding.tvTimer.visibility = View.GONE
            activityCreateOrderBinding.llAvailable.visibility = View.GONE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        }

    }*/

    private fun hideUnhideButtons(data: OrdersDetailResponse.Data) {
        val orderStatus = data.orderStatus?.status // 1 available, 2 active, 3 completed
        val orderStatusName = data.orderStatus?.statusName
        if (orderStatus.equals("1")) {
            activityCreateOrderBinding.imgCall.visibility = View.GONE
            activityCreateOrderBinding.linChatHelp.visibility = View.GONE
            activityCreateOrderBinding.relNavigation.visibility = View.VISIBLE
            activityCreateOrderBinding.llAvailable.visibility = View.VISIBLE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        } else if (orderStatus.equals("2")) {
            activityCreateOrderBinding.imgCall.visibility = View.VISIBLE
            if (orderStatusName.equals("Picked Up")) {
                activityCreateOrderBinding.relNavigation.visibility = View.VISIBLE
                activityCreateOrderBinding.linChatHelp.visibility = View.VISIBLE
                activityCreateOrderBinding.tvTimer.visibility = View.GONE
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.VISIBLE
            } else {
                if (!isTimerStarted)
                    getTimeDifference()
                activityCreateOrderBinding.relNavigation.visibility = View.VISIBLE
                activityCreateOrderBinding.linChatHelp.visibility = View.VISIBLE
                activityCreateOrderBinding.tvTimer.visibility = View.VISIBLE
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.VISIBLE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
            }

        } else if (orderStatus.equals("3")) {
            activityCreateOrderBinding.imgCall.visibility = View.GONE
            if (orderStatusName == "Cancelled-User")
                showCancelledOrderAlert("Order is cancelled by customer")

            activityCreateOrderBinding.relNavigation.visibility = View.GONE
            activityCreateOrderBinding.linChatHelp.visibility = View.GONE
            activityCreateOrderBinding.tvTimer.visibility = View.GONE
            activityCreateOrderBinding.llAvailable.visibility = View.GONE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        } else if (orderStatus.equals("4")) {
            activityCreateOrderBinding.imgCall.visibility = View.GONE
            activityCreateOrderBinding.relNavigation.visibility = View.GONE
            if (orderStatusName.equals("Cancelled-Driver")) {
                activityCreateOrderBinding.linChatHelp.visibility = View.GONE
                activityCreateOrderBinding.tvTimer.visibility = View.GONE
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
            }
        }
    }

    private fun showCancelledOrderAlert(message1: String) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(getApplicationContext()),
                R.layout.layout_custom_alert,
                null,
                false
            )
        val dialog = Dialog(this@OrderDetailsActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setTitle(getString(R.string.app_name))
        // set the custom dialog components - text, image and button
        val text = dialog.findViewById(R.id.text) as TextView
        text.text = message1
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        val dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel) as Button
        dialogButtonCancel.visibility = View.GONE
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()


        //Inflate the dialog with custom view
        /*val mDialogView =
            LayoutInflater.from(baseContext).inflate(R.layout.layout_custom_alert, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(baseContext)
            .setView(mDialogView)
            .setTitle(getString(R.string.app_name))
        //show dialog
        val mAlertDialog = mBuilder.show()
        //text.setText(message1)
        // dialogButtonCancel.visibility = View.GONE
        //button click of custom layout
        dialogButtonOK.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
            finish()
            //get text from EditTexts of custom layout
        }*/
    }

    private fun setDeliveryAddressAdapter(deliveryAddressList: ArrayList<OrdersDetailResponse.PickupAddress>?) {
        val linearLayoutManager = LinearLayoutManager(this)
        val deliveryAddressAdapter =
            DetailDeliveryAddressAdapter(this, deliveryAddressList!!, orderDetail)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        activityCreateOrderBinding.rvDeliveryAddress.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvDeliveryAddress.adapter = deliveryAddressAdapter
    }

    //region map
    override fun onMapReady(googleMap: GoogleMap) {
        this.mGoogleMap = googleMap
        this.mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                //mGoogleMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            // mGoogleMap!!.isMyLocationEnabled = true
        }
*/
        //mGoogleMap?.uiSettings?.setAllGesturesEnabled(true)
        // mGoogleMap?.uiSettings?.isScrollGesturesEnabled = true
        mGoogleMap!!.uiSettings.isZoomControlsEnabled = true
        //    mGoogleMap?.setOnCameraIdleListener(this)
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient
        )
        if (mLastLocation != null) {
            val latLng = LatLng(mLastLocation.latitude, mLastLocation.longitude)
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        }
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 50000 //5 seconds
        mLocationRequest.fastestInterval = 50000 //5 seconds
        mLocationRequest.smallestDisplacement = 0.1f //added
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest,
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        if (mGoogleApiClient != null) {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(applicationContext, "connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(p0: Int) {
        Toast.makeText(applicationContext, "connection suspended", Toast.LENGTH_SHORT).show()
    }

    //endregion

    private fun checkPermission() {
        if (!mPermissionCheck) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext != null)
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = true
                    } else {
                        if (dialog != null) {
                            dialog!!.dismiss()
                        }
                        mPermissionCheck = false

                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ), MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
            } else {
                if (dialog != null) {
                    dialog!!.dismiss()
                }
                mPermissionCheck = true
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && permissions.size > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog!!.dismiss()
                    mPermissionCheck = true
                } else {
                    if (scan > 0) {
                        scan = 0
                    } else {
                        val permission1 = permissions[0]
                        val showRationale = shouldShowRequestPermissionRationale(permission1)
                        if (clickSettings > 0) {
                            clickSettings = 0
                            dialog!!.show()
                        } else {
                            if (!showRationale && permanent_deny > 0) {
                                clickSettings++
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", this.packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            } else if (permanent_deny == 0 && !showRationale) {
                                dialog!!.show()
                                permanent_deny++
                            } else {
                                permanent_deny++
                                dialog!!.show()
                            }
                        }
                    }
                }
            }
        }
    }

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
        val background =
            ContextCompat.getDrawable(this, vectorDrawableResourceId)
        background?.setBounds(
            0,
            0,
            background.intrinsicWidth,
            background.intrinsicHeight
        )
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showCancelReasonDialog() {
        submitCancelReasonDialog = Dialog(this)
        submitCancelReasonDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_cancel_order_reason,
                null,
                false
            )
        submitCancelReasonDialog!!.setContentView(dialogBinding.root)
        submitCancelReasonDialog!!.setCancelable(false)

        val spinnerReason =
            submitCancelReasonDialog!!.findViewById<Spinner>(R.id.sp_cancellation_reason)
        val tvSubmit = submitCancelReasonDialog!!.findViewById<TextView>(R.id.tv_submit)
        val tvPrice = submitCancelReasonDialog!!.findViewById<TextView>(R.id.tv_price)
        val imgCross = submitCancelReasonDialog!!.findViewById<ImageView>(R.id.img_cross)
        val relOtherReason =
            submitCancelReasonDialog!!.findViewById<RelativeLayout>(R.id.rel_other_reason)
        val etOtherReason =
            submitCancelReasonDialog!!.findViewById<EditText>(R.id.et_other_reason)

        tvPrice.text = GlobalConstants.CURRENCY_SIGN + orderDetail!!.driverCCharges

        setCancelReasonSpinner(spinnerReason, relOtherReason)

        tvSubmit.setOnClickListener {
            if (TextUtils.isEmpty(cancellationReason))
                UtilsFunctions.showToastError(getString(R.string.please_select_reason))
            else if (otherReasonSelected && TextUtils.isEmpty(etOtherReason.text.toString().trim()))
                showToastError("Please enter reason")
            else
                cancelOrderApi(etOtherReason.text.toString().trim())
        }
        imgCross.setOnClickListener {
            submitCancelReasonDialog!!.dismiss()
        }

        submitCancelReasonDialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        submitCancelReasonDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        submitCancelReasonDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        submitCancelReasonDialog!!.show()
    }

    private fun cancelOrderApi(otherReason: String) {
        orderViewModel.cancelOrder(orderId, cancellationReason!!, otherReason)
    }

    private fun setCancelReasonSpinner(
        spCancellationReason: Spinner,
        relOtherReason: RelativeLayout
    ) {
        if (cancelStringReasonList.isNotEmpty())
            cancelStringReasonList.clear()

        for (item in 0 until cancelReasonList!!.size) {
            cancelStringReasonList.add(cancelReasonList!![item].reason)
        }
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_item)
        adapter.add(getString(R.string.select_reason))
        adapter.addAll(cancelStringReasonList)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spCancellationReason.adapter = adapter

        spCancellationReason.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    cancellationReason = cancelReasonList!![position - 1].reason
                    //  UtilsFunctions.showToastSuccess(cancellationReason!!)
                    if (position == 1) {
                        otherReasonSelected = true
                        relOtherReason.visibility = View.VISIBLE
                    } else {
                        otherReasonSelected = false
                        relOtherReason.visibility = View.GONE
                    }
                } else {
                    otherReasonSelected = false
                    relOtherReason.visibility = View.GONE
                    cancellationReason = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun onCameraIdle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun selfieFromCamera(mKey: String) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.packageManager) == null) {
            return
        }
        val fileName = "CAMERA_" + "img" + ".jpg"
        //val photoFile = getTemporaryCameraFile(fileName)
        photoFile = File(this.externalCacheDir, fileName)
        val uri = UtilsFunctions.getValidUri(photoFile!!, this)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            selfieImage = absolutePath
        }
    }

    //region UPLOAD_SELFIE
    private fun uploadSelfieApi() {
        val mHashMap = HashMap<String, RequestBody>()
        var type = ""
        if (selfieAction == 1) {
            type = "pickup"
            mHashMap["type"] =
                Utils(this).createPartFromString("pickup")
        } else {
            type = "delivery"
            mHashMap["type"] =
                Utils(this).createPartFromString("delivery")
        }
        mHashMap["orderId"] =
            Utils(this).createPartFromString(orderId)
        var userImage: MultipartBody.Part? = null
        if (fileUri.isNotEmpty()) {
            var f1 = File(fileUri)
            f1 = File(ResizeImage.compressImage(fileUri))
            userImage =
                Utils(this).prepareFilePart(
                    "image",
                    f1
                )
        }
        orderViewModel.uploadSelfie(userImage, type, orderId, addressId)
    }

    private fun uploadSelfieObserver() {
        isFirst = true
        orderViewModel.uploadSelfieListData().observe(
            this,
            Observer<CommonModel> { response ->
                stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            //   confirmationDialog!!.dismiss()
                            if (selfieAction == 1) {
                                orderViewModel.pickupOrder(orderId)
                            } else {
                                if (dialogDelAddressList!!.size == 1) {
                                    orderViewModel.completeOrder(
                                        orderId,
                                        dialogDelAddressList!![0].id!!,
                                        orderDetail!!.driverEarning,
                                        orderDetail!!.adminComission
                                    )
                                } else {
                                    orderViewModel.completeOrder(
                                        orderId,
                                        addressId,
                                        orderDetail!!.driverEarning,
                                        orderDetail!!.adminComission
                                    )
//                                    selectDeliveryAddress()
                                }
                            }
                        }
                        else -> {
                            UtilsFunctions.showToastError(response.message!!)
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }
    //endregion

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {

                CropImage.activity(data.data)
                    .start(this)
            } else {
                if (photoFile != null) {
                    val data1 = Intent()
                    data1.data = UtilsFunctions.getValidUri(photoFile!!, this)
                    CropImage.activity(data1.data)
                        .start(this)
                }
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                fileUri = RealPathUtil.getRealPath(this, resultUri)!!
                val file = File(fileUri)
                imageFile = file

                if (selfieAction == 2 && dialogDelAddressList!!.size == 1) {
                    orderViewModel.completeOrder(
                        orderId,
                        dialogDelAddressList!![0].id!!,
                        orderDetail!!.driverEarning,
                        orderDetail!!.adminComission
                    )
                } else
                    uploadSelfieApi()
/*
                if (selfieAction == 1) {
                    orderViewModel.pickupOrder(orderId)
                } else {
                    selectDeliveryAddress()
                }
*/
                /* Glide.with(this).load(resultUri).placeholder(R.drawable.ic_user_image)
                     .error(R.drawable.ic_user_image)
                     .into(activityProfileBinding.imgProfile)*/
                //hit upload api
            }

        }
    }

    private fun loaderObserver() {
        orderViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    private fun selectDeliveryAddress() {
        selectDeliveryAddressDailog = Dialog(this)
        selectDeliveryAddressDailog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_select_address,
                null,
                false
            )
        selectDeliveryAddressDailog!!.setContentView(dialogBinding.root)
        selectDeliveryAddressDailog!!.setCancelable(false)

        val rvAddressList =
            selectDeliveryAddressDailog!!.findViewById<RecyclerView>(R.id.rv_address_list)
        val tvSubmit = selectDeliveryAddressDailog!!.findViewById<TextView>(R.id.tv_submit)
        val tvCancel = selectDeliveryAddressDailog!!.findViewById<TextView>(R.id.tv_cancel)

        setDialogDeliveryAddressAdapter(rvAddressList)

        tvSubmit.setOnClickListener {
            if (addressId != "0")
                showTakeSelfieAlert("complete_order")
            else
                showToastError("Please select address")
        }
        tvCancel.setOnClickListener {
            selectDeliveryAddressDailog!!.dismiss()
        }

        selectDeliveryAddressDailog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        selectDeliveryAddressDailog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        selectDeliveryAddressDailog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        selectDeliveryAddressDailog!!.show()
    }

    private fun setDialogDeliveryAddressAdapter(rvAddressList: RecyclerView) {
        Log.d("TAG", "listSize=---- ${dialogDelAddressList!!.size}")
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = DeliveryAddressAdapter(this, dialogDelAddressList)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rvAddressList.layoutManager = linearLayoutManager
        rvAddressList.adapter = adapter
    }

    fun completeOrder(id: String?) {
        if (id != null)
            addressId = id
    }

    private fun getTimeDifference() {
        val simpleDateFormat = SimpleDateFormat("dd/mm/yyyy hh:mmaa")

        try {
            val c = Calendar.getInstance()
            println("Current time => " + c.time)
            val df = SimpleDateFormat("dd/mm/yyyy hh:mmaa")
            val formattedDate = df.format(c.time)

            val pickupTime = orderDetail!!.pickupAddress!!.time
            val separated =
                pickupTime!!.split("- ".toRegex()).toTypedArray()
            var pickupTimeSplitted = separated[1]
            pickupTimeSplitted = pickupTimeSplitted.replace(" ", "")
            val date2 =
                simpleDateFormat.parse(orderDetail!!.pickupAddress!!.date + " " + pickupTimeSplitted)
            val date1 = simpleDateFormat.parse(formattedDate)

            val dateDiff = UtilsFunctions.getTimeDifference(date1, date2)
            countDown(dateDiff)

        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun countDown(dateDiff: String) {
        object : CountDownTimer(dateDiff.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimerStarted = true
                // activityOtpVerificationBinding.resendOTP = 1
                val millis: Long = millisUntilFinished
                val hms = String.format(
                    "%02d:%02d:%02d:%02d",
                    TimeUnit.HOURS.toDays(TimeUnit.MILLISECONDS.toDays(millis)),
                    (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(
                            millis
                        )
                    )),
                    (TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
                    (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                    ))
                )

                // println("Time : $hms")
                activityCreateOrderBinding.tvTimer.text =
                    hms                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                println("Time up")
            }
        }.start()
    }

    private fun setToolbar() {
        activityCreateOrderBinding.toolbarCommon.imgRight.visibility = View.GONE
        activityCreateOrderBinding.toolbarCommon.imgToolbarText.text = "Order Details"
    }

    private fun getIntentData() {
        orderId = intent.extras?.get("id").toString()
        userId = SharedPrefClass().getPrefValue(this, GlobalConstants.USER_ID).toString()
        if (intent.hasExtra("orderStatus"))
            orderStatus = intent.extras?.get("orderStatus")
                .toString()  // 1- Available, 2 - Active, 3 - Completed
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (clickSettings > 0) {
            checkPermission()
        }
        super.onResume()
    }

    private fun settNavigationIconAddress() {
        setNavigationIconDialog = Dialog(this)
        setNavigationIconDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_select_address,
                null,
                false
            )
        setNavigationIconDialog!!.setContentView(dialogBinding.root)
        setNavigationIconDialog!!.setCancelable(false)

        val rvAddressList =
            setNavigationIconDialog!!.findViewById<RecyclerView>(R.id.rv_address_list)
        val tvSubmit = setNavigationIconDialog!!.findViewById<TextView>(R.id.tv_submit)
        val tvCancel = setNavigationIconDialog!!.findViewById<TextView>(R.id.tv_cancel)
        tvSubmit.visibility = View.VISIBLE
        tvCancel.visibility = View.GONE

        tvSubmit.setOnClickListener {
            setNavigationIconDialog!!.dismiss()
        }

        setNavigationAddressAdapter(rvAddressList)

        setNavigationIconDialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        setNavigationIconDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setNavigationIconDialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setNavigationIconDialog!!.show()
    }

    private fun setNavigationAddressAdapter(rvAddressList: RecyclerView) {
        Log.d("TAG", "listSize=---- ${dialogDelAddressList!!.size}")
        val linearLayoutManager = LinearLayoutManager(this)
        val adapter = NavigationAddressAdapter(this, null, dialogDelAddressList)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        rvAddressList.layoutManager = linearLayoutManager
        rvAddressList.adapter = adapter
    }

    fun navigateIcon(addressData: OrdersDetailResponse.PickupAddress) {
        if (setNavigationIconDialog != null)
            setNavigationIconDialog!!.dismiss()

        val gmmIntentUri =
            Uri.parse("google.navigation:q=${addressData.lat},${addressData.long}&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun drawPolyline(
        sourceLatLng: LatLng,
        destinationLatLng: LatLng,
        isSourceAdded: Boolean
    ) {
        var path: MutableList<LatLng> = ArrayList()
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyA7Zj-PTZm4sDG-eoiLPA-XohvgxBe95wU"/*getString(R.string.maps_api_key)*/)
            .build()
        val req = DirectionsApi.getDirections(
            context,
            /*sourceLatLng.latitude.toString().plus(",").plus(sourceLatLng.longitude),
            destinationLatLng.latitude.toString().plus(",").plus(destinationLatLng.longitude)*/
            sourceLatLng.latitude.toString().plus(",").plus(sourceLatLng.longitude.toString()),
            destinationLatLng.latitude.toString().plus(",")
                .plus(destinationLatLng.longitude.toString())
        )
        try {
            val res = req.await()
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (i in 0 until route.legs.size) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in 0 until leg.steps.size) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (k in 0 until step.steps.size) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to latLongList of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                // path.add(LatLng(coord1.lat, coord1.lng))
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to latLongList of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //  mGoogleMap.drawPolyline("Destination is not detected,unable to draw path")
                Log.d("MapPath", "Unable to draw path")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        drawLine(path, sourceLatLng, destinationLatLng, isSourceAdded)

    }

    var polyPath: MutableList<LatLng>? = null
    private fun drawLine(
        path: MutableList<LatLng>,
        sourceLatLng: LatLng,
        destinationLatLng: LatLng, isSourceAdded: Boolean
    ) {
        //  polyPath = path
        if (polyPath != null)
            polyPath!!.addAll(path)
        else
            polyPath = path
        // mGoogleMap?.clear()
        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorPrimary).width(16f)
            /*polylineFinal = */mGoogleMap?.addPolyline(opts)
        }

    }

    override fun onConnectError() {
        // TODO("Not yet implemented")
    }

    override fun onConnected() {
        //TODO("Not yet implemented")
    }

    override fun onDisconnected() {
        //TODO("Not yet implemented")
    }

}
