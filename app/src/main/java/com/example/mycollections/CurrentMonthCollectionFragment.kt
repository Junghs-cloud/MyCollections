package com.example.mycollections

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.Utility.db
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding
import com.example.mycollections.databinding.FragmentCurrentMonthCollectionBinding
import java.text.SimpleDateFormat
import java.util.Date

class CurrentMonthViewHolder(val binding: CurrentMonthCollectionRecyclerBinding): RecyclerView.ViewHolder(binding.root)

class CurrentMonthCollectionAdapter(val context: CurrentMonthCollectionFragment, val datas: MutableList<CurrentMonthCollection>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CurrentMonthViewHolder(CurrentMonthCollectionRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CurrentMonthViewHolder).binding
        val filePath = datas[position].filePath
        val documentID = datas[position].documentID

        binding.collectionNameTextView.text = datas[position].name
        binding.categoryTextView.text = datas[position].category
        GlideUtility.setImageToImageView(context, binding.collectionImage, filePath, documentID)
    }
}

class CurrentMonthCollectionFragment : Fragment() {
    private var currentMonthCollections = mutableListOf<CurrentMonthCollection>()
    private val adapter = CurrentMonthCollectionAdapter(this, currentMonthCollections)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCurrentMonthCollectionBinding.inflate(layoutInflater, container, false)

        val currentMonthOfYear = getCurrentMonthOfYear()
        binding.monthTextView.text = currentMonthOfYear

        addCurrentMonthCollectionsToRecycler(binding, currentMonthOfYear)
        return binding.root
    }

    private fun getCurrentMonthOfYear(): String
    {
        val yearFormat = SimpleDateFormat("yyyy년")
        val monthFormat = SimpleDateFormat("MM월")
        val now = System.currentTimeMillis()
        val date = Date(now)
        val currentYear = yearFormat.format(date)
        var currentMonth = monthFormat.format(date)
        if (currentMonth[0] == '0') {
            currentMonth = currentMonth.substring(1)
        }
        return "$currentYear $currentMonth"
    }

    private fun addCurrentMonthCollectionsToRecycler(binding: FragmentCurrentMonthCollectionBinding, currentMonthOfYear: String)
    {
        val userID = CurrentUser.user!!.id
        val collection = db.collection("user").document(userID).collection("collection")
        collection.whereGreaterThanOrEqualTo("releaseDate", currentMonthOfYear)
            .whereLessThanOrEqualTo("releaseDate", "$currentMonthOfYear 31일").get()
            .addOnSuccessListener {
                for (document in it.documents)
                {
                    val collectionCategory = document.get("collectionCategory").toString()
                    val collectionName = document.get("name").toString()
                    val filePath=document.get("filePath").toString()
                    val documentID = document.id
                    currentMonthCollections.add(CurrentMonthCollection(collectionCategory, collectionName, filePath, documentID))
                }
                binding.currentMonthRecycler.adapter = adapter
            }
    }

}


class CurrentMonthCollection(val category: String, val name: String, val filePath: String, val documentID: String)