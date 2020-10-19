package com.example.courier.maps

/*
 * Created by Akash on 25-09-2020.
 */

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * constructor
 *
 */
class FusedLocationClass(private val mContext : Context?) : GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    private var mGoogleApiClient : GoogleApiClient? = null
    private var mLocationRequest : LocationRequest? = null

    init {
        if (mContext != null) {
            buildGoogleApiClient()
        }
    }
    /**
     * destructor
     *
     */
    ///////////// 1
    /**
     * builds a GoogleApiClient
     */
    @Synchronized
    private fun buildGoogleApiClient() {
        // setup googleApi client
        mGoogleApiClient = GoogleApiClient.Builder(mContext!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        // setup location updates
        configRequestLocationUpdate()
    }
    ///////////// 2
    /**
     * config request location update
     */
    @SuppressLint("RestrictedApi")
    private fun configRequestLocationUpdate() {
        mLocationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(LOCATION_FREQUENCY.toLong())
            .setFastestInterval(FAST_LOCATION_FREQUENCY.toLong())
    }
    ///////////// 3
    /**
     * request location updates
     *
     */
    private fun requestLocationUpdates(mContext : Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
                LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    mLocationRequest,
                    this
                )
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
            )
    }

    /**
     * start location updates
     *
     */
    private fun startLocationUpdates(mContext : Context) {
        // connect and force the updates
        mGoogleApiClient!!.connect()
        if (mGoogleApiClient!!.isConnected) {
            requestLocationUpdates(mContext)
        }
    }

    /**
     * removes location updates from the FusedLocationApi
     */
    public fun stopLocationUpdates() {
        // stop updates, disconnect from google api
        if (null != mGoogleApiClient && mGoogleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            mGoogleApiClient!!.disconnect()
        }

    }

    /**
     * get last available location
     *
     * @return last known location
     */
    fun getLastLocation(mContext : Context) : Location? {
        if (null != mGoogleApiClient && mGoogleApiClient!!.isConnected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
                // return last location
                    return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            } else
            // return last location
                return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        } else
            startLocationUpdates(mContext) // start the updates
        return null
    }

    /***********************************************************************************************
     * GoogleApiClient Callbacks
     */
    override fun onConnected(bundle : Bundle?) {
        // do location updates
        if (mContext != null)
            requestLocationUpdates(mContext)
    }

    override fun onConnectionSuspended(i : Int) {
        // connection to Google Play services was lost for some reason
        if (null != mGoogleApiClient) {
            mGoogleApiClient!!.connect() // attempt to establish a new connection
        }
    }

    override fun onConnectionFailed(connectionResult : ConnectionResult) {

    }

    /***********************************************************************************************
     * Location Listener Callback
     */
    override fun onLocationChanged(location : Location) {
        Log.d("Inside", "Fused")
        //Toast.makeText(mContext,"iNSIDE",Toast.LENGTH_LONG).show()
    }

    interface FusedLocationInterface {
        fun onLocationChanged(location : Location)
    }

    companion object {
        private val FAST_LOCATION_FREQUENCY = 5 * 1000
        private val LOCATION_FREQUENCY = 5 * 1000
    }
}
