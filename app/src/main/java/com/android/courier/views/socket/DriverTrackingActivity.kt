package com.android.courier.views.socket

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.android.courier.R
import com.android.courier.chatSocket.ConnectionListener
import com.android.courier.databinding.FragmentTrackingBinding
import com.android.courier.maps.FusedLocationClass
import com.android.courier.maps.MapClass
import com.android.courier.maps.MapInterface
import com.android.courier.model.order.OrdersDetailResponse
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseActivity
import com.android.courier.utils.DialogClass
import com.android.courier.utils.DialogssInterface
import com.android.courier.utils.Utils
import com.example.services.socket.SocketClass
import com.example.services.socket.SocketInterface
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import org.json.JSONObject

open class DriverTrackingActivity : BaseActivity(), OnMapReadyCallback, LocationListener,
    SocketInterface,
    GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MapInterface,
    DialogssInterface,
    GoogleMap.OnInfoWindowClickListener,
    ConnectionListener {
    private var mark1 : Marker? = null
    internal var cameraZoom = 16.0f
    private var mAddress = ""
    private var mInterface : DialogssInterface? = null
    private var mGoogleMap : GoogleMap? = null
    private var mFusedLocationClass : FusedLocationClass? = null
    private var mLocation : Location? = null
    private var mLatitude : String? = null
    private var mLongitude : String? = null
    private var utils : Utils? = null
    private var mHandler = Handler()
    private var mMapClass = MapClass()
    private var mContext : Context? = null
    private var sharedPrefClass = SharedPrefClass()
    private var check : Int = 0
    private val points = ArrayList<LatLng>()
    private var mPermissionCheck = false
    private var mGoogleApiClient : GoogleApiClient? = null
    private var mLine : Polyline? = null
    private var socket = SocketClass.socket
    private var dialog : Dialog? = null
    private var locationDialog : Dialog? = null
    private var mDialogClass = DialogClass()
    private var click_settings = 1
    private var click_gps = 1
    private var scan = 0
    private var start = 0
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var mJsonObject = JSONObject()
    private val mMarkers = java.util.ArrayList<Marker>()
    var jobId = ""
    var sourceLat = "0.0"
    var sourceLong = "0.0"
    var destlat = "0.0"
    var destLong = "0.0"
    var driverLat = "0.0"
    var driverLong = "0.0"
    var istrack = ""
    var pickLat = ""
    var pickLong = ""
    var driverId = ""
    var orderStatus = ""
    var addressArray = ArrayList<OrdersDetailResponse.PickupAddress>()
    private lateinit var fkip : LatLng
    private lateinit var destLocation : LatLng
    private var orderDetails : OrdersDetailResponse.Data? = null

    // lateinit var trackingViewModel : TrackingViewModel
    lateinit var trackingActivityBinding : FragmentTrackingBinding

    //private lateinit var locationsViewModel : LocationsViewModel
    var runCall = 0
    var serviceIntent : Intent? = null
    var polyPath : MutableList<LatLng>? = null
    private var confirmationDialog : Dialog? = null
    var isCompleted = false
    val builder = LatLngBounds.Builder()
    private var markerList : ArrayList<Marker>? = ArrayList()
    private var sourceMarker : Marker? = null
    private var isEventFirstHit = true

    companion object {
        var categoryListids : ArrayList<String>? = null

    }

    override fun getLayoutId() : Int {
        return R.layout.fragment_tracking
    }

    override fun onBackPressed() {
        //  super.onBackPressed()
        /* var intent = Intent(Intent.ACTION_MAIN)
         intent.addCategory(Intent.CATEGORY_HOME)
         startActivity(intent)*/
        finish()

    }

    override fun initViews() {
        trackingActivityBinding = viewDataBinding as FragmentTrackingBinding
        //  trackingViewModel = ViewModelProviders.of(this).get(TrackingViewModel::class.java)
        mFusedLocationClass = FusedLocationClass(this)
        // locationsViewModel = ViewModelProvider(this).get(LocationsViewModel::class.java)
        // trackingActivityBinding.trackingViewModel = trackingViewModel
//        var records = locationsViewModel.allLocations
        // application.registerActivityLifecycleCallbacks(this);
        sharedPrefClass = SharedPrefClass()
        // GlobalConstants.JOB_STARTED = "true"
        categoryListids = ArrayList()
        addressArray =
            intent.extras?.get("addresses") as ArrayList<OrdersDetailResponse.PickupAddress>
        jobId =
            "" + intent.extras?.get("orderId")
        pickLat =
            "" + intent.extras?.get("pickLat")
        pickLong =
            "" + intent.extras?.get("pickLong")
        driverId =
            "" + intent.extras?.get("driverId")
        orderStatus = "" + intent.extras?.get("orderStatus")

        if (intent.hasExtra("orderDetails"))
            orderDetails = intent.getSerializableExtra("orderDetails") as OrdersDetailResponse.Data?

        trackYourRiderSocket()
        //pickLat.toDouble()
        /*   try {
               mJsonObject = JSONObject(intent.extras?.get("data")?.toString())
               jobId = mJsonObject.get("orderId").toString()
               sourceLat = mJsonObject.get("lat").toString()
               sourceLong = mJsonObject.get("lng").toString()

               destlat = mJsonObject.get("destLat").toString()
               destLong = mJsonObject.get("destLong").toString()
               //  istrack = mJsonObject.get("trackOrStart").toString()

           } catch (e : JSONException) {
               e.printStackTrace()
           }*/
        //service
        mContext = this
        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mMapClass.setSupportMapFragmentAsync(supportMapFragment!!, this)
        mMapClass.getGeoDataClient(mContext!!)
        mInterface = this
        utils = Utils(this)

        dialog = Dialog(this)
        /*dialog = mDialogClass.setPermissionDialog(
            this,
            this@DriverTrackingActivity
        )
        locationDialog = mDialogClass.setDefaultDialog(
            this,
            mInterface!!,
            "GPSCheck",
            getString(R.string.GPS_enabled)
        )*/
        //Socket Initialization
        Log.e("Connect Socket", "Track activity")
        socket.updateSocketInterface(this)
        socket.onConnect()

        Handler().postDelayed({
            callSocketMethods("getLocation")
        }, 2000)
        // callSocketMethods("trackYourRider")
        destLocation = LatLng(destlat.toDouble(), destLong.toDouble())
        /* trackingViewModel.isClick().observe(
             this, Observer<String>(function =
             fun(it : String?) {
                 when (it) {
                     "btn_clear" -> {
                         confirmationDialog = mDialogClass.setDefaultDialog(
                             this,
                             this,
                             "Finish Job",
                             getString(R.string.warning_finish_job)
                         )
                         confirmationDialog?.show()

                     }
                 }
             })
         )*/
        /*trackingViewModel.startCompleteJob().observe(this,
            Observer<CommonModel> { response->
                stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                            GlobalConstants.JOB_STARTED = "false"
                            startActivity(Intent(this, DashboardActivity::class.java))
                            finish()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })*/

    }

    private fun trackYourRiderSocket() {
        /*try {
            val socketConnectionManager : SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e : URISyntaxException) {
            e.printStackTrace()
        }


        runOnUiThread {
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

        }

        SocketConnectionManager.getInstance()
            .addEventListener("trackYourRider") { args->
                val data = args[0] as JSONArray
            }*/
    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.onDisconnect()
        if (mGoogleApiClient != null) {
            // mGoogleApiClient!!.disconnect()

        }

    }

    override fun onCameraIdle(cameraPosition : CameraPosition) {
        cameraZoom = cameraPosition.zoom
    }

    override fun onCameraMoveStarted() {
//Empty
    }

    override fun onInfoWindowClick(marker : Marker) {
//Not In Use
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onResume() {
        if (click_settings > 0) {
            checkPermission()
        }

        if (click_gps > 0) {
            checkGPS()
            click_gps = 0
        }

        super.onResume()
    }

    override fun onMapReady(googleMap : GoogleMap) {
        this.mGoogleMap = googleMap
        mGoogleMap!!.setMinZoomPreference(5f)
        buildGoogleApiClient()
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.isTrafficEnabled = false
        googleMap.isMyLocationEnabled = true
        // mHandler.postDelayed(mRunnable, 500)
        mPermissionCheck = false
        check = 0
        val source = LatLng(
            pickLat.toDouble(),
            pickLong.toDouble()
        )
        setPickupDestMarker()
    }

    private fun setPickupDestMarker() {
        var ic_source : BitmapDescriptor
        /* ic_source = bitmapDescriptorFromVector(
             this,
             R.drawable.ic_destination
         )*/
        if (orderStatus!!.equals("true")) {
            ic_source = bitmapDescriptorFromVector(
                this,
                R.drawable.ic_source_completed
            )

        } else {
            ic_source = bitmapDescriptorFromVector(
                this,
                R.drawable.ic_source
            )
        }
        var oldLatLong = LatLng(0.0, 0.0)
        sourceMarker = mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(pickLat.toDouble(), pickLong.toDouble()))
                .title(orderDetails!!.pickupAddress!!.address!!)
                .icon(ic_source)
        )
        var isSourceAdded = false
        var destination : LatLng?
        //deliveryAddress = response.data?.deliveryAddress!!
        for (item in addressArray!!) {
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
                    .position(LatLng(item.lat!!.toDouble(), item.long!!.toDouble()))
                    .title(item.address!!)
                    .icon(dest)
            )

            if (!isSourceAdded) {
                isSourceAdded = true
                //TODO-- drawPolyline(source, destination, true)
                oldLatLong = destination
            } else {
                //TODO--drawPolyline(oldLatLong, destination, false)
                oldLatLong = destination
            }
        }
        /* for (Marker marker : markers) {
             builder.include(marker);
         }*/
        builder.include(LatLng(pickLat.toDouble(), pickLong.toDouble()))
        for (item in addressArray) {
            builder.include(LatLng(item.lat!!.toDouble(), item.long!!.toDouble()))
        }
        var bounds = builder.build();
        var padding = 200  // offset from edges of the map in pixels
        var cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mGoogleMap!!.animateCamera(cu)
    }

    override fun onAutoCompleteListener(place : Place) {
//Not In Use
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
            /*val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.title("Current Position")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))*/
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 3000 //5 seconds
        mLocationRequest.fastestInterval = 3000 //3 seconds
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

    override fun onConnectionSuspended(i : Int) {
//Not In Use
    }

    override fun onConnectionFailed(connectionResult : ConnectionResult) {
//Not In Use
    }

    override fun onPause() {
        super.onPause()

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onLocationChanged(location : Location) {
        //  getCurrentLocation(location)
        val mCameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )         // Sets the center of the map to Mountain View
            .zoom(17.0f)
            .tilt(30f)
            .build()
        /*if (!SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("null") && !SharedPrefClass().getPrefValue(
                MyApplication.instance.applicationContext,
                GlobalConstants.JOBID
            ).toString().equals("0")
        ) {*/
        // var icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car)
        /* var icon = bitmapDescriptorFromVector(this, R.drawable.ic_app)
         mGoogleMap!!.addMarker(
             MarkerOptions()
                 .position(LatLng(location.latitude, location.longitude))
                 //.snippet(points[0].longitude.toString() + "")
                 .icon(icon)
         )*/

        fkip = LatLng(location.latitude, location.longitude)
        Log.e("OnLocationChange: ", "Called")

    }

    /* fun bitmapDescriptorFromVector(context : Context, @DrawableRes vectorDrawableResourceId : Int) : BitmapDescriptor {
         var background = ContextCompat.getDrawable(this, R.drawable.ic_app);
         background?.setBounds(
             0,
             0,
             background.getIntrinsicWidth(),
             background.getIntrinsicHeight()
         )
         var vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
         vectorDrawable?.setBounds(
             40,
             20,
             vectorDrawable.getIntrinsicWidth(),
             vectorDrawable.getIntrinsicHeight()
         )
         var bitmap = Bitmap.createBitmap(
             background!!.getIntrinsicWidth(),
             background.getIntrinsicHeight(),
             Bitmap.Config.ARGB_8888
         )
         var canvas = Canvas(bitmap)
         background?.draw(canvas)
         vectorDrawable?.draw(canvas)
         return BitmapDescriptorFactory.fromBitmap(bitmap);
     }*/
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

    //SOCKET FUNCTIONALITY
    private fun callSocketMethods(methodName : String) {
        val object5 = JSONObject()
        when (methodName) {
            "getLocation" -> try {
                object5.put("methodName", methodName)
                object5.put("orderId", jobId)
                object5.put("driverId", driverId)

                socket.sendDataToServer(methodName, object5)
            } catch (e : Exception) {
                e.printStackTrace()
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
        val serverResponse = jsonObject[0] as JSONObject
        var methodName = serverResponse.get("method")
        Log.e("", "serverResponse: " + serverResponse)
        try {
            this.runOnUiThread {
                when (methodName) {
                    "updateLocation" -> try {
                        //callSocketMethods("getLocation")
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                    "trackYourRider" -> {
                        try {
                            val innerResponse = serverResponse.get("data") as JSONObject

                        } catch (e1 : Exception) {
                            e1.printStackTrace()
                        }
                    }
                    "getLocation" -> try {
                        val innerResponse = serverResponse.get("data") as JSONObject
                        val trackRiderObj = innerResponse.get("trackYourRider") as JSONObject?
                        val isPickedUp = trackRiderObj!!.getString("isPickedUp")
                        val isAccepted = trackRiderObj.getString("isAccepted")

                        if (isPickedUp == "false") {
                            val trackDataArray = trackRiderObj.getJSONArray("data")

                            if (isAccepted == "true")
                                setDriverMarker(innerResponse)

                            trackDataArray?.let {
                                for (i in 0 until trackDataArray.length()) {
                                    val pos = trackDataArray.getJSONObject(i)
                                    val order = pos.getJSONObject("order")
                                    order?.let {
                                        val pickupAddress = order.getJSONObject("pickupAddress")
                                        val lat = pickupAddress.getString("lat")
                                        val lng = pickupAddress.getString("long")
                                        val address = pickupAddress.getString("address")
                                        var riderLocation : BitmapDescriptor
                                        val background = ContextCompat.getDrawable(
                                            this, R.drawable.pickup_pending
                                        )
                                        background?.setBounds(
                                            0, 0,
                                            background.intrinsicWidth, background.intrinsicHeight
                                        )
                                        val vectorDrawable =
                                            ContextCompat.getDrawable(
                                                this,
                                                R.drawable.pickup_pending
                                            );
                                        vectorDrawable?.setBounds(
                                            0,
                                            0,
                                            vectorDrawable.getIntrinsicWidth(),
                                            vectorDrawable.getIntrinsicHeight()
                                        )
                                        val bitmap = Bitmap.createBitmap(
                                            vectorDrawable!!.getIntrinsicWidth(),
                                            vectorDrawable.getIntrinsicHeight(),
                                            Bitmap.Config.ARGB_8888
                                        )
                                        val canvas = Canvas(bitmap)
                                        vectorDrawable.draw(canvas)
                                        riderLocation = BitmapDescriptorFactory.fromBitmap(bitmap)
                                        var marker = mGoogleMap!!.addMarker(
                                            MarkerOptions().position(
                                                LatLng(
                                                    lat.toDouble(),
                                                    lng.toDouble()
                                                )
                                            ).title(address)
                                                .icon(riderLocation)
                                        )
                                        markerList!!.add(marker)
                                    }
                                }
                            }
                        } else {
                            if (markerList != null && markerList!!.isNotEmpty()) {
                                for (i in 0 until markerList!!.size) {
                                    markerList!!.remove(markerList!![i])
                                    //  if (markerList != null)
                                    //      markerList!!.clear()
                                }
                                markerList = ArrayList()
                            }

                            if (jobId == innerResponse.getString("id")
                            ) {
                                if (innerResponse.getString("isOrderComplete").equals("true")) {
                                    if (!isCompleted) {
                                        isCompleted = true
                                        showCompleteAlert(
                                            this,
                                            "Order delivered successfully by rider"
                                        )
                                    }
                                } else {
                                    val ic_source : BitmapDescriptor
                                    if (sourceMarker != null)
                                        sourceMarker!!.remove()
                                    ic_source = bitmapDescriptorFromVector(
                                        this,
                                        R.drawable.ic_source_completed
                                    )

                                    sourceMarker = mGoogleMap!!.addMarker(
                                        MarkerOptions()
                                            .position(
                                                LatLng(
                                                    pickLat.toDouble(),
                                                    pickLong.toDouble()
                                                )
                                            )
                                            .title(orderDetails!!.pickupAddress!!.address!!)
                                            .icon(ic_source)
                                    )

                                    setDriverMarker(innerResponse)
                                }
                            }
                        }
                        /*if (innerResponse.getString("isOrderComplete").equals("true")) {
                            showAlert(this,"Show Order Complete Message")
                        } else {

                        }*/
                        /* Handler().postDelayed({
                             callSocketMethods("getLocation")
                         }, 2000)*/
                    } catch (e1 : Exception) {
                        e1.printStackTrace()
                    }
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }

        Handler().postDelayed(
            {
                callSocketMethods("getLocation")
            }, 5000
        )
    }

    private fun setDriverMarker(innerResponse : JSONObject) {
        if (!driverLat.equals(innerResponse.optString("lastLatitude")) && !driverLong.equals(
                innerResponse.getString("lastLongitude")
            )
        ) {
            mark1?.remove()
            //val obj = innerResponse[0] as JSONObject
            driverLat = innerResponse.optString("lastLatitude")
            driverLong = innerResponse.getString("lastLongitude")
            //innerResponse.get("to_lat").toString()
            var driverLocation : BitmapDescriptor
            /* driverLocation = bitmapDescriptorFromVector(
                                             this,
                                             R.drawable.ic_driver
                                         )*/
            val background =
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_driver/*ic_driver*//*R.drawable.ic_app*/
                );
            background?.setBounds(
                0,
                0,
                background.getIntrinsicWidth(),
                background.getIntrinsicHeight()
            )
            val vectorDrawable =
                ContextCompat.getDrawable(this, R.drawable.ic_driver);
            vectorDrawable?.setBounds(
                0,
                0,
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight()
            )
            val bitmap = Bitmap.createBitmap(
                vectorDrawable!!.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            // background?.draw(canvas)
            vectorDrawable.draw(canvas)
            driverLocation = BitmapDescriptorFactory.fromBitmap(bitmap)


            mark1 = mGoogleMap!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            driverLat.toDouble(),
                            driverLong.toDouble()
                        )
                    )
                    //.snippet(points[0].longitude.toString() + "")
                    .icon(
                        driverLocation
                    )
            )

            if (isEventFirstHit) {
                builder.include(
                    LatLng(
                        driverLat.toDouble(),
                        driverLong.toDouble()
                    )
                )
                var bounds = builder.build();
                var padding = 200  // offset from edges of the map in pixels
                var cu =
                    CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mGoogleMap!!.animateCamera(cu)

                isEventFirstHit = false
            }
        }
    }

    fun showCompleteAlert(activity : Activity, message1 : String) {
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
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
        text.text = message1
        val dialogButton = dialog.findViewById(R.id.dialogButtonOK) as Button
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()

    }

    override fun onSocketConnect(vararg args : Any) {
        //OnSocket Connect Call It
        Log.e("Socket Status : ", "Connected")
    }

    override fun onSocketDisconnect(vararg args : Any) {
        // //OnSocket Disconnect Call It
        Log.e("Socket Status : ", "Disconnected")
    }

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

    override fun onDialogConfirmAction(mView : View?, mKey : String) {
        when (mKey) {
            "GPSCheck" -> {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                click_gps++
                // locationDialog.dismiss();
                startActivity(intent)
            }
            "Finish Job" -> {
                //GlobalConstants.JOB_STARTED = "false"
                // locationsViewModel.updateJobStatus("1", jobId.toInt())
                /* var upload = UploadDataToServer()
                 upload.synchData(jobId, "track")*/
                /* SharedPrefClass().putObject(
                     MyApplication.instance.applicationContext,
                     GlobalConstants.JOB_STARTED,
                     "false"
                 )*/

                mFusedLocationClass?.stopLocationUpdates()
                /* if (UtilsFunctions.isNetworkConnected()) {
                     trackingViewModel.startJob("1", jobId)
                 } else {
                     showToastSuccess(getString(R.string.job_finished_msg))
                     startActivity(Intent(this, DashboardActivity::class.java))
                     finish()
                 }*/
            }
        }
    }

    override fun onDialogCancelAction(mView : View?, mKey : String) {
        when (mKey) {
            "GPSCheck" -> {
                dialog!!.dismiss()
                locationDialog!!.dismiss()
            }
            "Finish Job" -> {
                confirmationDialog?.dismiss()
            }
        }

    }

    /*
        override fun onCheckedChanged(p0 : CompoundButton?, p1 : Boolean) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }*/
    fun checkGPS() {
        val mLocationManager =
            mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        if (mLocationManager != null) {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            network_enabled =
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        /* if (!gps_enabled || !network_enabled) {
             checkPermission()
             locationDialog!!.show()
         } else {
             if (locationDialog!!.isShowing()) {
                 locationDialog!!.dismiss()
             }
             // locationDialog.dismiss();
             checkPermission()
         }*/

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

    /* private fun drawPolyline() {
         var path : MutableList<LatLng> = ArrayList()
         val context = GeoApiContext.Builder()
             //TODO-- .apiKey(getString(R.string.api_key))
             .apiKey(""*//*getString(R.string.api_key)*//*)
            .build()
        val req = DirectionsApi.getDirections(
            context,
            fkip.latitude.toString().plus(",").plus(fkip.longitude.toString()),
            destLocation.latitude.toString().plus(",").plus(destLocation.longitude.toString())
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

        drawLine(path)

    }

    private fun drawLine(path : MutableList<LatLng>) {
        polyPath = path
        mGoogleMap?.clear()
        //  var icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car)
        var icon = bitmapDescriptorFromVector(this, R.drawable.ic_app)
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(fkip.latitude, fkip.longitude))
                //.snippet(points[0].longitude.toString() + "")
                .icon(icon)
        )
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(destLocation.latitude, destLocation.longitude))
                //.snippet(points[0].longitude.toString() + "")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorRed).width(20f)
            *//*polylineFinal = *//*mGoogleMap?.addPolyline(opts)
        }

    }*/
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

    //var polyPath : MutableList<LatLng>? = null
    private fun drawLine(
        path : MutableList<LatLng>,
        sourceLatLng : LatLng,
        destinationLatLng : LatLng, isSourceAdded : Boolean
    ) {
        if (polyPath != null)
            polyPath!!.addAll(path)
        else
            polyPath = path


        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorPrimary).width(14f)
            /*polylineFinal = */mGoogleMap?.addPolyline(opts)
        }

    }

    override fun onConnectError() {
        TODO("Not yet implemented")
    }

    override fun onConnected() {
/*
        runOnUiThread {
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

        }
*/

    }

    override fun onDisconnected() {
        TODO("Not yet implemented")
    }

}









