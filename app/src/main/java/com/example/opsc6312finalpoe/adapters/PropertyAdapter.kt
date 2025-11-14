package com.example.opsc6312finalpoe.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.opsc6312finalpoe.R
import com.example.opsc6312finalpoe.models.Property

class PropertyAdapter(
    private var properties: List<Property> = emptyList(),
    private val onItemClick: (Property) -> Unit = {},
    private val onFavoriteClick: (Property, Boolean) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder>() {

    private val favorites = mutableSetOf<String>()

    class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: View = itemView.findViewById(R.id.cardProperty)
        val imageView: ImageView = itemView.findViewById(R.id.ivProperty)
        val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        val locationTextView: TextView = itemView.findViewById(R.id.tvLocation)
        val priceTextView: TextView = itemView.findViewById(R.id.tvPrice)
        val typeTextView: TextView = itemView.findViewById(R.id.tvPropertyType)
        val bedroomsTextView: TextView = itemView.findViewById(R.id.tvBedrooms)
        val bathroomsTextView: TextView = itemView.findViewById(R.id.tvBathrooms)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.btnFavorite)
        val statusTextView: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = properties[position]

        // Set basic property info
        holder.titleTextView.text = property.title
        holder.locationTextView.text = property.location
        holder.priceTextView.text = property.getFormattedPrice() + "/month"
        holder.typeTextView.text = property.propertyType
        holder.bedroomsTextView.text = property.bedrooms.toString()
        holder.bathroomsTextView.text = property.bathrooms.toString()

        // Set favorite state
        val isFavorite = favorites.contains(property.propertyId)
        val favoriteIcon = if (isFavorite) {
            R.drawable.ic_favorite_filled
        } else {
            R.drawable.ic_favorite_border
        }
        holder.favoriteButton.setImageResource(favoriteIcon)

        // Load image - using placeholder for now
        Glide.with(holder.itemView.context)
            .load(property.getMainPhoto())
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)

        // Set status
        if (property.status == "available") {
            holder.statusTextView.visibility = View.VISIBLE
            holder.statusTextView.text = "Available"
        } else {
            holder.statusTextView.visibility = View.GONE
        }

        // Set click listeners
        holder.cardView.setOnClickListener {
            onItemClick(property)
        }

        holder.favoriteButton.setOnClickListener {
            val newFavoriteState = !favorites.contains(property.propertyId)
            if (newFavoriteState) {
                favorites.add(property.propertyId)
            } else {
                favorites.remove(property.propertyId)
            }
            notifyItemChanged(position)
            onFavoriteClick(property, newFavoriteState)
        }
    }

    override fun getItemCount(): Int = properties.size

    fun updateProperties(newProperties: List<Property>) {
        this.properties = newProperties
        notifyDataSetChanged()
    }
}