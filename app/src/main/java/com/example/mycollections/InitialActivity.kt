package com.example.mycollections

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.databinding.ActivityInitialBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = binding.idEditText.text
        val password = binding.passwordEditText.text
        val auth = com.google.firebase.ktx.Firebase.auth
        auth.signOut()

        binding.loginButton.setOnClickListener{
            auth.signInWithEmailAndPassword(id.toString(), password.toString()).
            addOnCompleteListener(this){ task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
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