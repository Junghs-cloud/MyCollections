package com.example.mycollections

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.databinding.ActivityInitialBinding
import com.google.firebase.firestore.DocumentSnapshot

class InitialActivity : AppCompatActivity() {
    private val binding: ActivityInitialBinding by lazy {
        ActivityInitialBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth.signOut()
        addClickListeners()
    }

    private fun addClickListeners()
    {
        val id = binding.idEditText.text
        val password = binding.passwordEditText.text
        binding.loginButton.setOnClickListener{
            tryLogin(id.toString(), password.toString())
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

    private fun tryLogin(id: String, password: String)
    {
        db.collection("user").document(id).get()
            .addOnSuccessListener { documentSnapshot->
                val email = documentSnapshot.data?.get("email").toString()
                auth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this){ task ->
                    if (task.isSuccessful) {
                        setUser(documentSnapshot)
                        val intent= Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "오류가 발생했습니다.\n 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setUser(documentSnapshot: DocumentSnapshot)
    {
        val id = binding.idEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val email = documentSnapshot.data?.get("email").toString()
        val name = documentSnapshot.data?.get("name").toString()

        CurrentUser.user = User(id, password, email, name)
    }
}