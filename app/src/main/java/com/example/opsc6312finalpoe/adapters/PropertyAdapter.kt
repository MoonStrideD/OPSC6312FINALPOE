package com.example.opsc6312finalpoe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.models.Property

class PropertyAdapter(
    private var properties: List<Property> = emptyList(),
    private val onItemClick: (Property) -> Unit = {}
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]
        holder.bind(property)
        holder.itemView.setOnClickListener { onItemClick(property) }
    }

    override fun getItemCount(): Int = properties.size

    fun updateProperties(newProperties: List<Property>) {
        properties = newProperties
        notifyDataSetChanged()
    }

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivProperty: ImageView = itemView.findViewById(R.id.ivProperty)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvBedrooms: TextView = itemView.findViewById(R.id.tvBedrooms)

        fun bind(property: Property) {
            tvTitle.text = property.title
            tvLocation.text = property.location
            tvPrice.text = "R ${property.price}/month"
            tvBedrooms.text = "${property.bedrooms} Bedrooms"

            // Load image with Glide
            if (property.photos.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(property.photos[0])
                    .placeholder(R.drawable.ic_placeholder)
                    .into(ivProperty)
            }
        }
    }
}