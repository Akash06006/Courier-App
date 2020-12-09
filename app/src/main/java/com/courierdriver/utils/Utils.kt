package com.courierdriver.utils

import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by saira on 2018-03-27.
 */

class Utils(internal var context: Context) {

    fun monthList(): ArrayList<String> {
        val monthList = ArrayList<String>()
        monthList.add("All")
        monthList.add("Jan")
        monthList.add("Feb")
        monthList.add("Mar")
        monthList.add("Apr")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("Aug")
        monthList.add("Sep")
        monthList.add("Oct")
        monthList.add("Nov")
        monthList.add("Dec")
        return monthList
    }

    fun yearsList(): ArrayList<String> {
        val years = ArrayList<Int>()
        val thisYear: Int = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 2015..thisYear) {
            years.add(i)
        }

        val yearStringList = ArrayList<String>()
        for (i in 0 until years.size)
        {
            yearStringList.add(years[i].toString())
        }
        return yearStringList
    }

    fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, string
        )
    }

    fun prepareFilePart(partName: String, fileUri: File): MultipartBody.Part {
        val requestFile = fileUri
            .asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, fileUri.name, requestFile)
    }
}
