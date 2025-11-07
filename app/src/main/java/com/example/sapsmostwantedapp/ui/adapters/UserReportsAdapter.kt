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
import com.example.sapsmostwantedapp.ui.viewmodel.UserWithReports

class UserReportsAdapter(
    private val onViewReportsClick: (String) -> Unit,
    private val onViewUserClick: (com.example.sapsmostwantedapp.data.model.User) -> Unit
) : ListAdapter<UserWithReports, UserReportsAdapter.UserReportsViewHolder>(UserReportsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReportsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_reports, parent, false)
        return UserReportsViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserReportsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserReportsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
        private val textViewTotalReports: TextView = itemView.findViewById(R.id.textViewTotalReports)
        private val textViewPendingCount: TextView = itemView.findViewById(R.id.textViewPendingCount)
        private val textViewReviewedCount: TextView = itemView.findViewById(R.id.textViewReviewedCount)
        private val textViewResolvedCount: TextView = itemView.findViewById(R.id.textViewResolvedCount)
        private val textViewAdminBadge: TextView = itemView.findViewById(R.id.textViewAdminBadge)
        private val buttonViewReports: Button = itemView.findViewById(R.id.buttonViewReports)
        private val buttonViewUser: Button = itemView.findViewById(R.id.buttonViewUser)

        fun bind(userWithReports: UserWithReports) {
            val user = userWithReports.user
            textViewUsername.text = user.username
            textViewName.text = "${user.firstName} ${user.lastName}"
            textViewEmail.text = user.email
            textViewTotalReports.text = "Total Reports: ${userWithReports.totalCount}"
            textViewPendingCount.text = "Pending: ${userWithReports.pendingCount}"
            textViewReviewedCount.text = "Reviewed: ${userWithReports.reviewedCount}"
            textViewResolvedCount.text = "Resolved: ${userWithReports.resolvedCount}"

            // Show admin badge if user is admin
            if (user.isAdmin) {
                textViewAdminBadge.visibility = View.VISIBLE
            } else {
                textViewAdminBadge.visibility = View.GONE
            }

            // Highlight pending reports with notification badge
            if (userWithReports.pendingCount > 0) {
                textViewPendingCount.setBackgroundColor(
                    itemView.context.getColor(android.R.color.holo_orange_light)
                )
                textViewPendingCount.setTextColor(
                    itemView.context.getColor(android.R.color.white)
                )
            } else {
                textViewPendingCount.setBackgroundColor(
                    itemView.context.getColor(android.R.color.transparent)
                )
                textViewPendingCount.setTextColor(
                    itemView.context.getColor(android.R.color.darker_gray)
                )
            }

            buttonViewReports.setOnClickListener {
                onViewReportsClick(user.username)
            }

            buttonViewUser.setOnClickListener {
                onViewUserClick(user)
            }
        }
    }

    class UserReportsDiffCallback : DiffUtil.ItemCallback<UserWithReports>() {
        override fun areItemsTheSame(oldItem: UserWithReports, newItem: UserWithReports): Boolean {
            return oldItem.user.username == newItem.user.username
        }

        override fun areContentsTheSame(oldItem: UserWithReports, newItem: UserWithReports): Boolean {
            return oldItem == newItem
        }
    }
}

