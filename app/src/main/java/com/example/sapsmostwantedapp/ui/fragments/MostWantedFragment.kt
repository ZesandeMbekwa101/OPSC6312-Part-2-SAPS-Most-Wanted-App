package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.View
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
import com.example.sapsmostwantedapp.ui.adapters.WantedAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.WantedViewModel

class MostWantedFragment : Fragment(R.layout.fragment_most_wanted) {

    private val vm: WantedViewModel by viewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var adapter: WantedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.recyclerViewWanted)
        progressBar = view.findViewById(R.id.progressBar)
        swipe = view.findViewById(R.id.swipeRefresh)

        adapter = WantedAdapter()
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

        // initial load
        vm.loadWanted()
    }
}