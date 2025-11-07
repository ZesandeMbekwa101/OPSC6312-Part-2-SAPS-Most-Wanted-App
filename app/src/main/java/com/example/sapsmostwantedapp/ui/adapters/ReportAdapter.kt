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
import com.example.sapsmostwantedapp.data.model.Report
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(
    private val onDeleteClick: (Long) -> Unit,
    private val onViewDetailsClick: (Report) -> Unit,
    private val onUpdateStatusClick: (Long, String, String?, String) -> Unit
) : ListAdapter<Report, ReportAdapter.ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewReportTitle)
        private val textViewStatus: TextView = itemView.findViewById(R.id.textViewStatus)
        private val textViewType: TextView = itemView.findViewById(R.id.textViewReportType)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)
        private val textViewSubmittedBy: TextView = itemView.findViewById(R.id.textViewSubmittedBy)
        private val textViewLocation: TextView = itemView.findViewById(R.id.textViewLocation)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val buttonViewDetails: Button = itemView.findViewById(R.id.buttonViewDetails)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(report: Report) {
            textViewTitle.text = report.title
            textViewStatus.text = report.status
            textViewType.text = "Type: ${report.reportType}"
            textViewDescription.text = report.description
            textViewSubmittedBy.text = "Submitted by: ${report.submittedBy}"
            
            if (report.location != null && report.location.isNotEmpty()) {
                textViewLocation.visibility = View.VISIBLE
                textViewLocation.text = "Location: ${report.location}"
            } else {
                textViewLocation.visibility = View.GONE
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            textViewDate.text = "Date: ${dateFormat.format(Date(report.createdAt))}"

            // Set status color
            when (report.status) {
                "Pending" -> textViewStatus.setBackgroundColor(
                    itemView.context.getColor(android.R.color.holo_orange_light)
                )
                "Reviewed" -> textViewStatus.setBackgroundColor(
                    itemView.context.getColor(android.R.color.holo_blue_light)
                )
                "Resolved" -> textViewStatus.setBackgroundColor(
                    itemView.context.getColor(android.R.color.holo_green_light)
                )
            }

            buttonViewDetails.setOnClickListener {
                onViewDetailsClick(report)
            }

            buttonDelete.setOnClickListener {
                onDeleteClick(report.id)
            }
        }
    }

    class ReportDiffCallback : DiffUtil.ItemCallback<Report>() {
        override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
            return oldItem == newItem
        }
    }
}






