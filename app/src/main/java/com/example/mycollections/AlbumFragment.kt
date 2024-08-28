package com.example.mycollections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycollections.databinding.AlbumRecyclerBinding
import com.example.mycollections.databinding.FragmentAlbumBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AlbumViewHolder(val binding: AlbumRecyclerBinding): RecyclerView.ViewHolder(binding.root)

class AlbumFragmentAdapter(val datas: MutableList<CollectionAlbum>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun getItemCount(): Int = datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = AlbumViewHolder(AlbumRecyclerBinding.inflate
        (LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AlbumViewHolder).binding
        binding.nameTextView.text =datas[position].name
        binding.collectionImage.setImageBitmap(datas[position].image)
    }
}

class AlbumFragment : Fragment() {
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

        val binding= FragmentAlbumBinding.inflate(layoutInflater, container, false)

        val album = mutableListOf<CollectionAlbum>()
        binding.albumRecycler.layoutManager= GridLayoutManager(activity, 3)
        binding.albumRecycler.adapter = AlbumFragmentAdapter(album)
        return binding.root
    }

    private fun makeCollectionAlbum(image: Int, name: String): CollectionAlbum
    {
        val originalBitmap: Bitmap = BitmapFactory.decodeResource(resources, image)
        val squareBitmap = ImageUtils.cropToSquare(originalBitmap)
        return CollectionAlbum(squareBitmap, name)
    }
}

class CollectionAlbum(val image: Bitmap, val name: String)

object ImageUtils {
    fun cropToSquare(bitmap: Bitmap?): Bitmap {
        requireNotNull(bitmap) { "Bitmap cannot be null" }
        val width = bitmap.width
        val height = bitmap.height

        // 정사각형으로 자를 크기 결정
        val newSize = Math.min(width, height)

        // 중앙을 기준으로 자르기
        val x = (width - newSize) / 2
        val y = (height - newSize) / 6

        // 자르기 작업 수행
        return Bitmap.createBitmap(bitmap, x, y, newSize, newSize)
    }
}