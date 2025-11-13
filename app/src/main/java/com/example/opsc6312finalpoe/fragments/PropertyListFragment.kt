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
import com.example.opsc6312finalpoe.adapters.PropertyAdapter
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
    private lateinit var propertyAdapter: PropertyAdapter

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
        setupSearch()
        loadProperties()
    }

    private fun setupRecyclerView() {
        propertyAdapter = PropertyAdapter(emptyList()) { property ->
            // Handle property click - navigate to property details
            // (activity as? MainActivity)?.replaceFragment(PropertyDetailsFragment.newInstance(property))
        }

        binding.recyclerViewProperties.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = propertyAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            propertyViewModel.properties.collect { properties ->
                propertyAdapter.updateProperties(properties)
                binding.tvEmpty.visibility = if (properties.isEmpty()) View.VISIBLE else View.GONE
            }
        }

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
                        // Show error message
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length >= 2) {
                        propertyViewModel.searchProperties(location = it)
                    } else if (it.isEmpty()) {
                        loadProperties()
                    }
                }
                return true
            }
        })
    }

    private fun loadProperties() {
        propertyViewModel.loadProperties()
    }
}

// Factory class remains the same
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