package com.appleto.loppyuser.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appleto.loppyuser.R
import com.appleto.loppyuser.adapter.JobsListAdapter
import com.appleto.loppyuser.apiModels.PendingRideDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.PrefUtils
import com.appleto.loppyuser.viewModel.JobViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_ride_history.*

class RideHistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: JobViewModel

    private lateinit var adapter: JobsListAdapter

    private var jobList = ArrayList<PendingRideDatum>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_history)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(this).get(JobViewModel::class.java)

        adapter = JobsListAdapter(this, jobList)
        recyclerViewJobs.adapter = adapter
        recyclerViewJobs.layoutManager = LinearLayoutManager(this)

        Handler().postDelayed({
            PrefUtils.getStringValue(this, Const.USER_ID)?.let {
                viewModel.getPendingRides(
                    this,
                    it
                )
            }
        }, 100)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0?.position == 0) {
                    PrefUtils.getStringValue(this@RideHistoryActivity, Const.USER_ID)?.let {
                        viewModel.getPendingRides(
                            this@RideHistoryActivity,
                            it
                        )
                    }
                } else {
                    PrefUtils.getStringValue(this@RideHistoryActivity, Const.USER_ID)?.let {
                        viewModel.getCompleteRides(
                            this@RideHistoryActivity,
                            it
                        )
                    }
                }
            }
        })

        viewModel.response.observe(this, Observer {
            if (it?.data != null) {
                jobList.clear()
                tvNoRecord.visibility = View.GONE
                recyclerViewJobs.visibility = View.VISIBLE
                jobList.addAll(it.data!!)
                adapter.notifyDataSetChanged()
            } else {
                tvNoRecord.visibility = View.VISIBLE
                recyclerViewJobs.visibility = View.GONE
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
