package com.example.mycollections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.databinding.AlbumRecyclerBinding
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding
import com.example.mycollections.databinding.FragmentAlbumBinding
import com.example.mycollections.databinding.FragmentCurrentMonthCollectionBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentCurrentMonthCollectionBinding.inflate(layoutInflater, container, false)

        val collection1 = makeCurrentMonthCollection(R.drawable.baseline_image_90, "분류 1","컬렉션 1")
        val collection2 = makeCurrentMonthCollection(R.drawable.baseline_image_90, "분류 2","컬렉션 2")
        val currentMonthCollections = mutableListOf(collection1, collection2)
        binding.currentMonthRecycler.adapter = CurrentMonthCollectionAdapter(currentMonthCollections)
        return binding.root
    }

    private fun makeCurrentMonthCollection(image: Int, category: String, name: String): CurrentMonthCollection
    {
        val originalBitmap: Bitmap = BitmapFactory.decodeResource(resources, image)
        val squareBitmap = ImageUtils.cropToSquare(originalBitmap)
        return CurrentMonthCollection(squareBitmap, category, name)
    }
}

class CurrentMonthCollection(val image: Bitmap, val category: String, val name: String)