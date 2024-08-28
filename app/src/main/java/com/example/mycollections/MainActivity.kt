package com.example.mycollections

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycollections.databinding.ActivityMainBinding
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
        val tabTitles = listOf("홈","앨범")

        binding.viewPager.adapter = MyFragmentPagerAdapter(this)

        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
        binding.tab.setTabTextColors(Color.parseColor("#9D9D9D"), Color.parseColor("#272727"))
    }
}