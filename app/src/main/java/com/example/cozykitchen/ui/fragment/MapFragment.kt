package com.example.cozykitchen.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.cozykitchen.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

interface OnMarkerDragListener {
    fun onMarkerDragEnd(locationString: String, latitude: Double, longitude: Double)
}

class MapFragment : Fragment(), OnMapReadyCallback{

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var marker: Marker? = null
    private var addresses: List<Address>? = null

    private var markerDragListener: OnMarkerDragListener? = null

    private var targetLatLng: LatLng? = null

    fun setOnMarkerDragListener(listener: OnMarkerDragListener) {
        markerDragListener = listener
    }

    fun setTargetLocation(latitude: Double, longitude: Double) {
        targetLatLng = LatLng(latitude, longitude)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Enable the location layer on the map
            googleMap.isMyLocationEnabled = true

            // Get the last known location
//            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location ->
//                    if (location != null) {
//                        // Add a marker at the current location
//                        val currentLatLng = LatLng(location.latitude, location.longitude)
//                        val physicalLocationString = getPhysicalLocationString(currentLatLng)
//
//                        // check if there is Address or not
//                        if (!physicalLocationString.isNullOrEmpty()) {
//                            markerDragListener?.onMarkerDragEnd(physicalLocationString, currentLatLng.latitude, currentLatLng.longitude)
//                        } else {
//                            Toast.makeText(requireContext(), "No Address Found", Toast.LENGTH_SHORT).show()
//                        }
//
//                        // add market to map
//                        marker = googleMap.addMarker(
//                            MarkerOptions()
//                                .position(currentLatLng)
//                                .title("Your Current Location")
//                                .snippet("Hold and Drag to change location")
//                                .draggable(true)
//                        )
//
//                        // Zoom in to the current location
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
//
//                        // Set up the marker drag listener
//                        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
//                            override fun onMarkerDragStart(marker: Marker) {
//                                // called when marker is held
//                            }
//
//                            override fun onMarkerDrag(marker: Marker) {
//                                // Called when the marker is being dragged
//                            }
//
//                            override fun onMarkerDragEnd(marker: Marker) {
//                                // Called when the marker drag ends
//                                val newPosition = marker.position
//                                val physicalLocationString = getPhysicalLocationString(newPosition)
//
//                                // check if there is Address or not
//                                if (!physicalLocationString.isNullOrEmpty()) {
//                                    markerDragListener?.onMarkerDragEnd(physicalLocationString, newPosition.latitude, newPosition.longitude)
//                                } else {
//                                    Toast.makeText(requireContext(), "No Address Found", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                        })
//                    }
//                }

            if (targetLatLng != null) {
                // Add a marker at the target location
                val targetLocation = targetLatLng!!
                val physicalLocationString = getPhysicalLocationString(targetLocation)

                // check if there is Address or not
                if (!physicalLocationString.isNullOrEmpty()) {
                    markerDragListener?.onMarkerDragEnd(physicalLocationString, targetLocation.latitude, targetLocation.longitude)
                } else {
                    Toast.makeText(requireContext(), "No Address Found", Toast.LENGTH_SHORT).show()
                }

                // Add marker to the map
                marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(targetLocation)
                        .title("Selected Location")
                        .snippet("Hold and Drag to change location")
                        .draggable(true)
                )

                // Zoom in to the target location
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 18f))
            } else {
                // Get the last known location
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            // Add a marker at the current location
                            val currentLatLng = LatLng(location.latitude, location.longitude)
                            val physicalLocationString = getPhysicalLocationString(currentLatLng)

                            // check if there is Address or not
                            if (!physicalLocationString.isNullOrEmpty()) {
                                markerDragListener?.onMarkerDragEnd(physicalLocationString, currentLatLng.latitude, currentLatLng.longitude)
                            } else {
                                Toast.makeText(requireContext(), "No Address Found", Toast.LENGTH_SHORT).show()
                            }

                            // Add marker to the map
                            marker = googleMap.addMarker(
                                MarkerOptions()
                                    .position(currentLatLng)
                                    .title("Your Current Location")
                                    .snippet("Hold and Drag to change location")
                                    .draggable(true)
                            )

                            // Zoom in to the current location
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                        }
                    }
            }

            // Set up the marker drag listener
            googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDragStart(marker: Marker) {
                    // Called when marker is held
                }

                override fun onMarkerDrag(marker: Marker) {
                    // Called when the marker is being dragged
                }

                override fun onMarkerDragEnd(marker: Marker) {
                    // Called when the marker drag ends
                    val newPosition = marker.position
                    val physicalLocationString = getPhysicalLocationString(newPosition)

                    // check if there is Address or not
                    if (!physicalLocationString.isNullOrEmpty()) {
                        markerDragListener?.onMarkerDragEnd(physicalLocationString, newPosition.latitude, newPosition.longitude)
                    } else {
                        Toast.makeText(requireContext(), "No Address Found", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    // get location string based on latitude and longitude
    private fun getPhysicalLocationString(location: LatLng): String? {
        var fullAddress: String? = null
        // enable geocoder
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        // get the physical address from latitude and longitude of current location
        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        // if addresses is not empty, get the physical address string
        if (addresses?.isNotEmpty() == true) {
            val address = addresses?.get(0)
            fullAddress = address?.getAddressLine(0)
//            Toast.makeText(requireContext(), "$fullAddress", Toast.LENGTH_SHORT).show()
        }

        return fullAddress
    }
}