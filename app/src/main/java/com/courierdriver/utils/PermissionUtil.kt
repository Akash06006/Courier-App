package com.courierdriver.utils

import android.content.pm.PackageManager


object PermissionUtil {

    private const val REQUEST_CODE = 99

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        context: BaseActivity,
        permCallback: BaseActivity.PermissionCallback?
    ) {
        var permGrantedBool = false
        when (requestCode) {
            REQUEST_CODE -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        //                        context.showSnackBar(context.getString(R.string.not_sufficient_permissions)
                        //                                + context.getString(R.string.app_name)
                        //                                + context.getString(R.string.permissions));
                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool)
                        permCallback.permGranted()
                    else {
                        permCallback.permDenied()
                    }
                }
            }
        }
    }

}
