package com.example.opsc6312finalpoe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.models.Message

class MessageAdapter(
    private var messages: List<Message> = emptyList(),
    private val onMessageClick: (Message) -> Unit = {}
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val messageContent: TextView = itemView.findViewById(R.id.tvMessageContent)
        val timestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val unreadDot: View = itemView.findViewById(R.id.viewUnreadDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        holder.senderName.text = message.senderName
        holder.messageContent.text = message.content
        holder.timestamp.text = message.getFormattedTime()

        // Show unread dot for unread messages
        if (message.read) {
            holder.unreadDot.visibility = View.GONE
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.transparent))
        } else {
            holder.unreadDot.visibility = View.VISIBLE
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_green))
        }

        holder.itemView.setOnClickListener {
            onMessageClick(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        this.messages = newMessages.sortedByDescending { it.timestamp }
        notifyDataSetChanged()
    }
}