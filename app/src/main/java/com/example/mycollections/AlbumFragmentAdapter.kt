package com.example.mycollections

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.databinding.AlbumRecyclerBinding

class AlbumViewHolder(val binding: AlbumRecyclerBinding): RecyclerView.ViewHolder(binding.root)

class AlbumFragmentAdapter(val context: AlbumFragment, var data: List<CollectionData>, val listener: OnViewClickListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var showName = false
    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AlbumViewHolder(
        AlbumRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AlbumViewHolder).binding

        displayNameOption(binding)
        holder.itemView.setOnClickListener{
            listener.onItemClickListener(it, position)
        }
        binding.nameTextView.text = data[position].name
        val filePath = data[position].filePath
        val documentID = data[position].documentID
        GlideUtilityFragment.setImageToImageView(context, binding.collectionImage, filePath, documentID)
    }

    private fun displayNameOption(binding: AlbumRecyclerBinding)
    {
        if (showName)
        {
            binding.nameTextView.visibility = View.VISIBLE
        }
        else
        {
            binding.nameTextView.visibility = View.GONE
        }
    }

    fun update(newData: List<CollectionData>)
    {
        data = newData
        notifyDataSetChanged()
    }

    fun setNewData(newData: List<CollectionData>)
    {
        data = newData
    }

    fun updateNameOption(option: Boolean)
    {
        showName = option
        notifyDataSetChanged()
    }
}