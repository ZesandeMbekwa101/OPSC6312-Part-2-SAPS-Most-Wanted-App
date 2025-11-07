package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.ui.adapters.ActivityLogAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.ActivityLogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ActivityLogsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonAll: Button
    private lateinit var spinnerAction: Spinner
    private lateinit var buttonClearFilters: Button
    private val activityLogViewModel: ActivityLogViewModel by viewModels()
    private var activityLogAdapter: ActivityLogAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            inflater.inflate(R.layout.fragment_activity_logs, container, false)
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogsFragment", "Error inflating layout: ${e.message}", e)
            // Return a simple empty view if layout fails
            android.view.View(requireContext())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            recyclerView = view.findViewById(R.id.recyclerViewActivityLogs)
            progressBar = view.findViewById(R.id.progressBarActivityLogs)
            buttonAll = view.findViewById(R.id.buttonAllLogs)
            spinnerAction = view.findViewById(R.id.spinnerActionFilter)
            buttonClearFilters = view.findViewById(R.id.buttonClearFilters)

            // Setup action filter spinner
            val actions = listOf("All Actions", "LOGIN", "LOGOUT", "REGISTER", "SUBMIT_REPORT", 
                "UPDATE_REPORT", "DELETE_REPORT", "DELETE_USER", "UPDATE_USER", "NAVIGATION")
            val adapter = android.widget.ArrayAdapter(requireContext(), 
                android.R.layout.simple_spinner_dropdown_item, actions)
            spinnerAction.adapter = adapter

            activityLogAdapter = ActivityLogAdapter()
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = activityLogAdapter

            // Filter buttons
            buttonAll.setOnClickListener {
                try {
                    activityLogViewModel.loadAllActivityLogs()
                    activityLogViewModel.clearFilters()
                } catch (e: Exception) {
                    android.util.Log.e("ActivityLogsFragment", "Error loading logs: ${e.message}", e)
                }
            }

            spinnerAction.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    try {
                        val selectedAction = actions[position]
                        if (selectedAction != "All Actions") {
                            activityLogViewModel.filterByAction(selectedAction)
                        } else {
                            activityLogViewModel.clearFilters()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ActivityLogsFragment", "Error filtering: ${e.message}", e)
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }

            buttonClearFilters.setOnClickListener {
                try {
                    activityLogViewModel.clearFilters()
                    spinnerAction.setSelection(0)
                } catch (e: Exception) {
                    android.util.Log.e("ActivityLogsFragment", "Error clearing filters: ${e.message}", e)
                }
            }

            // Observe activity logs
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        activityLogViewModel.filteredLogs.collect { logs ->
                            try {
                                activityLogAdapter?.submitList(logs)
                            } catch (e: Exception) {
                                android.util.Log.e("ActivityLogsFragment", "Error submitting list: ${e.message}", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ActivityLogsFragment", "Error observing logs: ${e.message}", e)
                }
            }

            // Observe loading state
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        activityLogViewModel.isLoading.collect { isLoading ->
                            try {
                                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                            } catch (e: Exception) {
                                android.util.Log.e("ActivityLogsFragment", "Error updating loading state: ${e.message}", e)
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ActivityLogsFragment", "Error observing loading: ${e.message}", e)
                }
            }

            // Load logs after observers are set up
            try {
                activityLogViewModel.loadAllActivityLogs()
            } catch (e: Exception) {
                android.util.Log.e("ActivityLogsFragment", "Error loading logs initially: ${e.message}", e)
            }
        } catch (e: Exception) {
            android.util.Log.e("ActivityLogsFragment", "Error in onViewCreated: ${e.message}", e)
        }
    }
}

