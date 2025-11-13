package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opsc6312finalpoe.databinding.FragmentPropertyListBinding
import com.example.opsc6312finalpoe.repository.PropertyRepository
import com.example.opsc6312finalpoe.repository.PropertyViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PropertyListFragment : Fragment() {
    private lateinit var binding: FragmentPropertyListBinding
    private val propertyViewModel: PropertyViewModel by viewModels {
        PropertyViewModelFactory(PropertyRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        loadProperties()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProperties.layoutManager = LinearLayoutManager(requireContext())
        // Set adapter here later
    }

    private fun setupObservers() {
        // Observe properties using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            propertyViewModel.properties.collect { properties ->
                // Update recycler view adapter
                binding.tvEmpty.visibility = if (properties.isEmpty()) View.VISIBLE else View.GONE
                // TODO: Update adapter with properties
            }
        }

        // Observe loading state using StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            propertyViewModel.loadingState.collect { state ->
                when (state) {
                    is PropertyViewModel.LoadingState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is PropertyViewModel.LoadingState.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is PropertyViewModel.LoadingState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        // Show error message (you can add a Snackbar or Toast here)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadProperties() {
        propertyViewModel.loadProperties()
    }
}

// Add this Factory class
class PropertyViewModelFactory(
    private val propertyRepository: PropertyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyViewModel(propertyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}