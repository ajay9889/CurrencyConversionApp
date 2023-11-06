package com.demo.currencyconversion.Utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log

class AppUtils {
    val TAG="AppUtils"
    fun isDeviceOnline(context: Context): Boolean {
        val connManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
            if (networkCapabilities == null) {
                Log.d(TAG, "Device Offline")
                return false
            } else {
                Log.d(TAG, "Device Online")
                return true
            }
        } else {
            // below Marshmallow
            val activeNetwork = connManager.activeNetworkInfo
            if (activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable) {
                Log.d(TAG, "Device Online")
                return true
            } else {
                Log.d(TAG, "Device Offline")
                return false
            }
        }
    }
}