package com.example.mycollections

import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.example.mycollections.databinding.ActivityEditUserInfoBinding
import com.example.mycollections.databinding.ActivityRegisterBinding
import com.example.mycollections.databinding.ActivitySettingBinding
import com.example.mycollections.databinding.DialogUpdatePasswordBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class passwordChecker(open val binding: ViewBinding) {
    private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$")

    fun isPasswordQualified(password: String, warningTextView: TextView): Boolean
    {
        if (passwordRegex.matches(password))
        {
            warningTextView.visibility = View.INVISIBLE
            return true
        }
        else
        {
            warningTextView.visibility = View.VISIBLE
            return false
        }
    }

    fun isPasswordConfirmQualified(
        password: String,
        passwordConfirm: String,
        warningTextView: TextView): Boolean
    {
        if (password == passwordConfirm)
        {
            warningTextView.visibility = View.INVISIBLE
            return true
        }
        else
        {
            warningTextView.visibility = View.VISIBLE
            return false
        }
    }
}


class EditUserInfoConditionChecker(private val binding: DialogUpdatePasswordBinding, private val currentPassword: String)
{
    private val passwordChecker = passwordChecker(binding)
    private val newPassword = binding.newPasswordEditText.text.toString()
    private val newPasswordConfirm = binding.newPasswordConfirmEditText.text.toString()
    fun checkAllConditions(): Boolean
    {
        val inputConditions = mutableListOf<Boolean>()
        val passwordCondition = passwordChecker.isPasswordQualified(newPassword, binding.newPasswordWarningTextView)
        val passwordConfirmCondition = passwordChecker.isPasswordConfirmQualified(
            newPassword, newPasswordConfirm, binding.newPasswordConfirmWarningTextView)
        inputConditions.add(isCurrentPasswordCorrect())
        inputConditions.add(passwordCondition)
        inputConditions.add(passwordConfirmCondition)
        return inputConditions.all{ it }
    }

    private fun isCurrentPasswordCorrect(): Boolean
    {
        return if (currentPassword == binding.currentPasswordEditText.text.toString()) {
            binding.currentPasswordWarningTextView.visibility = View.INVISIBLE
            true
        } else {
            binding.currentPasswordWarningTextView.visibility = View.VISIBLE
            false
        }
    }
}


class RegisterConditionChecker(val binding: ActivityRegisterBinding)
{
    private val idRegex = Regex("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{4,16}\$")
    private val password = binding.passwordEditText.text.toString()
    private val passwordConfirm = binding.passwordConfirmEditText.text.toString()
    private val passwordChecker = passwordChecker(binding)
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

    suspend fun checkAllConditions(): Boolean
    {
        val idQuerySnapshot = withContext(Dispatchers.IO) {
            getQuerySnapShot("id", binding.idEditText.text.toString())
        }
        val emailQuerySnapshot = withContext(Dispatchers.IO) {
            getQuerySnapShot("email", binding.emailEditText.text.toString())
        }

        val inputConditions = mutableListOf<Boolean>()
        inputConditions.add(isIdQualified(idQuerySnapshot, binding.idWarningTextView))
        inputConditions.add(isDuplicated(emailQuerySnapshot, binding.emailWarningTextView))
        inputConditions.add(passwordChecker.isPasswordQualified(password, binding.passwordWarningTextView))
        inputConditions.add(passwordChecker.isPasswordConfirmQualified(
            password, passwordConfirm, binding.passwordConfirmWarningTextView))
        return inputConditions.all{ it }
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

}