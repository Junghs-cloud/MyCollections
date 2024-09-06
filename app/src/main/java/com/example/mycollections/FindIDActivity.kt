package com.example.mycollections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.Utility.db
import com.example.mycollections.databinding.ActivityFindIdBinding
import com.example.mycollections.databinding.DialogInformBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FindIDActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindIdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.confirmButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            db.collection("user").whereEqualTo("name", name).whereEqualTo("email", email).get()
                .addOnSuccessListener {querySnapShot->
                    if (querySnapShot.isEmpty)
                    {
                        Toast.makeText(this, "해당하는 아이디가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val id = querySnapShot.documents[0].data?.get("id").toString()
                        makeInformDialog(id)
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "오류가 발생했습니다.\n 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun makeInformDialog(id: String)
    {
        val dialogBinding = DialogInformBinding.inflate(layoutInflater)
        dialogBinding.messageTextView.text="아이디는 "+id+"입니다. \n 확인 버튼을 누르면 초기로 돌아갑니다."
        AlertDialog.Builder(this).run{
            setView(dialogBinding.root)
            dialogBinding.confirmButton.setOnClickListener {
                finish()
            }
            show()
        }
    }
}