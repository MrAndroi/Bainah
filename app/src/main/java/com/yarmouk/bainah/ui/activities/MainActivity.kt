package com.yarmouk.bainah.ui.activities

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.yarmouk.bainah.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() , EasyPermissions.PermissionCallbacks{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setup bottom navigation view with navigation controller
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        //This code will show/hide bottom navigation view based oon current screen
        //BottomNavigationView will be shown in home fragment and settings fragment only
        navHostFragment.findNavController().addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.fragmentHome,R.id.settingsFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else ->{
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }
        requestLocationPermission()
    }

    //Change application language
    override fun attachBaseContext(newBase: Context?) {
        val prefs = newBase!!.getSharedPreferences(
            "language",
            MODE_PRIVATE
        )
        val localeString:String? =
            prefs.getString("lang", "en")
        val myLocale = Locale(localeString!!)
        Locale.setDefault(myLocale)
        val config = newBase.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(myLocale)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                val newContext = newBase.createConfigurationContext(config)
                super.attachBaseContext(newContext)
                return
            }
        } else {
            config.locale = myLocale
        }
        super.attachBaseContext(newBase)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun requestLocationPermission(){
        if(hasLocationPermission(this)){
            return
        }
        else{
            EasyPermissions.requestPermissions(this,
                "This application needs location permission to work",101,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA
            )
        }
    }

    private fun hasLocationPermission(context: Context):Boolean{
        return EasyPermissions.hasPermissions(context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
    }

    //This function called when user deny the permission
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //This block called when user deny permission for ever
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        //This block called when user deny permission first time
        else{
            requestLocationPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    //This function called when user accepts the permission
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) = Unit
}