package com.courierdriver.utils

import android.content.Context
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by saira on 2018-03-27.
 */

class Utils(internal var context: Context) {

    fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, string
        )
    }

    fun prepareFilePart(partName: String, fileUri: File): MultipartBody.Part {
        val requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            fileUri
        )
        return MultipartBody.Part.createFormData(partName, fileUri.name, requestFile)
    }


}
