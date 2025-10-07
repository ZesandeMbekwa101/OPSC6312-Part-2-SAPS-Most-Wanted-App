package com.example.sapsmostwantedapp.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.WantedPerson

class WantedAdapter(
    private val items: MutableList<WantedPerson> = mutableListOf()
) : RecyclerView.Adapter<WantedAdapter.VH>() {

    fun setItems(newItems: List<WantedPerson>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_wanted_person, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.name.text = p.properties?.name?.firstOrNull() ?: p.caption ?: "Unknown"
        holder.crime.text = p.properties?.notes?.joinToString("\n") ?: "No details"

        val url = p.properties?.sourceUrl?.firstOrNull() ?: ""
        holder.link.text = url

        holder.itemView.setOnClickListener {
            if (url.isNotBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val crime: TextView = view.findViewById(R.id.txtCrime)
        val link: TextView = view.findViewById(R.id.txtLink)
    }
}