package com.example.mycollections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.databinding.AlbumRecyclerBinding
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding
import com.example.mycollections.databinding.FragmentAlbumBinding
import com.example.mycollections.databinding.FragmentCurrentMonthCollectionBinding

class CurrentMonthViewHolder(val binding: CurrentMonthCollectionRecyclerBinding): RecyclerView.ViewHolder(binding.root)

class CurrentMonthCollectionAdapter(val datas: MutableList<CurrentMonthCollection>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CurrentMonthViewHolder(CurrentMonthCollectionRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CurrentMonthViewHolder).binding
        binding.collectionNameTextView.text = datas[position].name
        binding.categoryTextView.text = "["+datas[position].category+"]"
        binding.collectionImage.setImageBitmap(datas[position].image)
    }
}

class CurrentMonthCollectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentCurrentMonthCollectionBinding.inflate(layoutInflater, container, false)

        val currentMonthCollections = mutableListOf<CurrentMonthCollection>()
        binding.currentMonthRecycler.adapter = CurrentMonthCollectionAdapter(currentMonthCollections)
        return binding.root
    }

    private fun makeCurrentMonthCollection(image: Int, category: String, name: String): CurrentMonthCollection
    {
        val originalBitmap: Bitmap = BitmapFactory.decodeResource(resources, image)
        val squareBitmap = Utility.cropToSquare(originalBitmap)
        return CurrentMonthCollection(squareBitmap, category, name)
    }
}

class CurrentMonthCollection(val image: Bitmap, val category: String, val name: String)