package com.example.mycollections

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycollections.databinding.ActivityCollectionInformationBinding
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener


class CollectionPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {
    private val fragments:List<Fragment> = listOf(CollectionInformationTextsFragment())
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment =fragments[position]
}

class CollectionInformationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCollectionInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}