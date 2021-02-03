package com.android.courier.views.orders

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.courier.R
import com.android.courier.adapters.orders.OrderAddressListAdapter
import com.android.courier.adapters.orders.PaymentOptionsListAdapter
import com.android.courier.application.MyApplication
import com.android.courier.common.UtilsFunctions
import com.android.courier.databinding.ActivityOrderDetailBinding
import com.android.courier.maps.FusedLocationClass
import com.android.courier.model.CommonModel
import com.android.courier.model.order.CancelReasonsListResponse
import com.android.courier.model.order.ListsResponse
import com.android.courier.model.order.OrdersDetailResponse
import com.android.courier.utils.BaseActivity
import com.android.courier.viewmodels.home.HomeViewModel
import com.android.courier.viewmodels.order.OrderViewModel
import com.android.courier.views.chat.ChatActivity
import com.android.courier.views.chat.DriverChatActivity
import com.android.courier.views.socket.DriverTrackingActivity
import com.bumptech.glide.Glide
import com.example.services.socket.SocketClass
import com.example.services.socket.SocketInterface
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.JsonObject
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailActivity : BaseActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnCameraIdleListener, SocketInterface {
    private lateinit var deliveryAddress : ArrayList<OrdersDetailResponse.PickupAddress>
    private lateinit var activityCreateOrderBinding : ActivityOrderDetailBinding
    private lateinit var orderViewModel : OrderViewModel
    var vehicleList = ArrayList<ListsResponse.VehicleData>()
    var bannersList = ArrayList<ListsResponse.BannersData>()
    var deliveryTypeList = ArrayList<ListsResponse.DeliveryOptionData>()
    var weightList = ArrayList<ListsResponse.WeightData>()
    private var check : Int = 0
    private var confirmationDialog : Dialog? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var mGoogleMap : GoogleMap? = null
    private var mPermissionCheck = false
    private var dialog : Dialog? = null
    private var locationDialog : Dialog? = null
    private var mGoogleApiClient : GoogleApiClient? = null
    private var click_settings = 1
    private var click_gps = 1
    private var mHandler = Handler()
    private var mLatitude : String? = null
    private var mLongitude : String? = null
    private var scan = 0
    private var start = 0
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var mContext : Context? = null
    private var locationManager : LocationManager? = null;
    private val MIN_TIME = 400;
    private val MIN_DISTANCE = 1000;
    private var mFusedLocationClass : FusedLocationClass? = null
    internal var mFusedLocationClient : FusedLocationProviderClient? = null
    private var mLocation : Location? = null
    internal var cameraZoom = 16.0f
    private var mAddress = ""
    var orderId = ""
    var isFistTime = false
    private lateinit var homeViewModel : HomeViewModel
    internal lateinit var mLastLocation : Location
    internal lateinit var mLocationCallback : LocationCallback
    internal var mCurrLocationMarker : Marker? = null
    internal lateinit var mLocationRequest : LocationRequest
    var reasons = java.util.ArrayList<String>()
    var cancelledCharges = "0"
    var phoneNumber = ""
    var driverId = ""
    var pickLat = ""
    var picktLong = ""
    var isPicked = ""
    var isCompletedCancelled = false
    var isCancellable : String? = null
    private var assignedEmployeesStatus = ""
    private var addressAdapter : OrderAddressListAdapter? = null
    private var orderDetails : OrdersDetailResponse.Data? = null
    private var socket = SocketClass.socket

    override fun getLayoutId() : Int {
        return R.layout.activity_order_detail
    }

    private var timer : Timer? = null
    private var timerTask : TimerTask? = null
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

    override fun onDestroy() {
        super.onDestroy()
        stoptimertask()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (click_settings > 0) {
            checkPermission()
        }
        /* if (click_gps > 0) {
             checkGPS()
             click_gps = 0
         }*/

        super.onResume()

        assignedEmployeesStatus = ""
        reasons.clear()
        reasons.add("Select Your Reason")
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            orderViewModel.orderDetail(orderId)
            orderViewModel.cancelReason(orderId)
        }
    }

    override fun initViews() {
        isFistTime = false
        // Initialize the SDK
        deliveryAddress = ArrayList()
        assignedEmployeesStatus = ""
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        socket.updateSocketInterface(this)
        socket.onConnect()

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }
        activityCreateOrderBinding = viewDataBinding as ActivityOrderDetailBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel::class.java)
        activityCreateOrderBinding.orderViewModel = orderViewModel
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mContext = this
        /* val userImage =
             SharedPrefClass().getPrefValue(this, GlobalConstants.USER_IMAGE).toString()
         Glide.with(this).load(userImage).placeholder(R.drawable.ic_user)
             .into(activityCreateOrderBinding.toolbarCommon.imgRight)*/
        activityCreateOrderBinding.toolbarCommon.imgHelp.visibility = View.VISIBLE
        //activityCreateOrderBinding.toolbarCommon.imgRight.setImageResource(R.drawable.ic_help_chat)
        // activityCreateOrderBinding.toolbarCommon.imgToolbarText.text = "Order #123"
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        dialog = Dialog(this)

        orderId = intent.extras?.get("id").toString()
        val activeOrder = intent.extras?.get("active").toString()
        if (activeOrder.equals("false")) {
            activityCreateOrderBinding.bottomButtons.visibility = View.GONE
        } else {
            activityCreateOrderBinding.bottomButtons.visibility = View.VISIBLE
        }

        startTimer()
        // Specify the types of place data to return.
        orderViewModel.cancelReasonRes().observe(this,
            Observer<CancelReasonsListResponse> { response->
                // stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            for (count in 0 until response.data!!.size) {
                                reasons.add(response.data!![count].reason!!)

                            }
                            //reasons.add("Other Reason")
                            // activityCreateOrderBinding.orderDetailModel = response.data
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })

        orderViewModel.cancelOrderRes().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            Log.d("TAG", "emitingSocket")
                            Log.d(
                                "Socket",
                                MyApplication.createOrdersInput.orderId + "--------" + orderId
                            )
                            val mJsonObject = JSONObject()

                            mJsonObject.put("methodName", "updateOrderStatus")
                            mJsonObject.put("orderId", MyApplication.createOrdersInput.orderId)
                            mJsonObject.put("driverId", "")
                            mJsonObject.put("orderStatus", "3")
                            //  mSocket!!.emit("socketFromClient", mJsonObject)
                            socket.sendDataToServer("updateOrderStatus", mJsonObject)

                            showToastSuccess(
                                "Order Cancelled Successfully"
                            )
                            finish()
                            // activityCreateOrderBinding.orderDetailModel = response.data
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
                            if (response.data?.orderStatus?.status != assignedEmployeesStatus) {
                                mGoogleMap!!.clear()
                                driverId = ""
                                if (!response.data?.completedorder?.empId.isNullOrEmpty()  /*!TextUtils.isEmpty(response.data?.completedorder?.empId)*/) {
                                    showDeliveryBoyRatingDialog(response.data?.completedorder)
                                }
                                var oldLatLong = LatLng(0.0, 0.0)
                                activityCreateOrderBinding.orderDetailModel = response.data
                                orderDetails = response.data
                                cancelledCharges = response.data?.cancellationCharges!!
                                pickLat = response.data?.pickupAddress?.lat!!
                                picktLong = response.data?.pickupAddress?.long!!
                                isCancellable = response.data?.cancellable
                                isPicked = response.data?.pickupAddress?.isComplete!!
                                activityCreateOrderBinding.txtBookingId.text =
                                    "Booking ID " + response.data?.orderNo!!
                                activityCreateOrderBinding.txtfare.text =
                                    "₹ " + response.data?.totalOrderPrice!!

                                if (response.data?.assignedEmployees != null) {
                                    activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                        "Enroute to Pickup"
                                    activityCreateOrderBinding.rlDriverDetail.visibility =
                                        View.VISIBLE
                                    activityCreateOrderBinding.rlPayment.visibility = View.VISIBLE
                                    Glide.with(this).load(response.data?.assignedEmployees?.image)
                                        .placeholder(R.drawable.ic_user)
                                        .into(activityCreateOrderBinding.imgDriver)
                                    driverId = response.data?.assignedEmployees?.id!!
                                    phoneNumber = response.data?.assignedEmployees?.phoneNumber!!
                                    activityCreateOrderBinding.txtDelBoyName.text =
                                        response.data?.assignedEmployees?.firstName + " " + response.data?.assignedEmployees?.lastName
                                } else {
                                    activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                        "Searching for a Rider"
                                    activityCreateOrderBinding.rlDriverDetail.visibility = View.GONE
                                    activityCreateOrderBinding.rlPayment.visibility = View.GONE

                                }
                                /*if (response.data?.orderStatus.contains("Can")) {
                                activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                    "response.data?.orderStatus"
                            }

                            if(response.data?.orderStatus.contains("")){

                            }*/
                                //if (addressAdapter == null) {
                                addressAdapter = null
                                setAddressAdapter(response)
                                /* } else {
                                     addressAdapter!!.notifyDataSetChanged()
                                 }*/
                                if (response.data?.assignedEmployees != null) {
                                    val paymentAdapter =
                                        PaymentOptionsListAdapter(
                                            this,
                                            response.data?.assignedEmployees?.payViaNew!!
                                        )
                                    val linearLayoutManager1 = LinearLayoutManager(this)
                                    linearLayoutManager1.orientation = RecyclerView.HORIZONTAL
                                    activityCreateOrderBinding.rvPaymentOptions.layoutManager =
                                        linearLayoutManager1
                                    activityCreateOrderBinding.rvPaymentOptions.setHasFixedSize(true)
                                    activityCreateOrderBinding.rvPaymentOptions.adapter =
                                        paymentAdapter
                                    activityCreateOrderBinding.rvPaymentOptions.addOnScrollListener(
                                        object :
                                            RecyclerView.OnScrollListener() {
                                            override fun onScrolled(
                                                recyclerView : RecyclerView,
                                                dx : Int,
                                                dy : Int
                                            ) {

                                            }
                                        })
                                }
                                val source = LatLng(
                                    response.data?.pickupAddress?.lat!!.toDouble(),
                                    response.data?.pickupAddress?.long!!.toDouble()
                                )
                                var ic_source : BitmapDescriptor
                                if (!TextUtils.isEmpty(response.data?.pickupAddress!!.isComplete) && response.data?.pickupAddress!!.isComplete.equals(
                                        "false"
                                    )
                                ) {
                                    isPicked = "false"
                                    ic_source = bitmapDescriptorFromVector(
                                        this,
                                        R.drawable.ic_source
                                    )
                                    activityCreateOrderBinding.imgNavigate.visibility = View.VISIBLE
                                    mGoogleMap!!.addMarker(
                                        MarkerOptions()
                                            .position(source)
                                            .icon(ic_source)
                                    )
                                } else if (!TextUtils.isEmpty(response.data?.pickupAddress!!.isComplete) && response.data?.pickupAddress!!.isComplete.equals(
                                        "true"
                                    )
                                ) {
                                    isPicked = "true"
                                    ic_source = bitmapDescriptorFromVector(
                                        this,
                                        R.drawable.ic_source_completed
                                    )
                                    activityCreateOrderBinding.imgNavigate.visibility = View.VISIBLE
                                    mGoogleMap!!.addMarker(
                                        MarkerOptions()
                                            .position(source)
                                            .icon(ic_source)
                                    )
                                }

                                mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(source))
                                if (response.data?.distance!!.toDouble() < 20) {
                                    mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f))
                                } else {
                                    mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(12f))
                                }
                                var isSourceAdded = false
                                var destination : LatLng?
                                deliveryAddress = response.data?.deliveryAddress!!
                                for (item in response.data?.deliveryAddress!!) {
                                    destination = LatLng(
                                        item.lat!!.toDouble(),
                                        item.long!!.toDouble()
                                    )
                                    var dest : BitmapDescriptor
                                    if (item.isComplete.equals("false")) {
                                        dest = bitmapDescriptorFromVector(
                                            this,
                                            R.drawable.drop_pendig
                                        )
                                    } else {
                                        dest = bitmapDescriptorFromVector(
                                            this,
                                            R.drawable.drop_completed
                                        )
                                    }
                                    mGoogleMap!!.addMarker(
                                        MarkerOptions()
                                            .position(
                                                LatLng(
                                                    item.lat!!.toDouble(),
                                                    item.long!!.toDouble()
                                                )
                                            )
                                            .icon(dest)
                                    )

                                    if (!isSourceAdded) {
                                        isSourceAdded = true
                                        //TODO-- drawPolyline(source, destination, true)
                                        oldLatLong = destination
                                    } else {
                                        //TODO--  drawPolyline(oldLatLong, destination, false)
                                        oldLatLong = destination
                                    }
                                }
                                val builder = LatLngBounds.Builder();
                                /* for (Marker marker : markers) {
                                 builder.include(marker);
                             }*/
                                builder.include(LatLng(pickLat.toDouble(), picktLong.toDouble()))
                                for (item in response.data?.deliveryAddress!!) {
                                    builder.include(
                                        LatLng(
                                            item.lat!!.toDouble(),
                                            item.long!!.toDouble()
                                        )
                                    )
                                }
                                var bounds = builder.build();
                                var padding = 200  // offset from edges of the map in pixels
                                var cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mGoogleMap!!.animateCamera(cu)

                                assignedEmployeesStatus = response.data?.orderStatus?.status!!

                                if (response.data?.orderStatus?.status.equals("3") || response.data?.orderStatus?.status.equals(
                                        "4"
                                    ) || response.data?.orderStatus?.status.equals("5")
                                ) {
                                    activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                        "Order Cancelled"
                                    activityCreateOrderBinding.bottomButtons.visibility = View.GONE
                                    activityCreateOrderBinding.imgNavigate.visibility = View.GONE
                                    isCompletedCancelled = true
                                } else if (response.data?.orderStatus?.status.equals("7")) {
                                    activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                        "Delivered at Drop Location"
                                    stoptimertask()
                                    activityCreateOrderBinding.bottomButtons.visibility = View.GONE
                                    activityCreateOrderBinding.imgNavigate.visibility = View.GONE
                                    isCompletedCancelled = true
                                } else if (response.data?.orderStatus?.status.equals("6")) {
                                    activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                        "Order has picked up"
                                }
                                //drawPolyline()
                            }
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })



        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "imgHelp" -> {
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        startActivity(intent)
                    }
                    "imgCall" -> {
                        if (!isCompletedCancelled) {
                            val dialIntent = Intent(Intent.ACTION_DIAL)
                            dialIntent.data = Uri.parse("tel:" + phoneNumber)
                            startActivity(dialIntent)
                        } else {
                            UtilsFunctions.showToastWarning("This order is not going anymore, You can not call the rider")
                        }

                    }
                    "imgChat" -> {
                        if (!isCompletedCancelled) {
                            val intent = Intent(this, DriverChatActivity::class.java)
                            intent.putExtra("orderId", orderId)
                            intent.putExtra("driverId", driverId)
                            startActivity(intent)
                        } else {
                            UtilsFunctions.showToastWarning("This order is not going anymore, You can not text the rider")
                        }
                    }
                    "btnCancel" -> {
                        /* */
                        // Set the fields to specify which types of place data to
                        if (!TextUtils.isEmpty(isCancellable) && isCancellable.equals("true")) {
                            if (cancelledCharges == "0")
                                showCancelReasonDialog()
                            else
                                showAlertForCancelDialog()
                        } else {
                            showToastError("You can not cancel this order as your order is in transit")
                        }
/*you can not cancel this order or your order in transit
*/
                    }
                    "imgNavigate" -> {
                        val intent = Intent(this, DriverTrackingActivity::class.java)
                        intent.putExtra(
                            "addresses",
                            deliveryAddress
                        )
                        intent.putExtra(
                            "orderId",
                            orderId
                        )
                        intent.putExtra(
                            "pickLat",
                            pickLat
                        )
                        intent.putExtra(
                            "pickLong",
                            picktLong
                        )
                        intent.putExtra(
                            "driverId",
                            driverId
                        )
                        intent.putExtra(
                            "orderStatus",
                            isPicked
                        )
                        intent.putExtra("orderDetails", orderDetails)
                        startActivity(intent)
                    }
                    "btnSchedule" -> {
                        if (!TextUtils.isEmpty(isCancellable) && isCancellable.equals("true")) {
                            val intent = Intent(this, CreateOrderActivty::class.java)
                            intent.putExtra("id", orderId)
                            startActivity(intent)
                        } else {
                            showToastError("You can not reschedule this order  as your order is in transit")
                        }

                    }
                }
            })
        )

    }

    private fun setAddressAdapter(response : OrdersDetailResponse) {
        addressAdapter =
            OrderAddressListAdapter(
                this,
                response.data?.deliveryAddress,
                response.data?.pickupAddress
            )
        /// showToastSuccess("set adapter")
        if (!isFistTime) {
            isFistTime = true
            val controller =
                AnimationUtils.loadLayoutAnimation(
                    this,
                    R.anim.layout_animation_from_left
                )
            activityCreateOrderBinding.rvAddress.setLayoutAnimation(
                controller
            );
            activityCreateOrderBinding.rvAddress.scheduleLayoutAnimation();
        }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        activityCreateOrderBinding.rvAddress.layoutManager =
            linearLayoutManager
        activityCreateOrderBinding.rvAddress.setHasFixedSize(true)
        activityCreateOrderBinding.rvAddress.adapter = addressAdapter
        activityCreateOrderBinding.rvAddress.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView : RecyclerView,
                dx : Int,
                dy : Int
            ) {
            }
        })
    }

    /*override fun onMapReady(googleMap : GoogleMap) {
        this.mGoogleMap = googleMap
        mGoogleMap!!.setMinZoomPreference(5f)
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.isMyLocationEnabled = true
        //mHandler.postDelayed(mRunnable, 500)
        mPermissionCheck = false
        check = 0

    }*/
    //region mp
    override fun onMapReady(googleMap : GoogleMap) {
        this.mGoogleMap = googleMap

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
        //mGoogleMap?.uiSettings?.setAllGesturesEnabled(true)
        // mGoogleMap?.uiSettings?.isScrollGesturesEnabled = true
        mGoogleMap!!.uiSettings.isMapToolbarEnabled = false
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

    override fun onConnected(bundle : Bundle?) {
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

    override fun onLocationChanged(location : Location) {
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
        //mCurrLocationMarker = mMap!!.addMarker(markerOptions)
        //move map camera
        /*  mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
          mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14f))*/
        //stop location updates
        if (mGoogleApiClient != null) {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onConnectionFailed(connectionResult : ConnectionResult) {
        Toast.makeText(applicationContext, "connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(p0 : Int) {
        Toast.makeText(applicationContext, "connection suspended", Toast.LENGTH_SHORT).show()
    }

    //endregion
    fun checkPermission() {
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
        requestCode : Int, permissions : Array<String>,
        grantResults : IntArray
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
                        if (click_settings > 0) {
                            click_settings = 0
                            dialog!!.show()
                        } else {
                            if (!showRationale && permanent_deny > 0) {
                                click_settings++
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
        context : Context,
        @DrawableRes vectorDrawableResourceId : Int
    ) : BitmapDescriptor {
        var background =
            ContextCompat.getDrawable(this, vectorDrawableResourceId/*R.drawable.ic_app*/);
        background?.setBounds(
            0,
            0,
            background.getIntrinsicWidth(),
            background.getIntrinsicHeight()
        )
        var vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable?.setBounds(
            0,
            0,
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight()
        )
        var bitmap = Bitmap.createBitmap(
            vectorDrawable!!.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        // background?.draw(canvas)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showCancelReasonDialog() {
        confirmationDialog = Dialog(this, R.style.transparent_dialog)
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
            this,
            R.layout.spinner_item, reasons
        )

        adapter.setDropDownViewResource(R.layout.spinner_item);

        if (!cancelledCharges.equals("0") || !TextUtils.isEmpty(cancelledCharges)) {
            llCancelCharges?.visibility = View.VISIBLE
            txtCharges?.text = "₹ " + cancelledCharges
        } else {
            llCancelCharges?.visibility = View.GONE
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
                            startProgressDialog()
                            orderViewModel.cancelOrder(mJsonObject)
                            confirmationDialog?.dismiss()
                        }
                    }
                } else {
                    if (UtilsFunctions.isNetworkConnected()) {
                        startProgressDialog()
                        orderViewModel.cancelOrder(mJsonObject)
                        confirmationDialog?.dismiss()
                    }
                }

            } else {
                UtilsFunctions.showToastError("Please select reason")
            }
        }
        /*imgCross?.setOnClickListener {
            confirmationDialog?.dismiss()
        }*/
        if (!confirmationDialog?.isShowing()!!) {
            confirmationDialog?.show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showAlertForCancelDialog() {
        var confirmationDialog = Dialog(this, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)


        confirmationDialog?.setContentView(R.layout.cancel_alert_dialog)
        confirmationDialog?.setCancelable(true)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvAmount = confirmationDialog?.findViewById<TextView>(R.id.tv_amount)
        val tvOk = confirmationDialog?.findViewById<TextView>(R.id.tv_ok)
        val tvCancel = confirmationDialog?.findViewById<TextView>(R.id.tv_cancel)


        tvAmount!!.text = "₹" + cancelledCharges + " for cancellation"
        tvOk!!.text = "Cancel order with a fee of " + "₹" + cancelledCharges

        tvOk.setOnClickListener {
            showCancelReasonDialog()
        }
        tvCancel.setOnClickListener {
            confirmationDialog.dismiss()
        }
        if (!confirmationDialog?.isShowing()!!) {
            confirmationDialog?.show()
        }

    }

    override fun onCameraIdle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawPolyline(
        sourceLatLng : LatLng,
        destinationLatLng : LatLng,
        isSourceAdded : Boolean
    ) {
        var path : MutableList<LatLng> = ArrayList()
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
        } catch (ex : Exception) {
            ex.printStackTrace()
        }

        drawLine(path, sourceLatLng, destinationLatLng, isSourceAdded)

    }

    var polyPath : MutableList<LatLng>? = null
    private fun drawLine(
        path : MutableList<LatLng>,
        sourceLatLng : LatLng,
        destinationLatLng : LatLng, isSourceAdded : Boolean
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showDeliveryBoyRatingDialog(completedorder : ListsResponse.CompletedOrder?) {
        val confirmationDialog = Dialog(this, R.style.transparent_dialog)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)


        confirmationDialog?.setContentView(R.layout.add_delivery_rating_dialog)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgCross = confirmationDialog?.findViewById<ImageView>(R.id.imgCross)
        val imgUser = confirmationDialog?.findViewById<ImageView>(R.id.user_img)
        val txtUserName = confirmationDialog?.findViewById<TextView>(R.id.txtUserName)
        val rbRatings = confirmationDialog?.findViewById<RatingBar>(R.id.rb_ratings)
        val etReview = confirmationDialog?.findViewById<EditText>(R.id.et_review)
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)
        val rlBottom = confirmationDialog?.findViewById<RelativeLayout>(R.id.rlBottom)
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        animation.setDuration(1000)
        rlBottom?.setAnimation(animation)
        rlBottom?.animate()
        animation.start()

        txtUserName?.setText(completedorder?.firstName + " " + completedorder?.lastName)

        Glide.with(this).load(completedorder?.image).placeholder(R.drawable.ic_person)
            .into(imgUser!!)
        btnSubmit?.setOnClickListener {
            val mJsonObject = JsonObject()
            mJsonObject.addProperty(
                "companyId", completedorder?.companyId
            )
            mJsonObject.addProperty(
                "rating", rbRatings?.getRating()
            )
            mJsonObject.addProperty(
                "review", etReview?.getText().toString()
            )
            mJsonObject.addProperty(
                "orderId", completedorder?.orderId
            )
            mJsonObject.addProperty(
                "empId", completedorder?.empId
            )

            homeViewModel.addDriverRating(mJsonObject)
            confirmationDialog?.dismiss()
        }

        imgCross?.setOnClickListener {
            confirmationDialog?.dismiss()
        }
        if (!confirmationDialog?.isShowing()!!) {
            confirmationDialog?.show()
        }

    }

    override fun onSocketCall(onMethadCall : String, vararg args : Any) {
    }

    override fun onSocketConnect(vararg args : Any) {
    }

    override fun onSocketDisconnect(vararg args : Any) {
    }
}
