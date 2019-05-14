package com.parcels.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

object ConnectionDetector{

     @SuppressLint("MissingPermission")
     @JvmStatic fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return !(info == null || !info.isConnected)

    }
}