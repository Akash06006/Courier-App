package com.example.services.socket

import android.app.*
import android.app.Service.START_STICKY
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.courier.application.MyApplication

class LocationTrackingService : Service() {
    var locationManager : LocationManager? = null

    override fun onBind(intent : Intent?) = null

    override fun onStartCommand(intent : Intent?, flags : Int, startId : Int) : Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        if (locationManager == null)
            locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                INTERVAL,
                DISTANCE,
                locationListeners[1]
            )
        } catch (e : SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, "Network provider does not exist", e)
        }

        try {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                INTERVAL,
                DISTANCE,
                locationListeners[0]
            )
        } catch (e : SecurityException) {
            Log.e(TAG, "Fail to request location update", e)
        } catch (e : IllegalArgumentException) {
            Log.e(TAG, "GPS provider does not exist", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*if (locationManager != null)
            for (i in 0..locationListeners.size) {
                try {
                    locationManager?.removeUpdates(locationListeners[i])
                } catch (e : Exception) {
                    Log.w(TAG, "Failed to remove location listeners")
                }
            }*/
    }

    companion object {
        val TAG = "LocationTrackingService"
        val INTERVAL = 0.toLong() // In milliseconds
        val DISTANCE = 0.toFloat() // In meters
        val locationListeners = arrayOf(
            LTRLocationListener(LocationManager.GPS_PROVIDER),
            LTRLocationListener(LocationManager.NETWORK_PROVIDER)
        )

        class LTRLocationListener(provider : String) : android.location.LocationListener {
            val lastLocation = Location(provider)

            override fun onLocationChanged(location : Location?) {
                lastLocation.set(location)
                Log.e("onLocationChange: ", "Service")
                /* var jobLocationsDetails = JobLocationsDetails()
                 jobLocationsDetails.job_id = SharedPrefClass()!!.getPrefValue(
                     MyApplication.instance,
                     GlobalConstants.USERID
                 ).toString()
                 jobLocationsDetails.job_lat = location?.latitude.toString()
                 jobLocationsDetails.job_long = location?.longitude.toString()
                 jobLocationsDetails.status = "0"
                 var repo = LocationRepository()
                 repo.insert(jobLocationsDetails)
                 var upload = UploadDataToServer()
                 upload.synchData(
                     SharedPrefClass()!!.getPrefValue(
                         MyApplication.instance,
                         GlobalConstants.USERID
                     ).toString(), "track"
                 )*/
                Toast.makeText(
                    MyApplication.instance.applicationContext,
                    location?.latitude.toString() + " " + location?.longitude.toString(),
                    Toast.LENGTH_LONG
                ).show()

            }

            override fun onProviderDisabled(provider : String?) {
            }

            override fun onProviderEnabled(provider : String?) {
            }

            override fun onStatusChanged(provider : String?, status : Int, extras : Bundle?) {
            }

        }
    }
}
