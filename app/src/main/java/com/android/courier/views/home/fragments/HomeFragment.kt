package com.android.courier.views.home.fragments

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
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.android.courier.adapters.orders.DiscountListAdapter
import com.android.courier.model.order.ListsResponse
import kotlinx.android.synthetic.main.activity_profile.view.*

class
HomeFragment : BaseFragment() {
    private var mFusedLocationClass : FusedLocationClass? = null
    private lateinit var homeViewModel : HomeViewModel
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient : FusedLocationProviderClient
    var currentLat = ""
    var currentLong = ""
    var bannersList = ArrayList<ListsResponse.BannersData>()
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
        fragmentHomeBinding.imgToolbarText.setText("Welcome, " + name)


        initDiscountsAdapter()


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

    private fun initDiscountsAdapter() {
        /*val adapter = CategoriesGridListAdapter(this@HomeFragment, categoriesList, activity!!)
        fragmentHomeBinding.gridview.adapter = adapter*/
        val discountAdapter =
            DiscountListAdapter(
                this,
                bannersList
            )
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        //val gridLayoutManager = GridLayoutManager(this, 4)
        fragmentHomeBinding.rvDiscounts.layoutManager = linearLayoutManager
        fragmentHomeBinding.rvDiscounts.setHasFixedSize(true)
        fragmentHomeBinding.rvDiscounts.adapter = discountAdapter
        fragmentHomeBinding.rvDiscounts.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView : RecyclerView, dx : Int, dy : Int) {

            }
        })

    }

    public fun showOfferInformation(pos : Int) {
        var confirmationDialog = Dialog(activity, R.style.dialogAnimation_animation)
        confirmationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(activity),
                R.layout.layout_offer_dialog,
                null,
                false
            )

        confirmationDialog?.setContentView(binding.root)
        confirmationDialog?.setCancelable(false)

        confirmationDialog?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        confirmationDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btnSubmit = confirmationDialog?.findViewById<Button>(R.id.btnSubmit)
        val imgOffer = confirmationDialog?.findViewById<ImageView>(R.id.imgOffer)
        val txtCouponName = confirmationDialog?.findViewById<TextView>(R.id.txtCouponName)
        val txtCouponCode = confirmationDialog?.findViewById<TextView>(R.id.txtCouponCode)
        val txtCouponDiscount = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDiscount)
        val txtCouponDesc = confirmationDialog?.findViewById<TextView>(R.id.txtCouponDesc)
        val layoutBottomSheet =
            confirmationDialog?.findViewById<RelativeLayout>(R.id.layoutBottomSheet)
        val animation = AnimationUtils.loadAnimation(activity!!, R.anim.anim)
        animation.setDuration(500)
        layoutBottomSheet?.setAnimation(animation)
        layoutBottomSheet?.animate()
        animation.start()
        /*txtCouponName.setText("Offer Name: " + bannersList[pos].name)
        txtCouponCode.setText(bannersList[pos].code)
        txtCouponDesc.setText(Html.fromHtml(bannersList[pos].description).toString())
        txtCouponDiscount.setText(bannersList[pos].discount + "% OFF")

        Glide.with(this).load(bannersList[pos].icon).into(imgOffer)*/
        btnSubmit?.setOnClickListener {
            confirmationDialog?.dismiss()
        }

        confirmationDialog?.show()
    }

}