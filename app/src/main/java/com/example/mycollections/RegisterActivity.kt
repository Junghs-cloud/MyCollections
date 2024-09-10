package com.example.mycollections

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.databinding.ActivityRegisterBinding
import com.example.mycollections.databinding.DialogInformBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth.signOut()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                val registerConditionChecker = RegisterConditionChecker(binding)
                if (registerConditionChecker.checkAllConditions())
                {
                    createNewUser(binding)
                    makeInformDialog()
                }
            }
        }
    }

    private fun createNewUser(binding: ActivityRegisterBinding)
    {
        val email = binding.emailEditText.text.toString()
        val id = binding.idEditText.text.toString()
        val name = binding.nameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val newUser = hashMapOf(
            "email" to email,
            "id" to id,
            "name" to name,
            "password" to password
        )
        db.collection("user").document(id).set(newUser).addOnSuccessListener {
            auth.createUserWithEmailAndPassword(email, password)
            auth.signOut()
        }
            .addOnFailureListener{
                Toast.makeText(this, "오류가 발생했습니다.\n 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun makeInformDialog()
    {
        val dialogBinding = DialogInformBinding.inflate(layoutInflater)
        dialogBinding.messageTextView.text="가입이 완료되었습니다.\n 초기화면으로 돌아갑니다."
        AlertDialog.Builder(this).run{
            setView(dialogBinding.root)
            dialogBinding.confirmButton.setOnClickListener {
                finish()
            }
            show()
        }
    }
}