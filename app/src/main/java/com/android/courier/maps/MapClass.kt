package com.android.courier.maps

/*
 * Created by Akash on 25-09-2020.
 */

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

class MapClass : OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {
    private var mMapInterface : MapInterface? = null
    private var mGeoDataClient : GeoDataClient? = null
    private var mAdapter : PlaceAutocompleteAdapter? = null
    private var mGoogleMap : GoogleMap? = null
    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data Client
     * to retrieve more details about the place.
     *
     * @see GeoDataClient.getPlaceById
     */
    private val mAutocompleteClickListener = AdapterView.OnItemClickListener { parent, view, position, id->
        /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
        val item = mAdapter!!.getItem(position)
        //HomeActivity.search_icon.setVisibility(View.GONE);
        // HomeActivity.cross_icon.setVisibility(View.VISIBLE);
        var placeId : String? = null
        var primaryText : CharSequence? = null
        if (item != null) {
            placeId = item.placeId
            primaryText = item.getPrimaryText(null)
        }
        /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
        if (placeId != null) {
            val placeResult = mGeoDataClient!!.getPlaceById(placeId)
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback)
        } else
            Log.i(TAG, "PlaceId found null ")
    }
    /**
     * Callback for results from a Places Geo Data Client query that shows the first place result in
     * the details view on screen.
     */
    private val mUpdatePlaceDetailsCallback = OnCompleteListener<PlaceBufferResponse> { task->
        try {
            val places = task.result
            // Get the Place object from the buffer.
            val place = places!!.get(0)

            mMapInterface!!.onAutoCompleteListener(place)

            Log.i(TAG, "Place details received: " + place.name)

            places.release()
        } catch (e : Exception) {
            // Request did not complete successfully
            Log.e(TAG, "Place query did not complete.", e)
        }
    }

    fun setMapFragmentAsync(mMapFragment : MapFragment, mapInterface : MapInterface) {
        this.mMapInterface = mapInterface
        mMapFragment.getMapAsync(this)
    }

    fun setSupportMapFragmentAsync(mSupportMapFragment : SupportMapFragment, mapInterface : MapInterface) {
        this.mMapInterface = mapInterface
        mSupportMapFragment.getMapAsync(this)
    }

    fun getGeoDataClient(mContext : Context) : GeoDataClient {
        // Construct a GeoDataClient for the Google Places API for Android.
        if (mGeoDataClient == null)
            mGeoDataClient = Places.getGeoDataClient(mContext, null)
        return mGeoDataClient!!
    }

    fun setUpPlaceAutoComplete(
        mAutocompleteView : AutoCompleteTextView?, mContext : Context,
        mBounds : LatLngBounds, mFilter : AutocompleteFilter
    ) {
        if (mAutocompleteView != null) {
            // Register a listener that receives callbacks when a suggestion has been selected
            mAutocompleteView.onItemClickListener = mAutocompleteClickListener
            // Set up the adapter that will retrieve suggestions from the Places Geo Data Client.
            mAdapter = PlaceAutocompleteAdapter(mContext, mGeoDataClient!!, mBounds, mFilter)

            mAutocompleteView.setAdapter<PlaceAutocompleteAdapter>(mAdapter)
        }
    }

    override fun onMapReady(googleMap : GoogleMap?) {
        if (googleMap != null) {
            this.mGoogleMap = googleMap
            if (mMapInterface != null)
                mMapInterface!!.onMapReady(googleMap)

            googleMap.setMaxZoomPreference(20f)
            googleMap.isTrafficEnabled = true
            googleMap.isBuildingsEnabled = true
            googleMap.setOnCameraIdleListener(this)
            googleMap.setOnCameraMoveStartedListener(this)
        }
    }

    override fun onCameraIdle() {
        Log.i(TAG, "onCameraIdle  ")
        if (mGoogleMap != null)
            mMapInterface!!.onCameraIdle(mGoogleMap!!.cameraPosition)
    }

    override fun onCameraMoveStarted(i : Int) {
        Log.i(TAG, "onCameraMoveStarted  ")
        mMapInterface!!.onCameraMoveStarted()
    }

    companion object {
        private val TAG = "MapClass"
    }
}
