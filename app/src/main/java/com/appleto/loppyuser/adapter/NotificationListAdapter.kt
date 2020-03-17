package com.appleto.loppyuser.adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import com.appleto.loppyuser.activity.MapsActivity
import com.appleto.loppyuser.R
import com.appleto.loppyuser.apiModels.NotificationListModelDatum
import com.appleto.loppyuser.apiModels.PendingRideDatum
import com.appleto.loppyuser.helper.Const
import com.appleto.loppyuser.helper.Utils
import com.bumptech.glide.Glide
import kotlin.collections.ArrayList


class NotificationListAdapter(
    private val context: Context,
    private var data: ArrayList<NotificationListModelDatum>,
    val itemClick: (NotificationListModelDatum) -> Unit
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_notification, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.tvTitle.text = data[position].title
        holder.tvDesc.text = data[position].message
        if (data[position].status == "pending") {
            holder.btnConfirm.visibility = View.VISIBLE
        } else {
            holder.btnConfirm.visibility = View.GONE
        }
        holder.btnConfirm.setOnClickListener {
            itemClick(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        var btnConfirm: Button = itemView.findViewById(R.id.btnConfirm)
    }
}