package com.example.mycollections

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.storage
import com.example.mycollections.databinding.AlbumRecyclerBinding
import com.example.mycollections.databinding.FragmentAlbumBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.bumptech.glide.request.transition.Transition
import com.google.android.play.integrity.internal.i

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AlbumViewHolder(val binding: AlbumRecyclerBinding): RecyclerView.ViewHolder(binding.root)

class AlbumFragmentAdapter(val context: AlbumFragment, var data: List<CollectionData>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AlbumViewHolder(AlbumRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AlbumViewHolder).binding
        binding.nameTextView.text = data[position].collectionName
        val filePath = data[position].filePath
        val documentID = data[position].documentID
        GlideUtility.setImageToImageView(context, binding.collectionImage, filePath, documentID)
    }

    fun update(newData: List<CollectionData>)
    {
        data = newData
        notifyDataSetChanged()
    }
}

class AlbumFragment : Fragment() {
    enum class Category(val position: Int){
        All(0), ExcepthWishList(1), WishList(2)
    }

    private var collectionDocuments: MutableList<DocumentSnapshot>? = null
    private var album = mutableListOf<CollectionData>()
    private lateinit var adapter: AlbumFragmentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAlbumBinding.inflate(layoutInflater, container, false)
        getCollectionDataFromDB(binding)

        binding.floatingActionButton.setOnClickListener{
            val intent= Intent(context, CollectionInformationActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun getCollectionDataFromDB(binding: FragmentAlbumBinding)
    {
        adapter = AlbumFragmentAdapter(this, album)
        val userID = CurrentUser.user!!.id
        db.collection("user").document(userID).collection("collection").get()
            .addOnSuccessListener {
                collectionDocuments = it.documents
                for (document in it.documents)
                {
                    album.add(CollectionData(document))
                }
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
                changeAlbumData(position)
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
        binding.ownCategorySpinner.onItemSelectedListener = ownCategorySpinnerListener
        binding.orderSpinner.onItemSelectedListener = sortOrderSpinnerListener
    }

    private fun changeAlbumData(position: Int)
    {
        if (collectionDocuments == null)
        {
            return
        }
        val newAlbum = mutableListOf<CollectionData>()
        val filtered: List<DocumentSnapshot> = when (position)
        {
            Category.All.position -> collectionDocuments!!
            Category.ExcepthWishList.position -> collectionDocuments!!.filter{it.get("ownCategory").toString() != "[위시 리스트]"}
            else -> collectionDocuments!!.filter{it.get("ownCategory").toString() == "[위시 리스트]"}
        }
        for (document in filtered)
        {
            newAlbum.add(CollectionData(document))
        }
        adapter.update(newAlbum)
    }

    private fun changeSortOrder(position: Int)
    {
        val newOrderAlbum = when (position)
        {
            0 -> adapter.data.sortedBy { it.releaseDate }
            else -> adapter.data.sortedBy{it.unixTime}
        }
        adapter.update(newOrderAlbum)
    }

}

class CollectionData(private val document: DocumentSnapshot)
{
    val documentID = document.id
    val filePath = document.get("filePath").toString()
    val collectionName = document.get("name").toString()
    val releaseDate = document.get("releaseDate").toString()
    val unixTime = document.get("unixTime").toString().toLong()
}


