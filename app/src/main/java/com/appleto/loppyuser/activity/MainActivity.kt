package com.appleto.loppyuser.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import com.appleto.loppyuser.R
import com.appleto.loppyuser.helper.*
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_default.*
import com.google.android.libraries.places.api.model.Place
import java.util.*
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.android.synthetic.main.nav_header_main.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import com.karumi.dexter.listener.single.PermissionListener as PermissionListener


class MainActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0) {
            ibMenu -> {
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                } else {
                    drawer_layout.openDrawer(GravityCompat.START)
                }
            }
            btnNext -> {
                if (!Utils.isEmptyEditText(
                        edtPickUpLocation,
                        "Please select pickup location",
                        cardPickUpLocation
                    )
                    && !Utils.isEmptyEditText(
                        edtDestinationLocation,
                        "Please select destination location",
                        cardDestinationLocation
                    )
                    && !Utils.isEmptyEditText(edtDate, "Please select date", cardDate)
                    && !Utils.isEmptyEditText(edtTime, "Please select time", cardTime)
                ) {
                    goToNext()
                }
            }
            llPickUpLocation -> {
                fromIntent = 1
                openPlacesIntent()
            }
            edtPickUpLocation -> {
                fromIntent = 1
                openPlacesIntent()
            }
            llDestinationLocation -> {
                fromIntent = 2
                openPlacesIntent()
            }
            edtDestinationLocation -> {
                fromIntent = 2
                openPlacesIntent()
            }
            llDate -> {
                openDatePicker()
            }
            edtDate -> {
                openDatePicker()
            }
            llTime -> {
                openTimePicker()
            }
            edtTime -> {
                openTimePicker()
            }
            nav_view.getHeaderView(0).llOffers -> {
                startActivity(Intent(this, OffersActivity::class.java))
            }
            nav_view.getHeaderView(0).llHeader -> {
                startActivity(
                    Intent(this, RegisterActivity::class.java).putExtra(
                        Const.FROM,
                        Const.PROFILE
                    )
                )
            }
            nav_view.getHeaderView(0).llProfile -> {
                startActivity(
                    Intent(this, RegisterActivity::class.java).putExtra(
                        Const.FROM,
                        Const.PROFILE
                    )
                )
            }
            nav_view.getHeaderView(0).llHistory -> {
                startActivity(Intent(this, RideHistoryActivity::class.java))
            }
            nav_view.getHeaderView(0).llLogout -> {
                logOut()
            }
        }
    }

    private lateinit var mMap: GoogleMap
    var AUTOCOMPLETE_REQUEST_CODE = 1
    var fromIntent = 1
    var pickUpLatLng: LatLng? = null
    var destinationLatLng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    var mPickUpLocationMarker: Marker? = null
    var mDestinationLocationMarker: Marker? = null
    var selectedDate: Calendar? = null
    var selectedTime: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    createLocationRequest()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {

                }

            }).check()

        drawer_layout.setViewScale(Gravity.START, 0.9f)
        drawer_layout.setViewElevation(Gravity.START, 20F)

        drawer_layout.useCustomBehavior(Gravity.END)

        nav_view.getHeaderView(0).tvName.text =
            if (PrefUtils.getStringValue(
                    this,
                    Const.NAME
                )?.isEmpty()!!
            ) "John Doe" else PrefUtils.getStringValue(this, Const.NAME)
        nav_view.getHeaderView(0).tvPhone.text = if (PrefUtils.getStringValue(
                this,
                Const.MOBILE_NO
            )?.isEmpty()!!
        ) "1234567890" else PrefUtils.getStringValue(this, Const.MOBILE_NO)
        Glide.with(this)
            .load(PrefUtils.getStringValue(this, Const.PROFILE_IMAGE))
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .into(nav_view.getHeaderView(0).ivProfileImage)

        ibMenu.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        llPickUpLocation.setOnClickListener(this)
        llDestinationLocation.setOnClickListener(this)
        llDate.setOnClickListener(this)
        llTime.setOnClickListener(this)
        edtPickUpLocation.setOnClickListener(this)
        edtDestinationLocation.setOnClickListener(this)
        edtDate.setOnClickListener(this)
        edtTime.setOnClickListener(this)
        nav_view.getHeaderView(0).llOffers.setOnClickListener(this)
        nav_view.getHeaderView(0).llHeader.setOnClickListener(this)
        nav_view.getHeaderView(0).llProfile.setOnClickListener(this)
        nav_view.getHeaderView(0).llLogout.setOnClickListener(this)
        nav_view.getHeaderView(0).llHistory.setOnClickListener(this)
    }

    private fun logOut() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                PrefUtils.clear(this)
                startActivity(
                    Intent(
                        this,
                        LoginActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun goToNext() {
        PrefUtils.storeStringValue(
            this,
            Const.PICK_UP_LOCATION,
            edtPickUpLocation.text.toString().trim()
        )
        pickUpLatLng?.latitude?.let {
            PrefUtils.storeDoubleValue(
                this,
                Const.PICKUP_LAT,
                it
            )
        }
        pickUpLatLng?.longitude?.let {
            PrefUtils.storeDoubleValue(
                this,
                Const.PICKUP_LNG,
                it
            )
        }
        PrefUtils.storeStringValue(
            this,
            Const.DESTINATION_LOCATION,
            edtDestinationLocation.text.toString().trim()
        )
        destinationLatLng?.latitude?.let {
            PrefUtils.storeDoubleValue(
                this,
                Const.DESTINATION_LAT,
                it
            )
        }
        destinationLatLng?.longitude?.let {
            PrefUtils.storeDoubleValue(
                this,
                Const.DESTINATION_LNG,
                it
            )
        }
        PrefUtils.storeStringValue(
            this,
            Const.SELECTED_DATE,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                selectedDate?.time
            )
        )
        PrefUtils.storeStringValue(
            this,
            Const.SELECTED_TIME,
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                selectedTime?.time
            )
        )
        startActivity(Intent(this, VehicleListActivity::class.java))
    }

    private fun openDatePicker() {
        object :
            CustomDatePicker(this, Calendar.getInstance(), null, Calendar.getInstance()) {
            override fun onDateChange(calender: Calendar) {
                selectedDate = calender
                edtDate.setText(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        calender.time
                    )
                )
            }
        }
    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                .addLocationRequest(it)
        }

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder?.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        101
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun openTimePicker() {
        object :
            CustomTimePicker(this, Calendar.getInstance()) {
            override fun onTimeChange(calendar: Calendar) {
                selectedTime = calendar
                edtTime.setText(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                        calendar.time
                    )
                )
            }
        }
    }

    private fun openPlacesIntent() {
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .build(this)
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                if (fromIntent == 1) {
                    pickUpLatLng = place.latLng
                    pickUpLatLng?.latitude?.let {
                        pickUpLatLng?.longitude?.let { it1 ->
                            setPickUpMarker(
                                it,
                                it1
                            )
                        }
                    }
                    edtPickUpLocation.setText(place.name + ", " + place.address)
                } else {
                    destinationLatLng = place.latLng
                    destinationLatLng?.latitude?.let {
                        destinationLatLng?.longitude?.let { it1 ->
                            setDestinationMarker(
                                it,
                                it1
                            )
                        }
                    }
                    edtDestinationLocation.setText(place.name + ", " + place.address)
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i("Error Found", status.statusMessage)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 101) {
            createLocationRequest()
        }
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

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    if (location != null) {
                        setPickUpMarker(location.latitude, location.longitude)
                        val address = getAddressFromLatLng(location.latitude, location.longitude)
                        edtPickUpLocation.setText(address)
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        pickUpLatLng = LatLng(
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            }
        }

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragEnd(p0: Marker?) {
                if (p0?.title == "Current Location") {
                    val address = p0.position?.latitude?.let {
                        p0.position?.longitude?.let { it1 ->
                            getAddressFromLatLng(
                                it,
                                it1
                            )
                        }
                    }
                    pickUpLatLng = p0.position
                    edtPickUpLocation.setText(address)
                } else {
                    val address = p0?.position?.latitude?.let {
                        p0.position?.longitude?.let { it1 ->
                            getAddressFromLatLng(
                                it,
                                it1
                            )
                        }
                    }
                    destinationLatLng = p0?.position
                    edtDestinationLocation.setText(address)
                }
            }

            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }
        })

        mMap.setOnMapClickListener {
            setDestinationMarker(it.latitude, it.longitude)
            destinationLatLng = LatLng(
                it.latitude,
                it.longitude
            )
            val address = getAddressFromLatLng(it.latitude, it.longitude)
            edtDestinationLocation.setText(address)
        }

        /* // Add a marker in Sydney and move the camera
         val sydney = LatLng(-34.0, 151.0)
         mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
         mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    private fun setDestinationMarker(latitude: Double, longitude: Double) {
        if (mDestinationLocationMarker != null) {
            mDestinationLocationMarker?.remove()
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
        mDestinationLocationMarker = mMap.addMarker(markerOptions)
        //move map camera
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 17F
            )
        )
    }

    private fun setPickUpMarker(latitude: Double, longitude: Double) {
        if (mPickUpLocationMarker != null) {
            mPickUpLocationMarker?.remove()
        }

        val markerOptions = MarkerOptions()
        markerOptions.draggable(true)
        markerOptions.position(LatLng(latitude, longitude))
        markerOptions.title("Current Location")
        markerOptions.icon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_GREEN
            )
        )
        mPickUpLocationMarker = mMap.addMarker(markerOptions)

        //move map camera
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    latitude,
                    longitude
                ), 17F
            )
        )
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var errorMessage = ""
        var addressString = ""
        var addresses: List<Address> = emptyList()

        try {
            runOnUiThread {
                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    // In this sample, we get just a single address.
                    1
                )
            }
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "Service not available"
            Log.e(MainActivity::class.java.simpleName, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid lattitude and longitude"
            Log.e(
                MainActivity::class.java.simpleName, "$errorMessage. Latitude = $latitude , " +
                        "Longitude =  $longitude", illegalArgumentException
            )
        }
        // Handle case where no address was found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found"
                Log.e(MainActivity::class.java.simpleName, errorMessage)
            }
//            addressString = errorMessage
//            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }
            Log.i(MainActivity::class.java.simpleName, "Address found")
            addressString = addressFragments.joinToString(separator = "\n")
//            deliverResultToReceiver(Constants.SUCCESS_RESULT,
//                addressFragments.joinToString(separator = "\n"))
        }
        return addressString
    }

    override fun onStop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        createLocationRequest()
    }
}
