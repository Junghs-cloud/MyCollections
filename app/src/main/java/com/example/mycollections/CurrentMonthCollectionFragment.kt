package com.example.mycollections

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.makeCollectionData
import com.example.mycollections.databinding.CurrentMonthCollectionRecyclerBinding
import com.example.mycollections.databinding.FragmentCurrentMonthCollectionBinding
import java.text.SimpleDateFormat
import java.util.Date

class CurrentMonthCollectionFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var dataViewModel: DataViewModel
    private val listener = object : OnViewClickListener {
        override fun onItemClickListener(view: View?, position: Int) {
            val intent = Intent(activity, CollectionInformationActivity::class.java)
            intent.putExtra("type", "edit")
            intent.putExtra("collectionData", currentMonthCollections[position])
            startActivity(intent)
        }

        override fun onItemLongClickListener(view: View?, position: Int) {
            AlertDialog.Builder(requireContext()).run {
                setMessage("해당 컬렉션을 삭제하시겠습니까?")
                setNegativeButton("아니오", null)
                setPositiveButton("네") { _: DialogInterface, i: Int ->
                    dataViewModel.removeData(currentMonthCollections[position], context)
                }
                show()
            }
        }
    }

    private var currentMonthCollections = listOf<CollectionData>()
    private val adapter = CurrentMonthCollectionAdapter(this, currentMonthCollections, listener)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentCurrentMonthCollectionBinding.inflate(layoutInflater, container, false)

        val currentMonthOfYear = getCurrentMonthOfYear()
        binding.monthTextView.text = currentMonthOfYear
        binding.currentMonthRecycler.adapter = adapter

        mainActivity = requireActivity() as MainActivity
        dataViewModel = mainActivity.dataViewModel
        dataViewModel.album.observe(viewLifecycleOwner) { album ->
            val currentMonthCollection = album.filter {
                it.releaseDate >= currentMonthOfYear &&
                        it.releaseDate <= "$currentMonthOfYear 31일"
            }
            currentMonthCollections = currentMonthCollection
            adapter.updateData(currentMonthCollection)
        }

        return binding.root
    }

    private fun getCurrentMonthOfYear(): String {
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

}