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
import com.example.sapsmostwantedapp.data.model.User
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val onDeleteClick: (String) -> Unit,
    private val onMakeAdminClick: (String) -> Unit,
    private val onRemoveAdminClick: (String) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUsername: TextView = itemView.findViewById(R.id.textViewUsername)
        private val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewEmail)
        private val textViewAdminBadge: TextView = itemView.findViewById(R.id.textViewAdminBadge)
        private val buttonMakeAdmin: Button = itemView.findViewById(R.id.buttonMakeAdmin)
        private val buttonRemoveAdmin: Button = itemView.findViewById(R.id.buttonRemoveAdmin)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

        fun bind(user: User) {
            textViewUsername.text = user.username
            textViewName.text = "${user.firstName} ${user.lastName}"
            textViewEmail.text = user.email

            if (user.isAdmin) {
                textViewAdminBadge.visibility = View.VISIBLE
                buttonMakeAdmin.visibility = View.GONE
                buttonRemoveAdmin.visibility = View.VISIBLE
            } else {
                textViewAdminBadge.visibility = View.GONE
                buttonMakeAdmin.visibility = View.VISIBLE
                buttonRemoveAdmin.visibility = View.GONE
            }

            buttonMakeAdmin.setOnClickListener {
                onMakeAdminClick(user.username)
            }

            buttonRemoveAdmin.setOnClickListener {
                onRemoveAdminClick(user.username)
            }

            buttonDelete.setOnClickListener {
                onDeleteClick(user.username)
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}






