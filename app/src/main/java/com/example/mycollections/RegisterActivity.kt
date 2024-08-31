package com.example.mycollections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.databinding.ActivityRegisterBinding
import com.example.mycollections.databinding.DialogInformBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            val dialogBinding = DialogInformBinding.inflate(layoutInflater)
            AlertDialog.Builder(this).run{
                setView(dialogBinding.root)
                show()
            }
        }
    }
}