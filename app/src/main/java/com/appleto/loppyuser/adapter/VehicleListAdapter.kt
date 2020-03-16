package com.appleto.loppyuser.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.TruckListDatum
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList


class VehicleListAdapter(
    private val context: Context,
    private var data: ArrayList<TruckListDatum>,
    val listener: (TruckListDatum) -> Unit
) :
    RecyclerView.Adapter<VehicleListAdapter.ViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_vehicle_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        if (selectedPosition == position) {
            holder.frame.background = context.resources.getDrawable(R.drawable.selected_truck_bg)
        } else {
            holder.frame.background = context.resources.getDrawable(R.drawable.truck_bg)
        }
        Glide
            .with(context)
            .load(data[position].image)
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.ivTruckImage)
        holder.tvTruckName.text = data[position].name
        holder.tvTruckCapacity.text = data[position].capacity

        holder.llRoot.setOnClickListener {
            selectedPosition = position
            listener(data[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var llRoot: LinearLayout = itemView.findViewById(R.id.llRoot)
        var ivTruckImage: ImageView = itemView.findViewById(R.id.ivTruckImage)
        var tvTruckName: TextView = itemView.findViewById(R.id.tvTruckName)
        var tvTruckCapacity: TextView = itemView.findViewById(R.id.tvTruckCapacity)
        var frame: FrameLayout = itemView.findViewById(R.id.frame)
    }
}