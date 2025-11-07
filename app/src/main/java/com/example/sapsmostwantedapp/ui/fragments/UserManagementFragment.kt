package com.example.sapsmostwantedapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sapsmostwantedapp.R
import com.example.sapsmostwantedapp.data.model.User
import com.example.sapsmostwantedapp.ui.adapters.UserAdapter
import com.example.sapsmostwantedapp.ui.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserManagementFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val adminViewModel: AdminViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_management, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get admin username from parent activity
        val adminUsername = (activity as? com.example.sapsmostwantedapp.AdminDashboardActivity)?.let {
            it.intent.getStringExtra("admin_username")
        }
        adminUsername?.let { adminViewModel.setAdminUsername(it) }

        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        progressBar = view.findViewById(R.id.progressBarUsers)

        userAdapter = UserAdapter(
            onDeleteClick = { username ->
                adminViewModel.deleteUser(username)
            },
            onMakeAdminClick = { username ->
                adminViewModel.makeUserAdmin(username)
            },
            onRemoveAdminClick = { username ->
                adminViewModel.removeAdmin(username)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = userAdapter

        // Observe users using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.users.collect { users ->
                    userAdapter.submitList(users)
                }
            }
        }

        // Observe loading state
        adminViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe operation results
        adminViewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AdminViewModel.OperationResult.Success -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is AdminViewModel.OperationResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


