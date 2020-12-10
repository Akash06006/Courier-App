package com.courierdriver.views.home.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.courierdriver.R
import com.courierdriver.adapters.orders.HomeOrdersAdapter
import com.courierdriver.application.MyApplication
import com.courierdriver.common.UtilsFunctions
import com.courierdriver.constants.GlobalConstants
import com.courierdriver.databinding.FragmentHomeBinding
import com.courierdriver.maps.FusedLocationClass
import com.courierdriver.model.CancelReasonModel
import com.courierdriver.model.CommonModel
import com.courierdriver.model.RegionListModel
import com.courierdriver.model.order.OrderListModel
import com.courierdriver.sharedpreference.SharedPrefClass
import com.courierdriver.utils.BaseFragment
import com.courierdriver.utils.DialogClass
import com.courierdriver.utils.DialogssInterface
import com.courierdriver.utils.broadcastReceiver.NotifyWorkStatus
import com.courierdriver.utils.broadcastReceiver.WorkStatusBroadcastReceiver
import com.courierdriver.utils.broadcastReceiver.WorkStatusChangeAvailableButton
import com.courierdriver.viewmodels.home.HomeViewModel
import com.courierdriver.views.profile.HelpScreenActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class
HomeFragment : BaseFragment(), DialogssInterface, NotifyWorkStatus {
    private var mFusedLocationClass: FusedLocationClass? = null
    private lateinit var homeViewModel: HomeViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var currentLat: Double? = 0.0
    var currentLong: Double? = 0.0
    private lateinit var fragmentHomeBinding: FragmentHomeBinding
    private var orderList: ArrayList<OrderListModel.Body>? = null
    private var homeOrdersAdapter: HomeOrdersAdapter? = null
    private var orderStatus = 1
    private var adapterPosition = 0
    private var cancellationReason: String? = null
    private var cancelOrderAlertDialog: Dialog? = null
    private var submitCancelReasonDialog: Dialog? = null
    private var selectRegionDialog: Dialog? = null
    private var mDialogClass: DialogClass? = DialogClass()
    private var cancelReasonList: ArrayList<CancelReasonModel.Body>? = ArrayList()
    private var regionList: ArrayList<RegionListModel.Body>? = ArrayList()
    private var regionStringList: ArrayList<String>? = ArrayList()
    private var cancelStringReasonList = ArrayList<String?>()
    private var selectedRegion: String? = null
    private var orderId: String? = null
    private var regionId: String? = null
    private var available: String? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel

        sharedPrefValue()
        setToolbarTextIcons()
        viewClicks()


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(baseActivity)
        getLocationRequest()
        setLocation()

        getAvailableOrders()
        getOrderListObserver()
        acceptOrderObserver()
        cancelOrderObserver()
        // cancelReasonObserver()
        loaderObserver()
        if (TextUtils.isEmpty(regionId))
            getRegionListObserver()

        submitRegionObserver()
        subscribeWorkStatusReceiver()
        subscribeWorkStatusButtonReceiver()
        updateAvailabilityObserver()
    }

    private fun loaderObserver() {
        homeViewModel.isLoading().observe(viewLifecycleOwner, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                baseActivity.startProgressDialog()
            } else {
                baseActivity.stopProgressDialog()
            }
        })
    }

    private fun viewClicks() {
        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                when (it) {
                    "img_right" -> {
                        startActivity(Intent(baseActivity, HelpScreenActivity::class.java))
                    }
                    "tv_how_to_work" -> {
                        startActivity(Intent(baseActivity, HelpScreenActivity::class.java))
                    }
                    "tv_resume_work" -> {
                        val isAvailable = true

                        homeViewModel.updateAvailability(isAvailable)
                    }
                    "tv_available" -> {
                        getAvailableOrders()
                    }
                    "tv_active" -> {
                        clearList()
                        orderStatus = 2
                        fragmentHomeBinding.tvActive.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorTextBlue),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvActive.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorWhite
                            )
                        )
                        fragmentHomeBinding.tvAvailable.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorWhite),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvAvailable.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorBlack
                            )
                        )
                        fragmentHomeBinding.tvCompleted.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorWhite),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvCompleted.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorBlack
                            )
                        )
                        getOrderList(2)
                    }
                    "tv_completed" -> {
                        clearList()
                        orderStatus = 3

                        fragmentHomeBinding.tvCompleted.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorTextBlue),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvCompleted.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorWhite
                            )
                        )
                        fragmentHomeBinding.tvActive.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorWhite),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvActive.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorBlack
                            )
                        )
                        fragmentHomeBinding.tvAvailable.background.setColorFilter(
                            ContextCompat.getColor(baseActivity, R.color.colorWhite),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        fragmentHomeBinding.tvAvailable.setTextColor(
                            ContextCompat.getColor(
                                baseActivity,
                                R.color.colorBlack
                            )
                        )
                        getOrderList(3)
                    }
                }
            })
        )
    }

    private fun updateAvailabilityObserver() {
        homeViewModel.updateAvailabilityData().observe(
            this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            var isAvailable = true
                            SharedPrefClass().putObject(
                                MyApplication.instance,
                                GlobalConstants.AVAILABLE,
                                isAvailable.toString()
                            )
                            fragmentHomeBinding.linTabsMain.visibility = View.VISIBLE
                            fragmentHomeBinding.linNotWorking.visibility = View.GONE
                            fragmentHomeBinding.linInProgress.visibility = View.GONE

                            val workStatusData = Intent("workStatusButtonReceiver")
                            LocalBroadcastManager.getInstance(baseActivity)
                                .sendBroadcast(workStatusData)
                            getAvailableOrders()
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }


    private fun getAvailableOrders() {
        clearList()
        orderStatus = 1

        fragmentHomeBinding.tvAvailable.background.setColorFilter(
            ContextCompat.getColor(baseActivity, R.color.colorTextBlue),
            PorterDuff.Mode.SRC_ATOP
        )
        fragmentHomeBinding.tvAvailable.setTextColor(
            ContextCompat.getColor(
                baseActivity,
                R.color.colorWhite
            )
        )
        fragmentHomeBinding.tvActive.background.setColorFilter(
            ContextCompat.getColor(baseActivity, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        fragmentHomeBinding.tvActive.setTextColor(
            ContextCompat.getColor(
                baseActivity,
                R.color.colorBlack
            )
        )
        fragmentHomeBinding.tvCompleted.background.setColorFilter(
            ContextCompat.getColor(baseActivity, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        fragmentHomeBinding.tvCompleted.setTextColor(
            ContextCompat.getColor(
                baseActivity,
                R.color.colorBlack
            )
        )
        getOrderList(1)
    }

    private fun subscribeWorkStatusReceiver() {
        val contractDetailsReceiver = WorkStatusBroadcastReceiver(this)
        LocalBroadcastManager.getInstance(baseActivity)
            .registerReceiver(contractDetailsReceiver, IntentFilter("workStatusReceiver"))
    }

    private fun subscribeWorkStatusButtonReceiver() {
        val availablilityStatusReceiver = WorkStatusChangeAvailableButton()
        LocalBroadcastManager.getInstance(baseActivity)
            .registerReceiver(availablilityStatusReceiver, IntentFilter("workStatusButtonReceiver"))
    }

    override fun refreshWorkStatusData() {
        /* available =
             SharedPrefClass().getPrefValue(activity!!, GlobalConstants.AVAILABLE)
                 .toString()

         if (available == "false") {
             fragmentHomeBinding.linNotWorking.visibility = View.VISIBLE
             fragmentHomeBinding.linTabsMain.visibility = View.GONE
             fragmentHomeBinding.linInProgress.visibility = View.GONE
         } else {
             fragmentHomeBinding.linTabsMain.visibility = View.VISIBLE
             fragmentHomeBinding.linNotWorking.visibility = View.GONE
             fragmentHomeBinding.linInProgress.visibility = View.GONE
 */
        getAvailableOrders()
    }

    private fun clearList() {
        if (orderList != null)
            orderList!!.clear()
        if (homeOrdersAdapter != null)
            homeOrdersAdapter!!.notifyDataSetChanged()
    }

    //region API_CALL
    private fun getOrderList(orderStatus: Int) {
        homeViewModel.orderList(
            orderStatus.toString(),
            currentLat.toString(),
            currentLong.toString()
        )
    }

    fun acceptOrder(id: String?, adapterPosition: Int) {
        this.adapterPosition = adapterPosition
        orderId = id
        homeViewModel.acceptOrder(id!!)
    }

    private fun cancelOrderApi(otherReason: String) {
        homeViewModel.cancelOrder(orderId!!, cancellationReason!!, otherReason)
    }

    fun cancelOrder(id: String?, adapterPosition: Int) {

        showCancelOrderAlert()
        orderId = id
        this.adapterPosition = adapterPosition
        /* homeViewModel.cancelOrder(id!!, cancellationReason!!)*/
    }

    fun getRegionList() {
        homeViewModel.regionList()
    }
    //endregion

    private fun showCancelOrderAlert() {
        cancelOrderAlertDialog = mDialogClass!!.setDefaultDialog(
            baseActivity,
            this,
            "cancelOrderAlert",
            getString(R.string.no),
            getString(R.string.yes),
            getString(R.string.do_you_want_to_cancel_order)
        )
        cancelOrderAlertDialog!!.show()
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        cancelOrderAlertDialog!!.dismiss()
        showCancelReasonSubmitDialog()
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        cancelOrderAlertDialog!!.dismiss()
    }

    private fun showCancelReasonSubmitDialog() {
        submitCancelReasonDialog = Dialog(baseActivity)
        submitCancelReasonDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(baseActivity),
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

    //region Observers
    private fun getOrderListObserver() {
        homeViewModel.getOrderListData().observe(
            this,
            Observer<OrderListModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    fragmentHomeBinding.linTabsMain.visibility = View.VISIBLE
                    fragmentHomeBinding.linInProgress.visibility = View.GONE
                    fragmentHomeBinding.tvNoRecord.visibility = View.GONE
                    fragmentHomeBinding.rvOrderList.visibility = View.VISIBLE
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            fragmentHomeBinding.linTabsMain.visibility = View.VISIBLE
                            fragmentHomeBinding.linInProgress.visibility = View.GONE
                            fragmentHomeBinding.linNotWorking.visibility = View.GONE

                            if (response.body!!.isNotEmpty()) {
                                orderList = response.body
                                baseActivity.showToastSuccess("Updated! \nAvailable ${orderList!!.size} orders")
                                setAdapter()
                            } else {
                                fragmentHomeBinding.tvNoRecord.visibility = View.VISIBLE
                                fragmentHomeBinding.rvOrderList.visibility = View.GONE
                            }
                        }
                        400 -> {
                            fragmentHomeBinding.linInProgress.visibility = View.VISIBLE
                            fragmentHomeBinding.linTabsMain.visibility = View.GONE
                            fragmentHomeBinding.linNotWorking.visibility = View.GONE
                            // account under review
                        }
                        206 -> {
                            val isAvailable = false
                            SharedPrefClass().putObject(
                                MyApplication.instance,
                                GlobalConstants.AVAILABLE,
                                isAvailable.toString()
                            )
                            val workStatusData = Intent("workStatusButtonReceiver")
                            LocalBroadcastManager.getInstance(baseActivity)
                                .sendBroadcast(workStatusData)

                            fragmentHomeBinding.linNotWorking.visibility = View.VISIBLE
                            fragmentHomeBinding.linInProgress.visibility = View.GONE
                            fragmentHomeBinding.linTabsMain.visibility = View.GONE
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }


    private fun acceptOrderObserver() {
        homeViewModel.acceptOrderData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(message!!)
                            orderList!!.removeAt(adapterPosition)
                            homeOrdersAdapter!!.notifyItemRemoved(adapterPosition)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun cancelOrderObserver() {
        homeViewModel.cancelOrderData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    val message = response.message
                    when (response.code) {
                        200 -> {
                            submitCancelReasonDialog!!.dismiss()
                            orderList!!.removeAt(adapterPosition)
                            homeOrdersAdapter!!.notifyItemRemoved(adapterPosition)
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> UtilsFunctions.showToastError(message!!)
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun cancelReasonObserver() {
        homeViewModel.cancelReason()
        homeViewModel.cancelReasonData().observe(this,
            Observer<CancelReasonModel> { response ->
                baseActivity.stopProgressDialog()
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

    private fun getRegionListObserver() {
        getRegionList()
        homeViewModel.regionListData().observe(this,
            Observer<RegionListModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            if (response.body!!.isNotEmpty()) {
                                regionList = response.body
                                if (regionList!!.isNotEmpty())
                                    showSelectRegionDialog()
                            }
                        }
                    }
                } else {
                    UtilsFunctions.showToastError(resources.getString(R.string.internal_server_error))
                }
            })
    }

    private fun submitRegionObserver() {
        homeViewModel.profileSetupListData().observe(this,
            Observer<CommonModel> { response ->
                baseActivity.stopProgressDialog()
                if (response != null) {
                    when (response.code) {
                        200 -> {
                            UtilsFunctions.showToastSuccess(response.message!!)

                            if (submitCancelReasonDialog != null)
                                submitCancelReasonDialog!!.dismiss()

                            SharedPrefClass().putObject(
                                MyApplication.instance,
                                GlobalConstants.REGION_ID,
                                "regionId"
                            )
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

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(baseActivity)
        homeOrdersAdapter = HomeOrdersAdapter(this, orderList!!, orderStatus)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentHomeBinding.rvOrderList.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvOrderList.adapter = homeOrdersAdapter
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
        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
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


    private fun showSelectRegionDialog() {
        submitCancelReasonDialog = Dialog(baseActivity)
        submitCancelReasonDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(baseActivity),
                R.layout.dialog_select_region,
                null,
                false
            )
        submitCancelReasonDialog!!.setContentView(dialogBinding.root)
        submitCancelReasonDialog!!.setCancelable(false)

        val spinnerRegion =
            submitCancelReasonDialog!!.findViewById<Spinner>(R.id.sp_region)
        val tvSubmit = submitCancelReasonDialog!!.findViewById<TextView>(R.id.tv_submit)
        val imgCross = submitCancelReasonDialog!!.findViewById<ImageView>(R.id.img_cross)

        setRegionsSpinner(spinnerRegion)

        tvSubmit.setOnClickListener {
            homeViewModel.profileSetup(selectedRegion!!)
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


    private fun setRegionsSpinner(spRegions: Spinner) {
        if (regionStringList!!.isNotEmpty())
            regionStringList!!.clear()

        regionList?.let {
            for (item in 0 until regionList!!.size) {
                regionStringList!!.add(regionList!![item].name!!)
            }
        }

        val adapter = ArrayAdapter<String>(baseActivity, R.layout.spinner_item)
        adapter.add(getString(R.string.select_location))
        adapter.addAll(regionStringList!!)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spRegions.adapter = adapter

        spRegions.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    selectedRegion = regionList!![position - 1].id!!
                } else {
                    selectedRegion = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun sharedPrefValue() {
        val name =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERNAME).toString()
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE)
                .toString()
        regionId =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.REGION_ID)
                .toString()
        available =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.AVAILABLE)
                .toString()
    }

    private fun setToolbarTextIcons() {
        fragmentHomeBinding.toolbarCommon.toolbar.setImageResource(R.drawable.ic_back)
        fragmentHomeBinding.toolbarCommon.imgRight.visibility = View.VISIBLE
        fragmentHomeBinding.toolbarCommon.imgRight.setImageDrawable(
            ContextCompat.getDrawable(
                baseActivity,
                R.drawable.ic_help
            )
        )
        fragmentHomeBinding.toolbarCommon.imgToolbarText.text = getString(R.string.home)
    }

    //region CURRENT_LOCATION
    private fun getLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 10000
        locationRequest!!.fastestInterval = 3000
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun setLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(baseActivity)
        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 10 * 1000.toLong() // 10 seconds
        locationRequest!!.fastestInterval = 5 * 1000.toLong() // 5 seconds


        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(locationAvailability: LocationAvailability?) {
            }

            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    currentLat = location.latitude
                    currentLong = location.longitude
                }
                Log.e("Home", "onLocation $currentLat $currentLong")
                //show location on map
                //Location fetched, update listener can be removed
                //fusedLocationProviderClient!!.removeLocationUpdates(locationCallback!!)
            }
        }

        showCurrentLocationOnMap()
        startLocationUpdates()

    }

    private fun showCurrentLocationOnMap() {
        if (baseActivity.checkAndRequestPermissions()) {
            @SuppressLint("MissingPermission")
            val lastLocation = mFusedLocationClient.lastLocation
            lastLocation.addOnSuccessListener(baseActivity) { location ->
                if (location != null) {
                    //  mGoogleMap!!.clear()

                    //Go to Current Location
                    currentLat = location.latitude
                    currentLong = location.longitude
                    getAvailableOrders()

                    Log.e("Home", "showCurrentLocationOnMap $currentLat $currentLong")
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

    private fun getSettingsLocation() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        val result =
            LocationServices.getSettingsClient(baseActivity).checkLocationSettings(builder.build())

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
                                baseActivity,
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
                baseActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                baseActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!, null/* Looper */
        )
            .addOnSuccessListener { Log.d("TAG", "startLocationUpdates: onSuccess: ") }
            .addOnFailureListener { e ->
                Log.d("TAG", "startLocationUpdates: " + e.message)
            }
    }


    //endregion

/*
    override fun onResume() {
        super.onResume()
        if(orderStatus==1)
        getAvailableOrders()
    }
*/

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }
}