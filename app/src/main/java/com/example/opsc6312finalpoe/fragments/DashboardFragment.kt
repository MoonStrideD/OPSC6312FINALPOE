package com.example.opsc6312finalpoe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.databinding.FragmentDashboardBinding

open class DashboardFragment : Fragment() {
    protected lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCommonUI()
    }

    protected open fun setupCommonUI() {
        // Null-safe lookup: avoids compile errors if the binding class doesn't include ivLogo.
        val logoView = binding.root.findViewById<ImageView?>(R.id.ivLogo)
        logoView?.let {
            it.visibility = View.VISIBLE
            it.setImageResource(R.drawable.logo)
        }
    }
}
