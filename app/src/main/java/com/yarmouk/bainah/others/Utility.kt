package com.yarmouk.bainah.others

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Utility {

    const val RC_LOCATION_PERMISSION = 22

    fun hasLocationPermission(context: Context):Boolean{
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}