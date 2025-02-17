package com.actiangent.wasteye.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.actiangent.wasteye.databinding.ItemWasteBinding
import com.actiangent.wasteye.model.Waste
import com.google.android.material.shape.CornerFamily

class WasteListAdapter(
    private val onWasteClick: (Waste) -> Unit,
) : ListAdapter<Waste, WasteListAdapter.WasteListViewHolder>(WasteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WasteListViewHolder {
        val binding = ItemWasteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WasteListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WasteListViewHolder, position: Int) {
        val waste = getItem(position)
        holder.bind(waste, onWasteClick)
    }

    class WasteListViewHolder(
        private val binding: ItemWasteBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Waste, onWasteClick: (Waste) -> Unit) {
            val context = binding.root.context

            binding.apply {
                textWasteName.text = item.name
                imageWaste.apply {
                    shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                        .setAllCorners(CornerFamily.ROUNDED, 16f)
                        .build()
                    load(item.imageResId) {
                        crossfade(50)
                    }
                }

                itemWasteContainer.setOnClickListener{
                    onWasteClick(item)
                }
            }
        }
    }
}

object WasteDiffCallback : DiffUtil.ItemCallback<Waste>() {
    override fun areItemsTheSame(oldItem: Waste, newItem: Waste): Boolean {
        return oldItem.ordinal == newItem.ordinal
    }

    override fun areContentsTheSame(oldItem: Waste, newItem: Waste): Boolean {
        return oldItem == newItem
    }
}