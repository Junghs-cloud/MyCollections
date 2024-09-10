package com.example.mycollections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycollections.Utility.auth
import com.example.mycollections.databinding.ActivityMainBinding


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
                R.id.menu_home -> binding.viewPager.currentItem = 0
                R.id.menu_album->  binding.viewPager.currentItem = 1
            }
            true
        }

        binding.setting.setOnClickListener{
            val intent= Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

}