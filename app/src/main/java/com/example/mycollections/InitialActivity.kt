package com.example.mycollections

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.databinding.ActivityInitialBinding

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = binding.idEditText.text
        val password = binding.passwordEditText.text

        binding.loginButton.setOnClickListener{
            Log.d("jhs", id.toString()+password.toString())
        }

        binding.findIDTextView.setOnClickListener{
            val intent= Intent(this, FindIDActivity::class.java)
            startActivity(intent)
        }

        binding.findpasswordTextView.setOnClickListener{
            val intent= Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.registerTextView.setOnClickListener{
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}