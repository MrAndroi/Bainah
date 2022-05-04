package com.yarmouk.bainah.ui.fragments.submitNewReport.viewpagerFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.yarmouk.bainah.R
import com.yarmouk.bainah.ui.viewModels.MainViewModel
import com.yarmouk.bainah.util.mutation
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.info_three_fragment.*
import kotlinx.android.synthetic.main.submit_report_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

//Reference on how to create maps in android
//https://developers.google.com/maps/documentation/android-sdk/start
@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("MissingPermission")
@AndroidEntryPoint
class InfoThreeFragment : Fragment(R.layout.info_three_fragment) {


    private val mainViewModel: MainViewModel by viewModels(
        {requireParentFragment()}
    )
    private lateinit var googleMaps: GoogleMap
    private lateinit var currentLocation: LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private var marker:Marker? = null
    private lateinit var dialog: AlertDialog
    private lateinit var gnssStatusCallback: GnssStatus.Callback
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var arrayAdapter2: ArrayAdapter<String>


    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentLocation = LatLng(32.477504,35.796210)
        arrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.cars))

        arrayAdapter2 = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.yesNo))

        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())

        dialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage(getString(R.string.setting_new))
            .setCancelable(false)
            .build()

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNumberOfCars.setAdapter(arrayAdapter)
        etHaveYouHadAccident.setAdapter(arrayAdapter2)

        mapView.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {
            setUpMaps()
            locationManager = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
            gnssStatusCallback = object : GnssStatus.Callback(){
                override fun onStarted() {
                    getLastLocation()
                }

                override fun onStopped() = Unit

            }
            locationManager.registerGnssStatusCallback(gnssStatusCallback,null)
        }

        next3.setOnClickListener {
            val numberOfCars = etNumberOfCars.text.toString()
            val areaName = etAreaName.text.toString()
            val streetName = etStreetName.text.toString()

            if(currentLocation.equals(null)){
                showSnackBar(getString(R.string.location_error),R.color.red)
            }
            else if(numberOfCars.isBlank()){
                etNumberOfCars.error = getString(R.string.number_of_cars_error)
            }
            else if(areaName.isBlank()){
                etAreaName.error = getString(R.string.area_name_error)
            }
            else if(streetName.isBlank()){
                etStreetName.error = getString(R.string.street_name_error)
            }
            else{
                mainViewModel.request.mutation {
                    it.value?.let {request ->
                        request.latitude = currentLocation.latitude
                        request.longitude = currentLocation.longitude
                        request.numberOfCars = numberOfCars.toInt()
                        request.areaName = areaName
                        request.streetName = streetName
                    }
                }
                requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem+1
            }
        }

        back2.setOnClickListener {
            requireParentFragment().pager.currentItem = requireParentFragment().pager.currentItem-1
        }

    }

    private fun setUpMaps() {
        mapView.onResume()
        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
        mapView.getMapAsync {
            googleMaps = it

            googleMaps.uiSettings.isMyLocationButtonEnabled = true
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@getMapAsync
            }
            googleMaps.isMyLocationEnabled = true
            getLastLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                try{
                    val location = task.result
                    currentLocation = LatLng(location.latitude,location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17f)
                    googleMaps.animateCamera(cameraUpdate)
                    googleMaps.setOnMapClickListener {latLng ->
                        CoroutineScope(Dispatchers.Main).launch {
                            dialog.show()
                            delay(1000)
                            if(marker != null){
                                marker?.remove()
                            }
                            currentLocation = LatLng(latLng.latitude,latLng.longitude)
                            marker = googleMaps.addMarker(MarkerOptions()
                                .position(latLng)
                                .icon(bitmapDescriptorFromVector(requireContext(),R.drawable.sidecrash))
                                .title(getString(R.string.selected_location))
                            )
                            tvLocationInfo.text = getLocationInfo(latLng)
                        }
                    }
                }
                catch (e: java.lang.NullPointerException){
                    e.printStackTrace()
                }

            }
        } else {
            buildAlertMessageNoGps()
        }
    }

    private fun isLocationEnabled(): Boolean {
        if(isAdded){
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.off_gps))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.enable)) { dialog, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
                findNavController().popBackStack()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    //Reference for geo coding api
    //https://developer.android.com/reference/android/location/Address
    private fun getLocationInfo(location:LatLng):String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        if(addresses.isEmpty()){
            dialog.dismiss()
            return getString(R.string.unknow_location)
        }
        val address = addresses[0].getAddressLine(0) ?: getString(R.string.unknow_location)
        val state = addresses[0].adminArea ?: getString(R.string.unknow_location)
        val country = addresses[0].countryName ?:getString(R.string.unknow_location)
        val knownName = addresses[0].featureName ?: getString(R.string.unknow_location)

        dialog.dismiss()
        return "$country - $state:\n$address,$knownName"
    }

    private fun showSnackBar(message:String,color:Int){
        Snackbar.make(requireView(),message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(),color))
            .show()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

}