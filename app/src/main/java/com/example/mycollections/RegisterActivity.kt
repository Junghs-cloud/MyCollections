package com.example.mycollections

import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private val db = Firebase.firestore
    private val auth = com.google.firebase.ktx.Firebase.auth
    private val idRegex = Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{4,16}\$")
    private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$")
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                checkAllConditionsAndRegister()
            }
        }
    }
    private suspend fun checkAllConditionsAndRegister()
    {
        val inputConditions = mutableListOf<Boolean>()
        val idQuerySnapshot = withContext(Dispatchers.IO) {
            getQuerySnapShot("id", binding.idEditText.text.toString())
        }
        val emailQuerySnapshot = withContext(Dispatchers.IO) {
            getQuerySnapShot("email", binding.emailEditText.text.toString())
        }
        inputConditions.add(isIdQualified(idQuerySnapshot, binding.idWarningTextView))
        inputConditions.add(isDuplicated(emailQuerySnapshot, binding.emailWarningTextView))
        inputConditions.add(isPasswordQualified())
        inputConditions.add(isPasswordConfirmQualified())
        if (inputConditions.all{ it })
        {
            createNewUser(binding)
            makeInformDialog()
        }
    }
    private suspend fun getQuerySnapShot(field: String, value: String): QuerySnapshot =
        suspendCancellableCoroutine{ continuation ->
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection("user")

            val query = documentRef.whereEqualTo(field, value)
            query.get()
                .addOnSuccessListener { querySnapShot ->
                    continuation.resume(querySnapShot)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    private fun isIdQualified(query: QuerySnapshot, textView: TextView): Boolean
    {
        if (idRegex.matches(binding.idEditText.text.toString()))
        {
            textView.text = "이미 존재하는 아이디입니다."
            return isDuplicated(query, textView)
        }
        else
        {
            textView.text = "아이디는 4-16자의 영문 소문자와 숫자 조합이어야 합니다."
            textView.visibility = View.VISIBLE
            return false
        }
    }

    private fun isDuplicated(query: QuerySnapshot, textView: TextView): Boolean
    {
        return if (query.isEmpty) {
            textView.visibility = View.INVISIBLE
            true
        } else {
            textView.visibility = View.VISIBLE
            false
        }
    }

    private fun isPasswordQualified(): Boolean
    {
        if (passwordRegex.matches(binding.passwordEditText.text.toString()))
        {
            binding.passwordWarningTextView.visibility = View.INVISIBLE
            return true
        }
        else
        {
            binding.passwordWarningTextView.visibility = View.VISIBLE
            return false
        }
    }

    private fun isPasswordConfirmQualified(): Boolean
    {
        val password = binding.passwordEditText.text.toString()
        val passwordConfirm = binding.passwordConfirmEditText.text.toString()
        if (password == passwordConfirm)
        {
            binding.passwordConfirmWarningTextView.visibility = View.INVISIBLE
            return true
        }
        else
        {
            binding.passwordConfirmWarningTextView.visibility = View.VISIBLE
            return false
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
        db.collection("user").document().set(newUser).addOnSuccessListener {
            auth.createUserWithEmailAndPassword(email, password)
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