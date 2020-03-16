package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.models.LoadDetailsModel
import com.appleto.loppyuser.viewModel.SenderInfoViewModel
import kotlinx.android.synthetic.main.activity_receiver_information.*
import kotlin.collections.HashMap

class ReceiverInformationActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v) {
            btnSubmit -> {
                if (!Utils.isEmptyEditText(edtName, "Please enter receiver name", cardName)
                    && !Utils.isEmptyEditText(
                        edtCompanyName,
                        "Please enter company name",
                        cardCompanyName
                    )
                    && !Utils.isEmptyEditText(edtAddress, "Please enter address", cardAddress)
                ) {
                    addRideRequest()
                }
            }
        }
    }

    private var selectedTruck: TruckListDatum? = null
    private var duration: Long? = 0
    private var amount: String? = ""
    private var paidBy: String? = ""
    private var viewModel: SenderInfoViewModel? = null
    private var loadData: LoadDetailsModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver_information)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(this).get(SenderInfoViewModel::class.java)

        amount = intent?.extras?.getString(Const.AMOUNT)
        paidBy = intent?.extras?.getString(Const.PAID_BY)
        duration = intent?.extras?.getLong(Const.DURATION)
        selectedTruck = intent?.extras?.getParcelable(Const.TRUCK_DATA)
        loadData = intent?.extras?.getSerializable(Const.LOAD_INFO) as LoadDetailsModel

        tvAmount.text = "Rs.$amount"

        viewModel?.response?.observe(this, Observer {
            startActivity(Intent(this, OrderSuccessActivity::class.java))
        })


        btnSubmit.setOnClickListener(this)
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
        /*val pickUpDate = PrefUtils.getStringValue(this, Const.SELECTED_DATE)?.let {
            Utils.formatStringDate(
                "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd",
                it
            )
        }*/
        /*val pickUpTime = PrefUtils.getStringValue(this, Const.SELECTED_TIME)?.let {
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
        sendData["paid_by"] = paidBy.toString()*/
        sendData["amount"] = amount.toString()
//        sendData["receiver_name"] = loadData?.name.toString()
//        sendData["receiver_mobile_no"] = loadData?.mobile.toString()
//        sendData["load_type"] = loadData?.loadType.toString()
        sendData["good_type"] = loadData?.goodType.toString()
        sendData["length"] = loadData?.length.toString()
//        sendData["width"] = loadData?.width.toString()
        sendData["height"] = loadData?.height.toString()
        sendData["weight"] = loadData?.weight.toString()
//        sendData["quantity"] = loadData?.quantity.toString()
//        sendData["no_of_handlers"] = loadData?.no_of_handlers.toString()
        sendData["additional_info"] = loadData?.additionalInfo.toString()
        sendData["description"] = loadData?.additionalInfo.toString()
        sendData["company_name"] = edtCompanyName.text.toString().trim()
        sendData["receiver_name"] = edtName.text.toString().trim()
        sendData["address"] = edtAddress.text.toString().trim()
        sendData["load_type"] = PrefUtils.getStringValue(this, Const.LOAD_TYPE_PREF).toString()

        viewModel?.addRideRequest(this, sendData)

    }
}
