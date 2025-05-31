package com.example.mycollections

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycollections.Utility.isNetworkAvailable
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    enum class Category(val position: Int) {
        All(0), ExceptWishList(1), WishList(2)
    }

    private lateinit var mainActivity: MainActivity
    private lateinit var dataViewModel: DataViewModel
    private var allCollectionData: List<CollectionData> = listOf()
    private lateinit var adapter: AlbumFragmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAlbumBinding.inflate(layoutInflater, container, false)

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { activityResult ->
            if (activityResult.data == null) {
                return@registerForActivityResult
            }
            if (activityResult.data!!.getStringExtra("type") == "add") {
                activityResult.data!!.getParcelableExtra<CollectionData>("newCollectionData")?.let {
                    dataViewModel.addData(it)
                }
            } else {
                activityResult.data!!.getParcelableExtra<CollectionData>("newCollectionData")?.let {
                    val position = activityResult.data!!.getIntExtra("position", -1)
                    if (position != -1) {
                        if (context != null && isNetworkAvailable(requireContext())) {
                            dataViewModel.replaceData(allCollectionData?.get(position)!!, it)
                        } else {
                            context?.let { it1 -> sendErrorToastMessage(it1, "네트워크 연결을 확인해주세요.") }
                        }
                    }

                }
            }
        }

        getCollectionDataFromDB(binding, requestLauncher)
        addSortListenerToSpinners(binding)
        binding.albumRecycler.layoutManager = GridLayoutManager(activity, 3)
        binding.albumRecycler.adapter = adapter

        mainActivity = requireActivity() as MainActivity
        dataViewModel = mainActivity.dataViewModel

        dataViewModel.album.observe(viewLifecycleOwner) {
            allCollectionData = it
            adapter.update(allCollectionData!!)
        }

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, CollectionInformationActivity::class.java)
            intent.putExtra("type", "add")
            requestLauncher.launch(intent)
        }
        return binding.root
    }

    private fun getCollectionDataFromDB(
        binding: FragmentAlbumBinding,
        requestLauncher: ActivityResultLauncher<Intent>
    ) {
        adapter = AlbumFragmentAdapter(this, allCollectionData, object : OnViewClickListener {
            override fun onItemClickListener(view: View?, position: Int) {
                val intent = Intent(activity, CollectionInformationActivity::class.java)
                intent.putExtra("type", "edit")
                intent.putExtra("position", position)
                intent.putExtra("collectionData", allCollectionData[position])
                requestLauncher.launch(intent)
            }

            override fun onItemLongClickListener(view: View?, position: Int) {
                AlertDialog.Builder(requireContext()).run {
                    setMessage("해당 컬렉션을 삭제하시겠습니까?")
                    setNegativeButton("아니오", null)
                    setPositiveButton("네") { dialogInterface: DialogInterface, i: Int ->
                        dataViewModel.removeData(allCollectionData[position], context)
                    }
                    show()
                }
            }
        })
    }

    private fun addSortListenerToSpinners(binding: FragmentAlbumBinding) {
        val ownCategorySpinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                changeAlbumData(binding, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val sortOrderSpinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                changeSortOrder(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val optionListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    adapter.updateNameOption(false)
                } else {
                    adapter.updateNameOption(true)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.ownCategorySpinner.onItemSelectedListener = ownCategorySpinnerListener
        binding.orderSpinner.onItemSelectedListener = sortOrderSpinnerListener
        binding.showItemSpinner.onItemSelectedListener = optionListener
    }

    private fun changeAlbumData(binding: FragmentAlbumBinding, position: Int) {
        if (allCollectionData == null) {
            return
        }
        val filtered: List<CollectionData> = when (position) {
            Category.All.position -> allCollectionData!!
            Category.ExceptWishList.position -> allCollectionData!!.filter { it.ownCategory != "[위시 리스트]" }
            else -> allCollectionData!!.filter { it.ownCategory == "[위시 리스트]" }
        }
        adapter.setNewData(filtered)
        val position = binding.orderSpinner.selectedItemPosition
        changeSortOrder(position)
        adapter.update(filtered)
    }

    private fun changeSortOrder(position: Int) {
        val newOrderAlbum = when (position) {
            0 -> adapter.data.sortedBy { it.unixTime }
            else -> adapter.data.sortedBy { it.releaseDate }
        }
        adapter.update(newOrderAlbum)
    }
}