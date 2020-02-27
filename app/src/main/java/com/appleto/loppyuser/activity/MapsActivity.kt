package com.appleto.loppyuser.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.PendingRideDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.viewModel.MapsActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mDriverMarker: Marker? = null
    var mEndPointMarker: Marker? = null
    private var data: PendingRideDatum? = null
    private var viewModel: MapsActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        viewModel = ViewModelProvider(this).get(MapsActivityViewModel::class.java)

        data = intent?.extras?.getSerializable(Const.JOB_DATA) as PendingRideDatum

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun setDriverMarker(latitude: Double, longitude: Double) {
        if (mDriverMarker != null) {
            mDriverMarker?.remove()
        }

        val markerOptions = MarkerOptions()
        markerOptions.draggable(true)
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("Driver Location")
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
        mDriverMarker = mMap.addMarker(markerOptions)

    }

    private fun setEndPointMarker(latitude: Double, longitude: Double) {
        if (mEndPointMarker != null) {
            mEndPointMarker?.remove()
        }

        val markerOptions = MarkerOptions()
        markerOptions.draggable(true)
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("Destination Location")
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_ORANGE
            )
        )
        mEndPointMarker = mMap.addMarker(markerOptions)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (data != null) {
            viewModel?.driverId = data?.acceptedDriverId.toString()
            viewModel?.getDriverLocation()

            viewModel?.response?.observe(this, Observer {
                if (it.has("data")) {
                    setDriverMarker(
                        it.get("data").asJsonObject.get("latitude").asString.toDouble(),
                        it.get("data").asJsonObject.get("longitude").asString.toDouble()
                    )
                    getDirectionRoute()
                }
            })
        }
    }

    private fun getDirectionRoute() {
        val path = ArrayList<LatLng>()

        if (mDriverMarker != null) {
            val context = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build()

            val req = DirectionsApi.getDirections(
                context,
                "${mDriverMarker?.position?.latitude},${mDriverMarker?.position?.longitude}",
                "${data?.destinationLat},${data?.destinationLong}"
            )

            try {
                val res = req.await()

                if (res.routes != null && res.routes.isNotEmpty()) {
                    val route = res.routes[0]

                    if (route.legs != null) {
                        for (leg in route.legs) {
                            if (leg.steps != null) {
                                for (step in leg.steps) {
                                    if (step.steps != null && step.steps.isNotEmpty()) {
                                        for (step1 in step.steps) {
                                            val point1 = step1.polyline
                                            if (point1 != null) {
                                                val coords1 = point1.decodePath()
                                                for (coord1 in coords1) {
                                                    path.add(LatLng(coord1.lat, coord1.lng))
                                                }
                                            }
                                        }
                                    } else {
                                        val points = step.polyline
                                        if (points != null) {
                                            val coords = points.decodePath()
                                            for (coord in coords) {
                                                path.add(LatLng(coord.lat, coord.lng))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Draw poly line
            if (path.size > 0) {
                val opts = PolylineOptions().addAll(path).color(Color.BLACK).width(16F)
                mMap.addPolyline(opts)
            }

            mMap.uiSettings.isZoomControlsEnabled = true

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDriverMarker?.position?.latitude?.let {
                mDriverMarker?.position?.longitude?.let { it1 ->
                    LatLng(
                        it, it1
                    )
                }
            }, 17F))

            data?.destinationLat?.toDouble()?.let {
                data?.destinationLong?.toDouble()?.let { it1 ->
                    setEndPointMarker(
                        it,
                        it1
                    )
                }
            }
        }
    }
}
