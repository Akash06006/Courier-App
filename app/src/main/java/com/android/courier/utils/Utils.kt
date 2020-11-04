package com.android.courier.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by saira on 2018-03-27.
 */
class Utils(internal var context : Context) {
    fun createPartFromString(string : String) : RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, string
        )
    }

    fun prepareFilePart(partName : String, fileUri : File) : MultipartBody.Part {
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            fileUri
        )
        return MultipartBody.Part.createFormData(partName, fileUri.name, requestFile)
    }

    fun getAddressFromLocation(
        mContext : Context,
        mLatitude : Double,
        mLongitude : Double,
        mType : String
    ) : String {
        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        val mString = StringBuilder()
        val geocoder = Geocoder(mContext, Locale.getDefault())
        // Address found using the Geocoder.
        val addresses : List<Address>?

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 5) // Here 1 represent
            // max location result to returned, by documents it recommended 1 to 5
        } catch (ioException : IOException) {
            // Catch network or other I/O problems.
            return mString.toString()
        } catch (illegalArgumentException : IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            return ""
        }

        when (mType) {
            "Address" ->
                // Handle case where no address was found.
                if (!(addresses == null || addresses.size == 0)) {
                    if (addresses[0].getAddressLine(0) != null)
                        mString.append(addresses[0].getAddressLine(0))
                    else {
                        mString.append(addresses[0].featureName).append(" , ")
                        mString.append(addresses[0].locality).append(" , ")
                        mString.append(addresses[0].adminArea).append(" , ")
                        mString.append(addresses[0].countryName).append(" , ")
                        mString.append(addresses[0].postalCode)
                    }
                }
            "PostalCode" ->
                // Handle case where no address was found.
                if (!(addresses == null || addresses.size == 0)) {
                    mString.append(addresses[0].postalCode)
                }
            "Locality" ->
                // Handle case where no address was found.
                if (!(addresses == null || addresses.size == 0)) {
                    mString.append(addresses[0].locality)
                }
        }
        /* String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL*/
        return mString.toString()
    }

}
