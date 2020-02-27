package com.appleto.loppyuser.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.models.LoadDetailsModel
import com.appleto.loppyuser.viewModel.AddRideRequestViewModel
import com.bumptech.glide.Glide
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.TravelMode
import kotlinx.android.synthetic.main.activity_vehicle_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import android.widget.RadioButton
import com.appleto.loppyuser.R


class VehicleDetailActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0) {
            btnAddReceiver -> {
                startActivityForResult(Intent(this, SenderInformationActivity::class.java), 101)
            }
            btnNext -> {
                val selectedId = rgPaidBy.checkedRadioButtonId

                // find the radiobutton by returned id
                val radioButton = findViewById<View>(selectedId) as RadioButton
                var paidBy = "sender"
                if (radioButton.text == "Sender") {
                    paidBy = "sender"
                } else {
                    paidBy = "receiver"
                }
                startActivity(
                    Intent(this, SenderInformationActivity::class.java)
                        .putExtra(Const.AMOUNT, amount)
                        .putExtra(Const.TRUCK_DATA, selectedTruck)
                        .putExtra(Const.DURATION, duration)
                        .putExtra(Const.PAID_BY, paidBy)
                )
//                addRideRequest()
            }
        }
    }

    private var selectedTruck: TruckListDatum? = null
    private var amount: String? = ""
    private var loadData: LoadDetailsModel? = null
    private var viewModel: AddRideRequestViewModel? = null
    private var duration: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        selectedTruck = intent?.extras?.getParcelable(Const.TRUCK_DATA)

        viewModel = ViewModelProvider(this).get(AddRideRequestViewModel::class.java)

        if (selectedTruck != null) {
            Glide
                .with(this)
                .load(selectedTruck?.image)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(ivTruckImage)
            tvTruckName.text = selectedTruck?.name
            tvTruckCapacity.text = "Capacity : " + selectedTruck?.capacity
            tvPickUpLocation.text = PrefUtils.getStringValue(this, Const.PICK_UP_LOCATION)
            tvDestinationLocation.text = PrefUtils.getStringValue(this, Const.DESTINATION_LOCATION)
            Handler().postDelayed({
                getDurationForRoute()
            }, 500)
        }

        viewModel?.response?.observe(this, Observer {
            startActivity(Intent(this, PaymentActivity::class.java))
        })

//        rgPaidBy.setOnCheckedChangeListener { group, checkedId ->
        //            if (checkedId == R.id.rbReceiver) {
//                btnAddReceiver.visibility = View.VISIBLE
//            } else {
//                btnAddReceiver.visibility = View.GONE
//            }
//        }

//        btnAddReceiver.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    private fun addRideRequest() {
        val sendData = HashMap<String, String>()
        PrefUtils.getStringValue(this, Const.USER_ID)?.let { sendData.put("user_id", it) }
        PrefUtils.getStringValue(this, Const.PICK_UP_LOCATION)?.let {
            sendData.put(
                "source_address",
                it
            )
        }
        PrefUtils.getStringValue(this, Const.DESTINATION_LOCATION)?.let {
            sendData.put(
                "destination_address",
                it
            )
        }
        sendData["source_lat"] = PrefUtils.getDoubleValue(this, Const.PICKUP_LAT).toString()
        sendData["source_long"] = PrefUtils.getDoubleValue(this, Const.PICKUP_LNG).toString()
        sendData["destination_lat"] =
            PrefUtils.getDoubleValue(this, Const.DESTINATION_LAT).toString()
        sendData["destination_long"] =
            PrefUtils.getDoubleValue(this, Const.DESTINATION_LNG).toString()
        selectedTruck?.truckId?.let { sendData.put("truck_id", it) }
        val pickUpDate = PrefUtils.getStringValue(this, Const.SELECTED_DATE)?.let {
            Utils.formatStringDate(
                "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
                it
            )
        }
        val pickUpTime = PrefUtils.getStringValue(this, Const.SELECTED_TIME)?.let {
            Utils.formatStringDate(
                "yyyy-MM-dd HH:mm:ss", "HH:mm:ss",
                it
            )
        }
        val pickUpDateTime = "$pickUpDate $pickUpTime"

        val dropTime = System.currentTimeMillis() + duration!!
        val dropDateTime =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dropTime)

        sendData["pickup_date_time"] = pickUpDateTime
        sendData["drop_date_time"] = dropDateTime
        sendData["paid_by"] = "receiver"
        sendData["amount"] = amount.toString()
        sendData["receiver_name"] = loadData?.name.toString()
        sendData["receiver_mobile_no"] = loadData?.mobile.toString()
        sendData["load_type"] = loadData?.loadType.toString()
        sendData["good_type"] = loadData?.goodType.toString()
        sendData["length"] = loadData?.length.toString()
        sendData["width"] = loadData?.width.toString()
        sendData["height"] = loadData?.height.toString()
        sendData["weight"] = loadData?.weight.toString()
        sendData["quantity"] = loadData?.quantity.toString()
        sendData["no_of_handlers"] = loadData?.no_of_handlers.toString()
        sendData["additional_info"] = loadData?.additionalInfo.toString()

        viewModel?.addRideRequest(this, sendData)

    }

    private fun getDurationForRoute() {
        try {
            // access api
            val geoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.google_maps_key))
                .build()

            // request api
            val directionResult = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(PrefUtils.getStringValue(this, Const.PICK_UP_LOCATION))
                .destination(PrefUtils.getStringValue(this, Const.DESTINATION_LOCATION))
                .await()

            // parse api
            val route = directionResult.routes[0]
            val leg = route.legs[0]
            duration = leg.duration.inSeconds
            tvTime.text = leg.duration.humanReadable
            tvDistance.text = leg.distance.humanReadable
            val distanceKm = leg.distance.humanReadable.replace(" km", "").toDouble()
            val getAmount = distanceKm * 100
            amount = "%.2f".format(getAmount)
            tvEstimatedAmount.text = "RS.$amount"
            tvAmount.text = "RS.$amount"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                loadData = data?.extras?.getSerializable(Const.LOAD_DATA) as LoadDetailsModel
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
