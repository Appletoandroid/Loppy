package com.appleto.loppyuser.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.appleto.loppyuser.R
import com.appleto.loppyuser.adapter.OffersListAdapter
import com.appleto.loppyuser.apiModels.OfferListDatum
import com.appleto.loppyuser.viewModel.OffersListViewModel
import kotlinx.android.synthetic.main.activity_offers.*

class OffersActivity : AppCompatActivity() {

    private var viewModel: OffersListViewModel? = null
    private var offersList = ArrayList<OfferListDatum>()
    private var adapter: OffersListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)

        viewModel = ViewModelProvider(this).get(OffersListViewModel::class.java)

        Handler().postDelayed({
            viewModel?.offersDetails(this)
        }, 500)

        adapter = OffersListAdapter(this, offersList)
        recyclerViewOffers.adapter = adapter
        recyclerViewOffers.layoutManager = LinearLayoutManager(this)

        viewModel?.response?.observe(this, Observer {
            if (it?.data != null && it.data?.size!! > 0) {
                offersList.clear()
                offersList.addAll(it.data!!)
                adapter?.notifyDataSetChanged()
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
