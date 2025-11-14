package com.example.opsc6312finalpoe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.models.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private var notifications: List<Notification> = emptyList(),
    private val onNotificationClick: (Notification) -> Unit = {}
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        val message: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        val time: TextView = itemView.findViewById(R.id.tvNotificationTime)
        val dot: View = itemView.findViewById(R.id.viewUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.title.text = notification.title
        holder.message.text = notification.message
        holder.time.text = formatTime(notification.timestamp)

        // Show unread dot for unread notifications
        if (notification.read) {
            holder.dot.visibility = View.GONE
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
        } else {
            holder.dot.visibility = View.VISIBLE
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
        }

        holder.itemView.setOnClickListener {
            onNotificationClick(notification)
        }
    }

    override fun getItemCount(): Int = notifications.size

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun updateNotifications(newNotifications: List<Notification>) {
        this.notifications = newNotifications
        notifyDataSetChanged()
    }
}