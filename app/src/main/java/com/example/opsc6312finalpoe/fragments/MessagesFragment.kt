package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.adapters.MessageAdapter
import com.example.opsc6312finalpoe.models.Message
import com.example.opsc6312finalpoe.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagesFragment : Fragment() {
    private lateinit var messageRepository: MessageRepository
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: View
    private lateinit var emptyState: View

    // Test user ID - replace with actual user ID from authentication
    private val currentUserId = "tenant_123"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout without data binding
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messageRepository = MessageRepository()
        setupViews(view)
        setupRecyclerView()
        loadMessages()
    }

    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewChats)
        progressBar = view.findViewById(R.id.progressBar)
        emptyState = view.findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(
            onMessageClick = { message ->
                showMessageDetails(message)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun loadMessages() {
        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Try to load from Firebase first
                val messages = messageRepository.getChatsForUser(currentUserId)

                if (messages.isNotEmpty()) {
                    showMessages(messages)
                } else {
                    // If no messages in Firebase, show test data
                    showTestMessages()
                }
            } catch (e: Exception) {
                // If Firebase fails, show test data
                showTestMessages()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showTestMessages() {
        val testMessages = createTestMessages()
        showMessages(testMessages)
    }

    private fun createTestMessages(): List<Message> {
        return listOf(
            Message(
                messageId = "1",
                senderId = "landlord_123",
                senderName = "Property Manager",
                receiverId = "all",
                title = "Rent Reminder",
                content = "Your rent for Luxury Apartment is due in 5 days",
                type = "broadcast",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30, // 30 minutes ago
                read = false
            ),
            Message(
                messageId = "2",
                senderId = "landlord_123",
                senderName = "Property Manager",
                receiverId = "all",
                title = "New Property Available",
                content = "A new apartment just listed in Sandton area",
                type = "broadcast",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
                read = true
            ),
            Message(
                messageId = "3",
                senderId = "landlord_123",
                senderName = "Property Manager",
                receiverId = "all",
                title = "Maintenance Notice",
                content = "Annual maintenance scheduled for Friday",
                type = "broadcast",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
                read = true
            ),
            Message(
                messageId = "4",
                senderId = "landlord_123",
                senderName = "Property Manager",
                receiverId = "all",
                title = "Test Notification",
                content = "This is a test message from the notification system",
                type = "broadcast",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5, // 5 minutes ago
                read = false
            )
        )
    }

    private fun showMessages(messages: List<Message>) {
        messageAdapter.updateMessages(messages)

        if (messages.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        }
    }

    private fun showMessageDetails(message: Message) {
        // Mark as read when clicked
        if (!message.read) {
            CoroutineScope(Dispatchers.Main).launch {
                messageRepository.markAsRead(message.messageId)
                // Refresh the list
                loadMessages()
            }
        }

        // Show message details
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(message.title)
            .setMessage("${message.content}\n\nFrom: ${message.senderName}\nTime: ${message.getFormattedDate()}")
            .setPositiveButton("OK", null)
            .create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh messages when fragment becomes visible
        loadMessages()
    }
}