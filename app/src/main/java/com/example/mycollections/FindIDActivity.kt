package com.example.mycollections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mycollections.databinding.ActivityFindIdBinding

class FindIDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}