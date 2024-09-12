package com.example.mycollections

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.Transliterator.Position
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.makeCollectionData
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding
import com.example.mycollections.databinding.FragmentCurrentMonthCollectionBinding
import java.text.SimpleDateFormat
import java.util.Date

interface OnViewClickListener { fun onItemClickListener(view: View?, position: Int) }

class CurrentMonthViewHolder(val binding: CurrentMonthCollectionRecyclerBinding):
    RecyclerView.ViewHolder(binding.root)

class CurrentMonthCollectionAdapter(
    val context: CurrentMonthCollectionFragment,
    private val data: MutableList<CollectionData>,
    private val listener: OnViewClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = CurrentMonthViewHolder(CurrentMonthCollectionRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CurrentMonthViewHolder).binding
        val filePath = data[position].filePath
        val documentID = data[position].documentID

        binding.collectionNameTextView.text = data[position].name
        binding.categoryTextView.text = "[" + data[position].collectionCategory + "]"
        GlideUtilityFragment.setImageToImageView(context, binding.collectionImage, filePath, documentID)

        holder.itemView.setOnClickListener{
            listener.onItemClickListener(it, position)
        }

        holder.itemView.setOnLongClickListener{
            Log.d("jhs", "dd")
            true
        }
    }

}

class CurrentMonthCollectionFragment : Fragment() {

    private val listener = object: OnViewClickListener{
        override fun onItemClickListener(view: View?, position: Int) {
            val intent = Intent(activity, CollectionInformationActivity::class.java)
            intent.putExtra("type", "edit")
            intent.putExtra("collectionData", currentMonthCollections[position])
            startActivity(intent)
        }
    }

    private var currentMonthCollections = mutableListOf<CollectionData>()
    private val adapter = CurrentMonthCollectionAdapter(this, currentMonthCollections, listener)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {
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
                    val collectionData = makeCollectionData(document)
                    currentMonthCollections.add(collectionData)
                }
                binding.currentMonthRecycler.adapter = adapter
            }
    }

}