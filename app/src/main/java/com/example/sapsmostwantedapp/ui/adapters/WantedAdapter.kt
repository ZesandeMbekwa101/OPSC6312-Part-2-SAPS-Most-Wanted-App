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
        holder.gender.text = p.properties?.gender?.joinToString("\n") ?: "No details"

        holder.itemView.setOnClickListener {
            val fragment = com.example.sapsmostwantedapp.ui.fragments.FugitiveDetailFragment.newInstance(p)
            val activity = it.context as androidx.appcompat.app.AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_Layout, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.txtName)
        val gender: TextView = view.findViewById(R.id.txtGender)
    }
}