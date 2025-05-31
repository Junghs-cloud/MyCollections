package com.example.mycollections

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding

class CurrentMonthCollectionAdapter(
    val context: CurrentMonthCollectionFragment,
    private var data: List<CollectionData>,
    private val listener: OnViewClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        CurrentMonthViewHolder(
            CurrentMonthCollectionRecyclerBinding.inflate
                (LayoutInflater.from(parent.context), parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CurrentMonthViewHolder).binding
        val filePath = data[position].filePath
        val documentID = data[position].documentID

        binding.collectionNameTextView.text = data[position].name
        binding.categoryTextView.text = "[" + data[position].collectionCategory + "]"
        GlideUtilityFragment.setImageToImageView(
            context,
            binding.collectionImage,
            filePath,
            documentID
        )

        holder.itemView.setOnClickListener {
            listener.onItemClickListener(it, position)
        }

        holder.itemView.setOnLongClickListener {
            listener.onItemLongClickListener(it, position)
            true
        }
    }

    fun updateData(newCollectionData: List<CollectionData>) {
        data = newCollectionData
        notifyDataSetChanged()
    }
}