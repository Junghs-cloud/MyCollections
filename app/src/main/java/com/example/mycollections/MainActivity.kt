package com.example.mycollections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mycollections.Utility.auth
import com.example.mycollections.databinding.ActivityMainBinding


class MyFragmentPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments: List<Fragment> =
        listOf(CurrentMonthCollectionFragment(), AlbumFragment())

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}

class MainActivity : AppCompatActivity() {
    private var initTime = 0L
    val dataViewModel: DataViewModel by lazy {
        ViewModelProvider(this)[DataViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = MyFragmentPagerAdapter(this)

        binding.tab.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> binding.viewPager.currentItem = 0
                R.id.menu_album -> binding.viewPager.currentItem = 1
            }
            true
        }

        binding.setting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - initTime > 3000) {
                Toast.makeText(this, "한 번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                initTime = System.currentTimeMillis()
                return true
            }
            finishAffinity()
        }
        return super.onKeyDown(keyCode, event)
    }

}