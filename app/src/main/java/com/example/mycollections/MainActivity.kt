package com.example.mycollections

import android.R
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycollections.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.tabs.TabLayoutMediator


class MyFragmentPagerAdapter(activity:FragmentActivity): FragmentStateAdapter(activity) {
    private val fragments:List<Fragment> = listOf(CurrentMonthCollectionFragment(), AlbumFragment())
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment =fragments[position]
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MyFragmentPagerAdapter(this)

        binding.tab.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.mycollections.R.id.menu_home -> binding.viewPager.currentItem = 0
                com.example.mycollections.R.id.menu_album->  binding.viewPager.currentItem = 1
            }
            true
        }
    }

}