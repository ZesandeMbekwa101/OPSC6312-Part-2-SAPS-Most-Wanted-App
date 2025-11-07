package com.example.sapsmostwantedapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.WantedPersonNotification
import java.text.SimpleDateFormat
import java.util.*

class WantedPersonNotificationAdapter(
    private val onVerifyClick: (Long) -> Unit,
    private val onDeleteClick: (Long) -> Unit,
    private val onViewDetailsClick: (WantedPersonNotification) -> Unit
) : ListAdapter<WantedPersonNotification, WantedPersonNotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wanted_person_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPersonName: TextView = itemView.findViewById(R.id.textViewPersonName)
        private val textViewStatus: TextView = itemView.findViewById(R.id.textViewStatus)
        private val textViewReportedBy: TextView = itemView.findViewById(R.id.textViewReportedBy)
        private val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewVerified: TextView = itemView.findViewById(R.id.textViewVerified)
        private val buttonVerify: Button = itemView.findViewById(R.id.buttonVerify)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
        private val buttonViewDetails: Button = itemView.findViewById(R.id.buttonViewDetails)

        fun bind(notification: WantedPersonNotification) {
            textViewPersonName.text = notification.wantedPersonName
            textViewStatus.text = notification.status
            textViewReportedBy.text = "Reported by: ${notification.reportedBy}"
            
            if (notification.location != null && notification.location.isNotEmpty()) {
                textViewLocation.visibility = View.VISIBLE
                textViewLocation.text = "Location: ${notification.location}"
            } else {
                textViewLocation.visibility = View.GONE
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            textViewDate.text = "Date: ${dateFormat.format(Date(notification.createdAt))}"

            // Set status color
            when (notification.status) {
                "Emergency" -> {
                    textViewStatus.text = "🚨 EMERGENCY 🚨"
                    textViewStatus.setBackgroundColor(
                        itemView.context.getColor(android.R.color.holo_red_dark)
                    )
                    textViewStatus.setTextColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                    // Highlight emergency notifications
                    itemView.setBackgroundColor(
                        itemView.context.getColor(android.R.color.holo_red_light)
                    )
                    itemView.alpha = 1.0f
                }
                "Found" -> {
                    textViewStatus.setBackgroundColor(
                        itemView.context.getColor(android.R.color.holo_green_light)
                    )
                    textViewStatus.setTextColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                    itemView.setBackgroundColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                }
                "Not Found" -> {
                    textViewStatus.setBackgroundColor(
                        itemView.context.getColor(android.R.color.holo_red_light)
                    )
                    textViewStatus.setTextColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                    itemView.setBackgroundColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                }
                else -> {
                    itemView.setBackgroundColor(
                        itemView.context.getColor(android.R.color.white)
                    )
                }
            }

            // Show verified badge if verified
            if (notification.isVerified) {
                textViewVerified.visibility = View.VISIBLE
                buttonVerify.visibility = View.GONE
            } else {
                textViewVerified.visibility = View.GONE
                buttonVerify.visibility = View.VISIBLE
            }

            buttonVerify.setOnClickListener {
                onVerifyClick(notification.id)
            }

            buttonDelete.setOnClickListener {
                onDeleteClick(notification.id)
            }

            buttonViewDetails.setOnClickListener {
                onViewDetailsClick(notification)
            }
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<WantedPersonNotification>() {
        override fun areItemsTheSame(oldItem: WantedPersonNotification, newItem: WantedPersonNotification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WantedPersonNotification, newItem: WantedPersonNotification): Boolean {
            return oldItem == newItem
        }
    }
}

