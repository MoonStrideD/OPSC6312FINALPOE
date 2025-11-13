package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opsc6312finalpoe.databinding.FragmentNotificationsBinding
import com.example.opsc6312finalpoe.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var notificationRepository: NotificationRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationRepository = NotificationRepository()
        setupRecyclerView()
        loadNotifications()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())
        // Set adapter here
    }

    private fun loadNotifications() {
        CoroutineScope(Dispatchers.Main).launch {
            val userId = "current_user_id" // Get from auth
            val notifications = notificationRepository.getNotifications(userId)
            // Update adapter with notifications
            binding.tvEmpty.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}