package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_sender_information.*
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.GoodTypeDatum
import com.appleto.loppyuser.apiModels.LoadTypeDatum
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.models.LoadDetailsModel
import com.appleto.loppyuser.viewModel.SenderInfoViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SenderInformationActivity : AppCompatActivity(), View.OnClickListener {

    override fun onClick(p0: View?) {
        when (p0) {
            btnSubmit -> {
                if (!Utils.isEmptyEditText(edtName, "Please enter name", cardName)
                    && !Utils.isEmptyEditText(edtMobile, "Please enter mobile", cardMobile)
                    && !Utils.isEmptyEditText(edtLoadType, "Please select load type", cardLoadType)
                    && !Utils.isEmptyEditText(edtGoodType, "Please select good type", cardGoodType)
                    && !Utils.isEmptyEditText(edtQuantity, "Please enter quantity", cardQuantity)
                    && !Utils.isEmptyEditText(
                        edtHandlers,
                        "Please enter no of handlers",
                        cardNoOfHandlers
                    )
                    && !Utils.isEmptyEditText(edtWeight, "Please enter weight", cardWeight)
                    && !Utils.isEmptyEditText(
                        edtDescription,
                        "Please enter additional description",
                        cardDesc
                    )
                ) {
                    val model = LoadDetailsModel()
                    model.name = edtName.text.toString().trim()
                    model.mobile = edtMobile.text.toString().trim()
                    model.loadType = loadTypeId
                    model.goodType = goodTypeId
                    model.length = ""
                    model.width = ""
                    model.height = ""
                    model.weight = edtWeight.text.toString().trim()
                    model.quantity = edtQuantity.text.toString().trim()
                    model.no_of_handlers = edtHandlers.text.toString().trim()
                    model.additionalInfo = edtDescription.text.toString().trim()

                    addRideRequest(model)
//                    val intent = Intent()
//                    intent.putExtra(Const.LOAD_DATA, model)
//                    setResult(Activity.RESULT_OK, intent)
//                    finish()
                }
            }
            llLoadType -> {
                showLoadTypeChoice()
            }
            edtLoadType -> {
                showLoadTypeChoice()
            }
            cardGoodType -> {
                showGoodTypeChoice()
            }
            edtGoodType -> {
                showGoodTypeChoice()
            }
        }
    }

    private var viewModel: SenderInfoViewModel? = null
    private var loadTypes = ArrayList<String>()
    private var goodTypes = ArrayList<String>()
    private var selectedLoadTypePosition = -1
    private var selectedGoodTypePosition = -1
    private var loadTypesData = ArrayList<LoadTypeDatum>()
    private var goodTypesData = ArrayList<GoodTypeDatum>()
    private var loadTypeId = ""
    private var goodTypeId = ""
    private var selectedTruck: TruckListDatum? = null
    private var duration: Long? = 0
    private var amount: String? = ""
    private var paidBy: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender_information)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(this).get(SenderInfoViewModel::class.java)

        amount = intent?.extras?.getString(Const.AMOUNT)
        paidBy = intent?.extras?.getString(Const.PAID_BY)
        duration = intent?.extras?.getLong(Const.DURATION)
        selectedTruck = intent?.extras?.getParcelable(Const.TRUCK_DATA)

        Handler().postDelayed({
            viewModel?.getLoadType(this)
            viewModel?.getGoodType(this)
        }, 500)
        viewModel?.responseLoadType?.observe(this, Observer {
            if (it?.data?.size!! > 0) {
                loadTypesData.clear()
                loadTypesData.addAll(it.data!!)
                loadTypes.clear()
                for (item in loadTypesData) {
                    item.loadType?.let { loadTypes.add(it) }
                }
            }
        })

        viewModel?.responseGoodType?.observe(this, Observer {
            if (it?.data?.size!! > 0) {
                goodTypesData.clear()
                goodTypesData.addAll(it.data!!)
                goodTypes.clear()
                for (item in goodTypesData) {
                    item.goodType?.let { goodTypes.add(it) }
                }
            }
        })

        viewModel?.response?.observe(this, Observer {
            startActivity(Intent(this, PaymentActivity::class.java))
        })

        btnSubmit.setOnClickListener(this)
        llLoadType.setOnClickListener(this)
        edtLoadType.setOnClickListener(this)
        cardGoodType.setOnClickListener(this)
        edtGoodType.setOnClickListener(this)
    }

    private fun showLoadTypeChoice() {
        AlertDialog.Builder(this)
            .setTitle("Select Load Type")
            .setSingleChoiceItems(loadTypes.toTypedArray(), selectedLoadTypePosition, null)
            .setPositiveButton(
                "OK"
            ) { dialog, whichButton ->
                dialog.dismiss()
                val selectedPosition =
                    (dialog as AlertDialog).listView.checkedItemPosition
                if (selectedPosition >= 0) {
                    selectedLoadTypePosition = selectedPosition
                    // Do something useful withe the position of the selected radio button
                    edtLoadType.setText(loadTypes[selectedPosition])
                    loadTypeId = loadTypesData[selectedPosition].id.toString()
                }
            }
            .setNegativeButton("CANCEL") { dialog, whichButton ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showGoodTypeChoice() {
        AlertDialog.Builder(this)
            .setTitle("Select Good Type")
            .setSingleChoiceItems(goodTypes.toTypedArray(), selectedGoodTypePosition, null)
            .setPositiveButton(
                "OK"
            ) { dialog, whichButton ->
                dialog.dismiss()
                val selectedPosition =
                    (dialog as AlertDialog).listView.checkedItemPosition
                if (selectedPosition >= 0) {
                    selectedGoodTypePosition = selectedPosition
                    // Do something useful withe the position of the selected radio button
                    edtGoodType.setText(goodTypes[selectedPosition])
                    goodTypeId = goodTypesData[selectedPosition].id.toString()
                }
            }
            .setNegativeButton("CANCEL") { dialog, whichButton ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addRideRequest(loadData: LoadDetailsModel?) {
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
        sendData["paid_by"] = paidBy.toString()
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}