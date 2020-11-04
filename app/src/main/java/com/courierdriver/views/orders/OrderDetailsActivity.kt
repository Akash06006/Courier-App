package com.courierdriver.views.orders

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
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
import android.os.Environment
import android.os.Handler
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.orders.DetailDeliveryAddressAdapter
import com.courierdriver.callbacks.SelfieCallBack
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.ActivityOrderDetailsBinding
import com.courierdriver.maps.FusedLocationClass
import com.courierdriver.model.CancelReasonModel
import com.courierdriver.model.CommonModel
import com.courierdriver.model.order.ListsResponse
import com.courierdriver.model.order.OrdersDetailResponse
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseActivity
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.utils.RealPathUtil
import com.courierdriver.viewmodels.order.OrderDetailViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderDetailsActivity : BaseActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    GoogleMap.OnCameraIdleListener, DialogssInterface, SelfieCallBack {
    private lateinit var activityCreateOrderBinding: ActivityOrderDetailsBinding
    private lateinit var orderViewModel: OrderDetailViewModel
    private var selfieDialog: Dialog? = null
    var vehicleList = ArrayList<ListsResponse.VehicleData>()
    private var cancellationReason: String? = null
    var fileUri = ""

    // var bannersList = ArrayList<ListsResponse.BannersData>()
    var deliveryTypeList = ArrayList<ListsResponse.DeliveryOptionData>()
    var weightList = ArrayList<ListsResponse.WeightData>()
    private var check: Int = 0
    private var confirmationDialog: Dialog? = null
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private var mGoogleMap: GoogleMap? = null
    private var mPermissionCheck = false
    private var dialog: Dialog? = null
    private var locationDialog: Dialog? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var click_settings = 1
    private var click_gps = 1
    var imageFile: File? = null
    private var mHandler = Handler()
    private var mLatitude: String? = null
    private var mLongitude: String? = null
    private var scan = 0
    private var cancelOrderAlertDialog: Dialog? = null
    private var submitCancelReasonDialog: Dialog? = null
    private var cancelStringReasonList = ArrayList<String?>()
    private var cancelReasonList: ArrayList<CancelReasonModel.Body>? = ArrayList()
    private var mDialogClass = DialogClass()
    private var start = 0
    private var permanent_deny = 0
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private var mContext: Context? = null
    private var locationManager: LocationManager? = null;
    private val MIN_TIME = 400;
    private val MIN_DISTANCE = 1000;
    private var mFusedLocationClass: FusedLocationClass? = null
    internal var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocation: Location? = null
    internal var cameraZoom = 16.0f
    private var mAddress = ""
    var selfieImage = ""
    var orderId = ""
    var userId = ""
    private var photoFile: File? = null
    private val CAMERA_REQUEST = 1808
    internal lateinit var mLastLocation: Location
    internal lateinit var mLocationCallback: LocationCallback
    internal var mCurrLocationMarker: Marker? = null
    internal lateinit var mLocationRequest: LocationRequest
    var reasons = java.util.ArrayList<String>()
    var cancelledCharges = "0"
    override fun getLayoutId(): Int {
        return R.layout.activity_order_details
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

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            orderViewModel.orderDetail(orderId)
            //   orderViewModel.cancelReason(userId)
        }
    }

    override fun initViews() {
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.maps_api_key))
        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
        }
        activityCreateOrderBinding = viewDataBinding as ActivityOrderDetailsBinding
        orderViewModel = ViewModelProviders.of(this).get(OrderDetailViewModel::class.java)
        activityCreateOrderBinding.orderDetailViewModel = orderViewModel
        mContext = this
        activityCreateOrderBinding.toolbarCommon.imgRight.visibility = View.GONE

        val supportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        dialog = Dialog(this)
        reasons.add("Select Reason")
        orderId = intent.extras?.get("id").toString()
        val activeOrder = intent.extras?.get("active").toString()
        userId = SharedPrefClass().getPrefValue(this, GlobalConstants.USER_ID).toString()
        if (activeOrder.equals("true")) {
            // activityCreateOrderBinding.bottomButtons.visibility = View.VISIBLE
        } else {
            //  activityCreateOrderBinding.bottomButtons.visibility = View.GONE
        }
        cancelReasonObserver()
        orderViewModel.cancelOrderRes().observe(this,
            Observer<CommonModel> { response ->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
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
            Observer<OrdersDetailResponse> { response ->
                stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    when {
                        response.code == 200 -> {
                            activityCreateOrderBinding.orderDetailModel = response.data
                            cancelledCharges = response.data?.cancellationCharges!!
                            activityCreateOrderBinding.toolbarCommon.imgToolbarText.text =
                                "Order #" + response.data?.orderNo!!
                            setDeliveryAddressAdapter(response.data?.deliveryAddress)
                            hideUnhideButtons(response.data!!)
                            val source = LatLng(
                                response.data?.pickupAddress?.lat!!.toDouble(),
                                response.data?.pickupAddress?.long!!.toDouble()
                            )

                            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(source))
                            mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(16f))
                            var isSourceAdded = false
                            var destination: LatLng?
                            for (item in response.data?.deliveryAddress!!) {
                                destination = LatLng(
                                    item.lat!!.toDouble(),
                                    item.long!!.toDouble()
                                )
                                var oldLatLong = LatLng(0.0, 0.0)
                                if (!isSourceAdded) {
                                    isSourceAdded = true
                                    drawPolyline(source, destination, true)
                                    oldLatLong = destination
                                } else {
                                    drawPolyline(oldLatLong, destination, false)
                                    oldLatLong = destination
                                }
                            }
                            //drawPolyline()
                        }
                        else -> message?.let { UtilsFunctions.showToastError(it) }
                    }
                }
            })


        orderViewModel.acceptOrderData().observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })

        orderViewModel.pickupOrderData().observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
        orderViewModel.completeOrderData().observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })

        orderViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "tv_complete_order" -> {
                        showTakeSelfieAlert("complete_order")
                        orderViewModel.completeOrder(orderId, "")
                    }
                    "tv_accepted_cancel_order" -> {
                        showCancelOrderAlert()
                    }
                    "tv_accepted_take_order" -> {
                        showTakeSelfieAlert("take_order")
                        orderViewModel.pickupOrder(orderId)
                    }
                    "tv_available_cancel_order" -> {
                        showCancelOrderAlert()
                    }
                    "tv_available_accept_order" -> {
                        orderViewModel.acceptOrder(orderId)
                    }
                }
            })
        )

    }

    private fun cancelReasonObserver() {
        orderViewModel.cancelReason()
        orderViewModel.cancelReasonData().observe(this,
            Observer<CancelReasonModel> { response ->
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
        cancelOrderAlertDialog = mDialogClass!!.setDefaultDialog(
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
            selfieDialog = mDialogClass.setUploadSelfieConfirmationDialog(
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


    private fun hideUnhideButtons(data: OrdersDetailResponse.Data) {
        var orderStatus = data.orderStatus?.status // 1 available, 2 active, 3 completed
        var orderStatusName = data.orderStatus?.statusName
        if (orderStatus.equals("1")) {
            activityCreateOrderBinding.llAvailable.visibility = View.VISIBLE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        } else if (orderStatus.equals("2")) {
            if (orderStatusName.equals("pickup")) {
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.VISIBLE
            } else {
                activityCreateOrderBinding.llAvailable.visibility = View.GONE
                activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.VISIBLE
                activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
            }

        } else if (orderStatus.equals("3")) {
            activityCreateOrderBinding.llAvailable.visibility = View.GONE
            activityCreateOrderBinding.llAcceptedTakeOrder.visibility = View.GONE
            activityCreateOrderBinding.llCompleteOrder.visibility = View.GONE
        }

    }

    private fun setDeliveryAddressAdapter(deliveryAddressList: ArrayList<OrdersDetailResponse.PickupAddress>?) {
        val linearLayoutManager = LinearLayoutManager(this)
        val deliveryAddressAdapter =
            DetailDeliveryAddressAdapter(this, deliveryAddressList!!)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        activityCreateOrderBinding.rvDeliveryAddress.layoutManager = linearLayoutManager
        activityCreateOrderBinding.rvDeliveryAddress.adapter = deliveryAddressAdapter
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
    override fun onMapReady(googleMap: GoogleMap) {
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

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(applicationContext, "connection failed", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionSuspended(p0: Int) {
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
        requestCode: Int, permissions: Array<String>,
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
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
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
        val tvCancel = submitCancelReasonDialog!!.findViewById<TextView>(R.id.tv_cancel)
        val relOtherReason =
            submitCancelReasonDialog!!.findViewById<RelativeLayout>(R.id.rel_other_reason)
        val etOtherReason =
            submitCancelReasonDialog!!.findViewById<EditText>(R.id.et_other_reason)

        setCancelReasonSpinner(spinnerReason, relOtherReason)

        tvSubmit.setOnClickListener {
            if (TextUtils.isEmpty(cancellationReason))
                UtilsFunctions.showToastError(getString(R.string.please_select_reason))
            else
                cancelOrderApi(etOtherReason.text.toString())
        }
        tvCancel.setOnClickListener {
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
        orderViewModel.cancelOrder(orderId!!, cancellationReason!!, otherReason)
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
                    UtilsFunctions.showToastSuccess(cancellationReason!!)
                    if (position == 1)
                        relOtherReason.visibility = View.VISIBLE
                    else
                        relOtherReason.visibility = View.GONE
                } else {
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

    private fun drawPolyline(
        sourceLatLng: LatLng,
        destinationLatLng: LatLng,
        isSourceAdded: Boolean
    ) {
        var path: MutableList<LatLng> = ArrayList()
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.maps_api_key))
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
        polyPath = path
        // mGoogleMap?.clear()
        var ic_source: BitmapDescriptor
        if (isSourceAdded) {
            ic_source = bitmapDescriptorFromVector(
                this,
                R.drawable.your_loc_marker
            )//ic_source
        } else {
            /* ic_source = BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)*/
            ic_source = bitmapDescriptorFromVector(
                this,
                R.drawable.destination_marker
            )
        }
        // var ic_destination = BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)
        var ic_destination = bitmapDescriptorFromVector(
            this,
            R.drawable.destination_marker
        )
        //var icon = bitmapDescriptorFromVector(this, R.drawable.ic_map_pin)
        //  if (isSourceAdded) {
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(sourceLatLng.latitude, sourceLatLng.longitude))
                .icon(ic_source)
        )
        //  }
        mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(destinationLatLng.latitude, destinationLatLng.longitude))
                //.snippet(points[0].longitude.toString() + "")
                .icon(ic_destination)
        )
        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorPrimary).width(16f)
            /*polylineFinal = */mGoogleMap?.addPolyline(opts)
        }

    }

    override fun selfieFromCamera(mKey: String) {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.getPackageManager()) == null) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {

                CropImage.activity(data.data)
                    .start(this);
            } else {
                if (photoFile != null) {
                    val data1 = Intent()
                    data1.data = UtilsFunctions.getValidUri(photoFile!!, this)
                    CropImage.activity(data1.data)
                        .start(this);
                }
            }


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val resultUri = result.uri
                fileUri = RealPathUtil.getRealPath(this, resultUri)!!
                val file = File(fileUri)
                imageFile = file
                /* Glide.with(this).load(resultUri).placeholder(R.drawable.ic_user_image)
                     .error(R.drawable.ic_user_image)
                     .into(activityProfileBinding.imgProfile)*/
                //hit upload api
            }

        }

    }

}
