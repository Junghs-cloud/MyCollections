package com.example.mycollections

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.isNetworkAvailable
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.databinding.ActivityInitialBinding
import com.google.firebase.firestore.DocumentSnapshot

class InitialActivity : AppCompatActivity() {
    private var initTime = 0L
    private val binding: ActivityInitialBinding by lazy {
        ActivityInitialBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth.signOut()

        if (isNetworkAvailable(this)) {
            val isLoggedIn = EncryptedPrefsManager.getBoolean(this, "isLoggedIn", false)
            if (isLoggedIn) {
                val id = EncryptedPrefsManager.getString(this, "id", null)
                val password = EncryptedPrefsManager.getString(this, "password", null)
                binding.idEditText.setText(id)
                binding.passwordEditText.setText(password)
                addClickListeners()
                if (id != null && password != null) {
                    tryLogin(id, password)
                }
            } else {
                addClickListeners()
            }
        } else {
            sendErrorToastMessage(this, "네트워크 연결을 확인해주세요.")
        }

    }

    private fun addClickListeners() {
        val id = binding.idEditText.text
        val password = binding.passwordEditText.text
        binding.loginButton.setOnClickListener {
            if (isNetworkAvailable(this)) {
                tryLogin(id.toString(), password.toString())
            } else {
                Toast.makeText(this, "네트워크 연결을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.findIDTextView.setOnClickListener {
            val intent = Intent(this, FindIDActivity::class.java)
            startActivity(intent)
        }

        binding.findpasswordTextView.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun tryLogin(id: String, password: String) {
        db.collection("user").document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                val email = documentSnapshot.data?.get("email").toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            setUser(documentSnapshot)
                            EncryptedPrefsManager.saveString(this, "id", id)
                            EncryptedPrefsManager.saveString(this, "password", password)
                            EncryptedPrefsManager.saveBoolean(this, "isLoggedIn", true)
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "오류가 발생했습니다.\n 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setUser(documentSnapshot: DocumentSnapshot) {
        val id = binding.idEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val email = documentSnapshot.data?.get("email").toString()
        val name = documentSnapshot.data?.get("name").toString()
        CurrentUser.user = User(id, password, email, name)
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