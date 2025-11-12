package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opsc6312finalpoe.databinding.FragmentPropertyListBinding
import com.example.opsc6312finalpoe.repository.PropertyRepository
import com.example.opsc6312finalpoe.repository.PropertyViewModel

class PropertyListFragment : Fragment() {
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var propertyViewModel: PropertyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val propertyRepository = PropertyRepository(requireContext())
        propertyViewModel = PropertyViewModel(propertyRepository)

        setupRecyclerView()
        setupObservers()
        loadProperties()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewProperties.layoutManager = LinearLayoutManager(requireContext())
        // Set adapter here later
    }

    private fun setupObservers() {
        propertyViewModel.properties.observe(viewLifecycleOwner) { properties ->
            // Update recycler view adapter
            binding.tvEmpty.visibility = if (properties.isEmpty()) View.VISIBLE else View.GONE
        }

        propertyViewModel.loadingState.observe(viewLifecycleOwner) { state ->
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

    private fun loadProperties() {
        propertyViewModel.loadProperties()
    }
}