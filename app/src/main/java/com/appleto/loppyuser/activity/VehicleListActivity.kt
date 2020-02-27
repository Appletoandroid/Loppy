package com.appleto.loppyuser.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appleto.loppyuser.R
import com.appleto.loppyuser.adapter.VehicleListAdapter
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.Utils
import com.appleto.loppyuser.viewModel.TruckListViewModel
import kotlinx.android.synthetic.main.activity_vehicle_list.*
import kotlinx.android.synthetic.main.activity_vehicle_list.btnNext
import kotlinx.android.synthetic.main.activity_vehicle_list.toolbar

class VehicleListActivity : AppCompatActivity(), View.OnClickListener, (TruckListDatum) -> Unit {

    override fun invoke(data: TruckListDatum) {
        selectedTruck = data
    }

    override fun onClick(p0: View?) {
        when (p0) {
            btnNext -> {
                if (selectedTruck == null) {
                    Utils.showSnackBar(root, "Please select vehicle")
                } else {
                    startActivity(
                        Intent(this, VehicleDetailActivity::class.java)
                            .putExtra(Const.TRUCK_DATA, selectedTruck)
                    )
                }
            }
        }
    }

    private lateinit var adapter: VehicleListAdapter
    private var vehicleList = ArrayList<TruckListDatum>()
    private var viewModel: TruckListViewModel? = null
    private var selectedTruck: TruckListDatum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(
            this
        ).get(TruckListViewModel::class.java)

        adapter = VehicleListAdapter(this, vehicleList, this)
        recyclerViewVehicles.adapter = adapter
        recyclerViewVehicles.layoutManager = LinearLayoutManager(this)
        btnNext.setOnClickListener(this)

        viewModel?.truckDetails(this)

        viewModel?.response?.observe(this, Observer {
            if (it?.data != null) {
                vehicleList.clear()
                vehicleList.addAll(it.data!!)
                adapter.notifyDataSetChanged()
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
