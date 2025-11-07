package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.ui.adapters.NotificationAdapter

class NotificationsFragment : Fragment() {

    private lateinit var recyclerViewNotifications: RecyclerView
    private lateinit var progressBarNotifications: ProgressBar
    private lateinit var swipeRefreshNotifications: SwipeRefreshLayout
    private lateinit var adapter: NotificationAdapter
    private val notificationList = mutableListOf<String>()
    private var handler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_final, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())

        // Initialize Views
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications)
        progressBarNotifications = view.findViewById(R.id.progressBarNotifications)
        swipeRefreshNotifications = view.findViewById(R.id.swipeRefreshNotifications)

        // Initialize RecyclerView with adapter
        adapter = NotificationAdapter(notificationList)
        recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewNotifications.adapter = adapter

        // Load notifications initially
        loadNotifications()

        // Swipe-to-refresh logic
        swipeRefreshNotifications.setOnRefreshListener {
            refreshNotifications()
        }
    }

    private fun loadNotifications() {
        if (!::progressBarNotifications.isInitialized || !::adapter.isInitialized) {
            return
        }

        progressBarNotifications.visibility = View.VISIBLE
        handler?.postDelayed({
            if (view != null && isAdded) {
                notificationList.clear()
                notificationList.addAll(
                    listOf(
                        "New message from Admin",
                        "Profile update reminder",
                        "Security alert: New login detected"
                    )
                )
                adapter.notifyDataSetChanged()
                progressBarNotifications.visibility = View.GONE
            }
        }, 1500)
    }

    private fun refreshNotifications() {
        if (!::swipeRefreshNotifications.isInitialized || !::adapter.isInitialized) {
            return
        }

        handler?.postDelayed({
            if (view != null && isAdded) {
                notificationList.clear()
                notificationList.addAll(
                    listOf(
                        "New fugitive added to the list!",
                        "System maintenance in 30 minutes"
                    )
                )
                adapter.notifyDataSetChanged()
                swipeRefreshNotifications.isRefreshing = false
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }
}
