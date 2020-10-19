package com.example.courier.maps

/*
 * Created by Akash on 25-09-2020.
 */

import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition

interface MapInterface {
    fun onMapReady(googleMap : GoogleMap)

    fun onAutoCompleteListener(place : Place)

    fun onCameraIdle(cameraPosition : CameraPosition)

    fun onCameraMoveStarted()
}
