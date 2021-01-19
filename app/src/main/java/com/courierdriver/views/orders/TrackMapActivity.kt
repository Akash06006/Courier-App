package com.courierdriver.views.orders

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ActivityManager
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
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import com.courierdriver.adapters.orders.NavigationAddressAdapter
import com.courierdriver.application.MyApplication
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityTrackMapBinding
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.offlineSyncing.NetworkChangeReceiver
import com.courierdriver.utils.service.NetworkChangeCallback
import com.courierdriver.utils.service.NotiFyRestartTrackingReceiver
import com.courierdriver.utils.service.RestartTrackingBroadcastReceiver
import com.courierdriver.utils.service.TrackingService
import com.courierdriver.viewmodels.order.OrderDetailViewModel
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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException


class TrackMapActivity : BaseActivity(), NotiFyRestartTrackingReceiver, NetworkChangeCallback,
    OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.
    OnConnectionFailedListener, LocationListener {
    private var binding: ActivityTrackMapBinding? = null
    private lateinit var orderViewModel: OrderDetailViewModel
    private var trackingService: TrackingService? = null
    private var currentLatitude: Double = 0.toDouble()
    private var currentLongitude: Double = 0.toDouble()
    private var builder: LatLngBounds.Builder = LatLngBounds.Builder()
    var mSocket: Socket? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private var mNetworkReceiver: BroadcastReceiver? = null
    private var TRACK_RIDE = 0
    private var isConnected = false
    var userId = ""
    var orderId = ""
    private var mGoogleMap: GoogleMap? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLastLocation: Location
    private var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mLocationCallback: LocationCallback
    private var mPermissionCheck = false
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var dialog: Dialog? = null
    private var scan = 0
    private var clickSettings = 1
    private var permanent_deny = 0
    private var setNavigationIconDialog: Dialog? = null
    private var dialogDelAddressList: ArrayList<OrdersDetailResponse.PickupAddress>? = null
    private var orderDetails: OrdersDetailResponse.Data? = OrdersDetailResponse.Data()
    private var marker: Marker? = null
    private var deliveryList: ArrayList<OrdersDetailResponse.PickupAddress>? = ArrayList()

    override fun initViews() {
        binding = viewDataBinding as ActivityTrackMapBinding?
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        binding!!.orderDetailViewModel = orderViewModel

        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.maps_api_key))

        initMap()
        viewClicks()
        networkChangeCheckReciever()
        subscribeTrackingBroadcastReceiver()
//        getIntentData()

        dialog = Dialog(this)
        trackingService = TrackingService()
        val mServiceIntent = Intent(this, trackingService!!::class.java)
        if (!isMyServiceRunning(trackingService!!::class.java)) {
            startService(mServiceIntent)
        }

        userId = SharedPrefClass().getPrefValue(this, GlobalConstants.USER_ID).toString()
    }

    private fun getIntentData() {
        if (intent.hasExtra("orderDetails"))
            orderDetails = intent.getSerializableExtra("orderDetails") as OrdersDetailResponse.Data?
        orderId = orderDetails!!.id!!
        //  setDeliveryAddressAdapter(orderDetails!!.deliveryAddress)

        setPickupDestinationMarker(orderDetails!!)
        deliveryList = orderDetails!!.deliveryAddress!!
        // customerId = orderDetails!!.userId!!
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
    }

    private fun setPickupDestinationMarker(response: OrdersDetailResponse.Data) {
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
        if (mGoogleMap != null) {
            if (response.pickedUp == false) {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.pickupAddress!!.lat!!.toDouble(),
                                response.pickupAddress!!.long!!.toDouble()
                            )
                        )
                        .title(response.pickupAddress!!.address)
                        .icon(icPickupPending)
                )
            } else {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.pickupAddress!!.lat!!.toDouble(),
                                response.pickupAddress!!.long!!.toDouble()
                            )
                        )
                        .title(response.pickupAddress!!.address)
                        .icon(icPickup)
                )
            }
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

        for (i in 0 until response.deliveryAddress!!.size) {
            if (response.deliveryAddress!![i].isComplete == false) {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.deliveryAddress!![i].lat!!.toDouble(),
                                response.deliveryAddress!![i].long!!.toDouble()
                            )
                        )
                        .title(response.deliveryAddress!![i].address)
                        .icon(icDestination)
                )
            } else {
                marker = mGoogleMap!!.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                response.deliveryAddress!![i].lat!!.toDouble(),
                                response.deliveryAddress!![i].long!!.toDouble()
                            )
                        )
                        .title(response.deliveryAddress!![i].address)
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

    private fun viewClicks() {
        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "rel_navigation" -> {
                        settNavigationIconAddress()
                    }
                }
            })
        )
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
        val linPickup = setNavigationIconDialog!!.findViewById<LinearLayout>(R.id.lin_pickup)
        val tvPickupAddress = setNavigationIconDialog!!.findViewById<TextView>(R.id.tv_pickup_address)
        val relPickupAddress = setNavigationIconDialog!!.findViewById<RelativeLayout>(R.id.rel_pickup_address)
        tvSubmit.visibility = View.VISIBLE
        tvCancel.visibility = View.GONE

        if(orderDetails!!.orderStatus?.status == "2") {
            if (orderDetails!!.orderStatus?.statusName.equals("Picked Up")) {
                linPickup.visibility = View.GONE
            } else {
                tvPickupAddress.text = orderDetails!!.pickupAddress!!.address
                linPickup.visibility = View.VISIBLE
            }
        }

        relPickupAddress.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("google.navigation:q=${orderDetails!!.pickupAddress!!.lat},${orderDetails!!.pickupAddress!!.long}&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
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
        val adapter = NavigationAddressAdapter(null, this, dialogDelAddressList)
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

    private fun initMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationRequest()
        setLocation()
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
    }
    //endregion

    //region CURRENT_LOCATION
    private fun getLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 10000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (clickSettings > 0) {
            checkPermission()
        }
        super.onResume()
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
//        getIntentData()
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

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mGoogleMap = googleMap
        this.mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        mGoogleMap!!.uiSettings.isZoomControlsEnabled = true

        getIntentData()
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

    override fun getLayoutId(): Int {
        return R.layout.activity_track_map
    }
}