package com.appleto.loppyuser.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.NonNull
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.OfferListDatum
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList


class OffersListAdapter(
    private val context: Context,
    private var data: ArrayList<OfferListDatum>
) :
    RecyclerView.Adapter<OffersListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_offers_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        Glide
            .with(context)
            .load(data[position].image)
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.ivOfferImage)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        var ivOfferImage: ImageView = itemView.findViewById(R.id.ivOfferImage)
    }
}