package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Criteria
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*


class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedPOI: PointOfInterest? = null
    private lateinit var selectedlatLng: LatLng
    private lateinit var locationStr: String

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("SelectLocationFragment", "Called")
            enableMyLocation()
        } else {
            Log.d("SelectLocationFragment", "NotCalled")
            _viewModel.showSnackBarInt.value = R.string.permission_denied_explanation
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        binding.locationSaveButton.setOnClickListener { onLocationSelected() }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        _viewModel.showToast.value = getString(R.string.select_poi)
        return binding.root
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLong ->
            map.clear()
            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                latLong.latitude,
                latLong.longitude
            )
            val customLocation = map.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
            )
            customLocation?.let {
                customLocation.showInfoWindow()
                selectedPosition(null,
                    latLong,
                    customLocation.snippet ?: getString(R.string.dropped_pin))
            }
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            map.clear()
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.let {
                poiMarker.showInfoWindow()
                selectedPosition(poi, null, poi.name)
            }
        }
    }

    private fun selectedPosition(poi: PointOfInterest?, latLng: LatLng?, locationName: String) {
        if (poi != null) {
            selectedPOI = poi
            selectedlatLng = poi.latLng
        }
        if (latLng != null) {
            selectedlatLng = latLng
        }
        locationStr = locationName
        binding.locationSaveButton.visibility = View.VISIBLE
    }

    private fun onLocationSelected() {
        binding.viewModel?.saveLocationInformation(locationStr,
            selectedlatLng.longitude,
            selectedlatLng.latitude,
            selectedPOI)

        binding.viewModel!!.navigationCommand.value = NavigationCommand.Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setPoiClick(map)
        setMapLongClick(map)
        setMapStyle(map)
        enableMyLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initMyLocation() {
        map.isMyLocationEnabled = true

        val locationManager = getSystemService(requireContext(), LocationManager::class.java) ?: return
        val zoomLevel = 18f
        val criteria = Criteria()
        val location = locationManager.getLastKnownLocation(locationManager
            .getBestProvider(criteria, false)!!) ?: return
        val latitude = location.latitude
        val longitude = location.longitude
        val currentLocation = LatLng(latitude, longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel))
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            initMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    companion object {
        private const val TAG = "SelectLocationFragment"
    }
}
