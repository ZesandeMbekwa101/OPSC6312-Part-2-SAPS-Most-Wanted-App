package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sapsmostwantedapp.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.sapsmostwantedapp.LocaleHelper
import com.example.sapsmostwantedapp.ui.adapters.WantedAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.WantedViewModel
import com.example.sapsmostwantedapp.ui.viewmodel.WantedPersonNotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MostWantedFragment : Fragment(R.layout.fragment_most_wanted) {

    private val vm: WantedViewModel by viewModels()
    private val notificationViewModel: WantedPersonNotificationViewModel by viewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var adapter: WantedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        LocaleHelper.loadLocale(requireActivity())

        recycler = view.findViewById(R.id.recyclerViewWanted)
        progressBar = view.findViewById(R.id.progressBar)
        swipe = view.findViewById(R.id.swipeRefresh)

        // Get username from parent activity
        val username = (activity as? com.example.sapsmostwantedapp.HomeActivity)?.let {
            it.intent.getStringExtra("username")
        }
        
        adapter = WantedAdapter(
            username = username,
            onEmergencyClick = { personId, personName ->
                showEmergencyDialog(personId, personName, username)
            }
        )
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        swipe.setOnRefreshListener {
            vm.loadWanted()
        }

        vm.wanted.observe(viewLifecycleOwner) { list ->
            adapter.setItems(list)
            swipe.isRefreshing = false
        }

        vm.loading.observe(viewLifecycleOwner) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            if (!loading) swipe.isRefreshing = false
        }

        vm.error.observe(viewLifecycleOwner) { err ->
            err?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe notification submit result
        notificationViewModel.submitResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is WantedPersonNotificationViewModel.SubmitResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is WantedPersonNotificationViewModel.SubmitResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // initial load
        vm.loadWanted()
    }

    private fun showEmergencyDialog(personId: String, personName: String, username: String?) {
        if (username == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_wanted_person_notification, null)
        val editTextLocation = view.findViewById<EditText>(R.id.editTextLocation)
        val editTextNotes = view.findViewById<EditText>(R.id.editTextNotes)

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("🚨 EMERGENCY ALERT 🚨")
            .setMessage("This is an EMERGENCY alert. Please provide location and details immediately!")
            .setView(view)
            .setPositiveButton("SEND EMERGENCY") { _, _ ->
                val location = editTextLocation.text.toString().trim()
                val notes = editTextNotes.text.toString().trim()
                
                if (location.isEmpty()) {
                    Toast.makeText(requireContext(), "Please provide location for emergency alert!", Toast.LENGTH_LONG).show()
                    return@setPositiveButton
                }
                
                notificationViewModel.submitNotification(
                    wantedPersonId = personId,
                    wantedPersonName = personName,
                    reportedBy = username,
                    status = "Emergency",
                    location = location,
                    notes = if (notes.isNotEmpty()) notes else "EMERGENCY ALERT - Immediate attention required!"
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}