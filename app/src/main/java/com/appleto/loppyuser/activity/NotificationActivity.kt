package com.appleto.loppyuser.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appleto.loppyuser.R
import com.appleto.loppyuser.adapter.NotificationListAdapter
import com.appleto.loppyuser.apiModels.NotificationListModelDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.viewModel.NotificationViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_notification.*
import org.json.JSONObject

class NotificationActivity : AppCompatActivity(), PaymentResultListener {
    override fun onPaymentError(errorCode: Int, response: String?) {
        try {
            Toast.makeText(this, "Payment failed $errorCode \n $response", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            Toast.makeText(this, "Payment Successful $razorpayPaymentId", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onPaymentSuccess", e)
        }
    }

    private var viewModel: NotificationViewModel? = null
    private var adapter: NotificationListAdapter? = null
    private var notificationList = ArrayList<NotificationListModelDatum>()
    val TAG: String = NotificationActivity::class.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        /*
       * To ensure faster loading of the Checkout form,
       * call this method as early as possible in your checkout flow
       * */
        Checkout.preload(applicationContext)

        adapter = NotificationListAdapter(this, notificationList) {
            startPayment(it)
        }
        recyclerViewNotification.adapter = adapter
        recyclerViewNotification.layoutManager = LinearLayoutManager(this)

        Handler().postDelayed({
            PrefUtils.getStringValue(this, Const.USER_ID)
                ?.let { viewModel?.getNotifications(this, it) }
        }, 500)

        viewModel?.response?.observe(this, Observer {
            if (it?.data != null) {
                notificationList.clear()
                notificationList.addAll(it.data!!)
                adapter?.notifyDataSetChanged()
            }
        })


    }

    private fun startPayment(data: NotificationListModelDatum) {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name", "Loppy")
            options.put("description", "Ride Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            val amount = data.amount?.replace(".", "")
            options.put("amount", amount)

            val prefill = JSONObject()
//            prefill.put("email", "test@razorpay.com")
            prefill.put("contact", data.mobileNo)

            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
