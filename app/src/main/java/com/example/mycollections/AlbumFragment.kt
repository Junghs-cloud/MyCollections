package com.example.mycollections

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.makeCollectionData
import com.example.mycollections.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    enum class Category(val position: Int){
        All(0), ExceptWishList(1), WishList(2)
    }

    private var allCollectionData: MutableList<CollectionData>? = null
    private var album = mutableListOf<CollectionData>()
    private lateinit var adapter: AlbumFragmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAlbumBinding.inflate(layoutInflater, container, false)
        getCollectionDataFromDB(binding)

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            it.data!!.getParcelableExtra<CollectionData>("newCollectionData")?.let {
                allCollectionData?.add(it)

                adapter.notifyDataSetChanged()
            }
        }


        binding.floatingActionButton.setOnClickListener{
            val intent= Intent(context, CollectionInformationActivity::class.java)
            intent.putExtra("type", "add")
            requestLauncher.launch(intent)
        }



        return binding.root
    }

    private fun getCollectionDataFromDB(binding: FragmentAlbumBinding)
    {
        adapter = AlbumFragmentAdapter(this, album, object: OnViewClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                val intent = Intent(activity, CollectionInformationActivity::class.java)
                intent.putExtra("type", "edit")
                intent.putExtra("collectionData", album[position])
                startActivity(intent)
            }
        })
        val userID = CurrentUser.user!!.id
        db.collection("user").document(userID).collection("collection").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents)
                {
                    val collectionData = makeCollectionData(document)
                    album.add(collectionData)
                }
                album.sortBy { it.unixTime }
                allCollectionData = album
                addSortListenerToSpinners(binding)
                binding.albumRecycler.layoutManager = GridLayoutManager(activity, 3)
                binding.albumRecycler.adapter = adapter
            }
            .addOnFailureListener {
            }
    }

    private fun addSortListenerToSpinners(binding: FragmentAlbumBinding)
    {
        val ownCategorySpinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                changeAlbumData(binding, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val sortOrderSpinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                changeSortOrder(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val optionListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                if (position==0)
                {
                    adapter.updateNameOption(false)
                }
                else
                {
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

    private fun changeAlbumData(binding: FragmentAlbumBinding, position: Int)
    {
        if (allCollectionData == null)
        {
            return
        }
        val filtered: List<CollectionData> = when (position)
        {
            Category.All.position -> allCollectionData!!
            Category.ExceptWishList.position -> allCollectionData!!.filter{it.ownCategory != "[위시 리스트]"}
            else -> allCollectionData!!.filter{it.ownCategory == "[위시 리스트]"}
        }
        adapter.setNewData(filtered)
        val position = binding.orderSpinner.selectedItemPosition
        changeSortOrder(position)
        adapter.update(filtered)
    }

    private fun changeSortOrder(position: Int)
    {
        val newOrderAlbum = when (position)
        {
            0 -> adapter.data.sortedBy { it.unixTime }
            else -> adapter.data.sortedBy{it.releaseDate}
        }
        adapter.update(newOrderAlbum)
    }
}