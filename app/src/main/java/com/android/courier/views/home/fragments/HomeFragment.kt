package com.android.courier.views.home.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.android.courier.databinding.FragmentHomeBinding
import com.android.courier.constants.GlobalConstants
import com.android.courier.maps.FusedLocationClass
import com.android.courier.sharedpreference.SharedPrefClass
import com.android.courier.utils.BaseFragment
import com.android.courier.viewmodels.home.HomeViewModel
import com.android.courier.views.home.LandingActivty
import com.android.courier.views.orders.CreateOrderActivty
import com.google.android.gms.location.*
import com.android.courier.R
import kotlinx.android.synthetic.main.activity_profile.view.*

class
HomeFragment : BaseFragment() {
    private var mFusedLocationClass : FusedLocationClass? = null
    private lateinit var homeViewModel : HomeViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    private lateinit var fragmentHomeBinding : FragmentHomeBinding
    //var categoriesList = null
    override fun getLayoutResId() : Int {
        return R.layout.fragment_home
    }

    override fun onResume() {
        super.onResume()

    }

    //api/mobile/services/getSubcat/b21a7c8f-078f-4323-b914-8f59054c4467
    override fun initView() {
        fragmentHomeBinding = viewDataBinding as FragmentHomeBinding
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        fragmentHomeBinding.homeViewModel = homeViewModel
        // categoriesList=List<Service>()
        mFusedLocationClass = FusedLocationClass(activity)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        // initRecyclerView()
        val name = SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USERNAME).toString()
        fragmentHomeBinding.txtWelcome.setText("Welcome, " + name)


        fragmentHomeBinding.toolbar.setImageResource(R.drawable.ic_side_menu)
        val userImage =
            SharedPrefClass().getPrefValue(activity!!, GlobalConstants.USER_IMAGE).toString()
        Glide.with(activity!!).load(userImage).placeholder(R.drawable.ic_user)
            .into(fragmentHomeBinding.imgRight)
        homeViewModel.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "txtCreateOrder" -> {
                        val intent = Intent(activity, CreateOrderActivty::class.java)
                        startActivity(intent)
                    }
                    "toolbar" -> {
                        (activity as LandingActivty).openCloseDrawer()
                    }
                }
            })
        )

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

}