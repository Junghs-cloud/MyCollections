package com.example.mycollections

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.databinding.ActivityFindPasswordBinding
import com.example.mycollections.databinding.DialogInformBinding
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FindPasswordActivity : AppCompatActivity() {
    private val binding: ActivityFindPasswordBinding by lazy {
        ActivityFindPasswordBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val id = binding.idEditText.text
        val name = binding.nameEditText.text
        val email = binding.emailEditText.text

        auth.signOut()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            val id = id.toString()
            val name= name.toString()
            val email = email.toString()
            db.collection("user").whereEqualTo("id", id).whereEqualTo("name", name).whereEqualTo("email", email).get()
                .addOnSuccessListener {querySnapShot->
                    if (querySnapShot.isEmpty)
                    {
                        Toast.makeText(this, "아이디, 이름, 이메일 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val loadingImage = binding.gifImageView
                        Glide.with(this).load(R.drawable.loading).into(loadingImage)
                        binding.gifImageView.visibility = View.VISIBLE
                        makeNewPasswordOfUser(querySnapShot, email)
                    }
                }
                .addOnFailureListener {
                    sendErrorToastMessage(this)
                }
        }
    }

    private fun makeInformDialog()
    {
        val dialogBinding = DialogInformBinding.inflate(layoutInflater)
        AlertDialog.Builder(this).run{
            setView(dialogBinding.root)
            dialogBinding.confirmButton.setOnClickListener {
                finish()
            }
            show()
        }
    }

    private fun makeNewPasswordOfUser(querySnapShot: QuerySnapshot, email: String)
    {
        val password = querySnapShot.documents[0].data?.get("password").toString()
        val userID = querySnapShot.documents[0].id
        updateAuthAndDB(userID, password, email)
    }

    private fun updateAuthAndDB(userID: String, password: String, email: String)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful)
            {
                val user = auth.currentUser
                val newPassword = makeNewPassword()
                user?.updatePassword(newPassword)?.addOnCompleteListener {task->
                    if (task.isSuccessful)
                    {
                        updateDBAndSendEmail(userID, newPassword, email)
                    }
                    else
                    {
                        binding.gifImageView.visibility = View.INVISIBLE
                        sendErrorToastMessage(this)
                    }
                }
            }
            else
            {
                binding.gifImageView.visibility = View.INVISIBLE
                sendErrorToastMessage(this)
            }
        }
    }

    private fun updateDBAndSendEmail(documentID: String, newPassword: String, email: String)
    {
        db.collection("user").document(documentID).update("password", newPassword).addOnCompleteListener{ its->
            binding.gifImageView.visibility = View.INVISIBLE
            if(its.isSuccessful){
                CoroutineScope(Dispatchers.IO).launch {
                    val emailSender = EmailSender(email, newPassword)
                    emailSender.sendMail()
                }
                makeInformDialog()
            }
            else
            {
                sendErrorToastMessage(this)
            }
        }
    }

    private fun makeNewPassword(): String
    {
        var newPassword = ""
        val str = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        val range=str.indices
        for (x in 0..7) {
            val random = range.random()
            newPassword += str[random]
        }
        return newPassword
    }
}