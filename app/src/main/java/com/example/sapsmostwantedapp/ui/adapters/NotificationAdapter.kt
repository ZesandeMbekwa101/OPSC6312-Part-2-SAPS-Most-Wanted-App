package com.example.sapsmostwantedapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R

class NotificationAdapter(
    private var notifications: List<String>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val icon: ImageView = itemView.findViewById(R.id.imgNotificationIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = notifications[position]
        // Optionally set icons based on notification type
    }

    override fun getItemCount() = notifications.size

    fun updateData(newNotifications: List<String>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
