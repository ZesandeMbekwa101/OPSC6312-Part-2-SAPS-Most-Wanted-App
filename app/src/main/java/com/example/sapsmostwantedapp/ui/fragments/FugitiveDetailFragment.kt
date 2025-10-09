package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.WantedPerson

class FugitiveDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fugitive_detail, container, false)

        val person = arguments?.getSerializable("person") as? WantedPerson ?: return view

        val imageView = view.findViewById<ImageView>(R.id.ivFugitive)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvCrime = view.findViewById<TextView>(R.id.tvCrime)
        val tvCaseNumber = view.findViewById<TextView>(R.id.tvCaseNumber)
        val tvGender = view.findViewById<TextView>(R.id.tvGender)
        val tvHairColor = view.findViewById<TextView>(R.id.tvHairColor)
        val tvStation = view.findViewById<TextView>(R.id.tvStation)
        val tvEmail = view.findViewById<TextView>(R.id.tvEmail)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)

        // Set data dynamically from model
        tvName.text = person.properties?.name?.firstOrNull() ?: person.caption ?: "Unknown"
        tvCrime.text = "Crime: ${person.properties?.topics?.joinToString() ?: "Unknown"}"
        tvCaseNumber.text = "Notes: ${person.properties?.notes?.joinToString() ?: "None"}"
        tvGender.text = "Gender: ${person.properties?.gender?.firstOrNull() ?: "Unknown"}"
        tvHairColor.text = "Hair Colour: ${person.properties?.hairColor ?: "Unknown"}"
        tvStation.text = "Country: ${person.properties?.country?.firstOrNull() ?: "Unknown"}"
        tvEmail.text = "Source: ${person.properties?.sourceUrl?.firstOrNull() ?: "N/A"}"
        tvPhone.text = "ID: ${person.id ?: "Unknown"}"

        // Optional image loading if available
        Glide.with(this)
            .load("https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png")
            .placeholder(R.drawable.ic_person)
            .into(imageView)

        return view
    }

    companion object {
        fun newInstance(person: WantedPerson): FugitiveDetailFragment {
            val fragment = FugitiveDetailFragment()
            val args = Bundle()
            args.putSerializable("person", person)
            fragment.arguments = args
            return fragment
        }
    }
}
