package com.example.mycollections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mycollections.databinding.ActivityFindPasswordBinding

class FindPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}