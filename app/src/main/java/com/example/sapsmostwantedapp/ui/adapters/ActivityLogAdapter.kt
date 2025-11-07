package com.example.sapsmostwantedapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.ActivityLog
import java.text.SimpleDateFormat
import java.util.*

class ActivityLogAdapter : ListAdapter<ActivityLog, ActivityLogAdapter.ActivityLogViewHolder>(ActivityLogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_log, parent, false)
        return ActivityLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivityLogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ActivityLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewAction: TextView = itemView.findViewById(R.id.textViewAction)
        private val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)
        private val textViewDetails: TextView = itemView.findViewById(R.id.textViewDetails)

        fun bind(activityLog: ActivityLog) {
            textViewAction.text = activityLog.action
            textViewUsername.text = "User: ${activityLog.username}"
            textViewDescription.text = activityLog.description
            
            // Format timestamp
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(activityLog.timestamp)
            textViewTimestamp.text = dateFormat.format(date)
            
            // Show details if available
            if (activityLog.details != null && activityLog.details.isNotEmpty()) {
                textViewDetails.text = "Details: ${activityLog.details}"
                textViewDetails.visibility = View.VISIBLE
            } else {
                textViewDetails.visibility = View.GONE
            }
        }
    }

    class ActivityLogDiffCallback : DiffUtil.ItemCallback<ActivityLog>() {
        override fun areItemsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ActivityLog, newItem: ActivityLog): Boolean {
            return oldItem == newItem
        }
    }
}




