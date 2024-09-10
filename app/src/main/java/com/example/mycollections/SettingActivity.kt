package com.example.mycollections

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.databinding.ActivitySettingBinding
import com.example.mycollections.databinding.DialogDeleteAccountBinding
import com.google.firebase.firestore.DocumentSnapshot

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.editLayout.setOnClickListener{
            val intent = Intent(this, EditUserInfoActivity::class.java)
            startActivity(intent)
        }

        binding.logoutLayout.setOnClickListener{
            auth.signOut()
            returnToInitialActivity()
        }

        binding.deleteAccountLayout.setOnClickListener{
            val dialogBinding = DialogDeleteAccountBinding.inflate(layoutInflater)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogBinding.root)
            builder.setNegativeButton("취소", null)
            builder.setPositiveButton("확인", null)
            val dialog = builder.create()

            dialog.setOnShowListener {
                setPositiveButtonListener(dialog, it, dialogBinding)
            }
            dialog.show()
        }
    }

    private fun returnToInitialActivity()
    {
        val intent = Intent(this, InitialActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setPositiveButtonListener(dialog: AlertDialog, dialogInterface: DialogInterface, dialogBinding: DialogDeleteAccountBinding)
    {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {possitiveButton->
            val currentUser = auth.currentUser
            if (currentUser!= null)
            {
                val passwordEditText = dialogBinding.passwordEditText.text
                eraseUserDataIfPasswordCorrect(passwordEditText.toString(), dialogInterface)
            }
            else
            {
                sendErrorToastMessage(this)
            }
        }
    }

    private fun eraseUserDataIfPasswordCorrect(password: String, dialog: DialogInterface)
    {
        val email = auth.currentUser!!.email.toString()
        db.collection("user").whereEqualTo("email", email).whereEqualTo("password", password).get()
            .addOnSuccessListener {querySnapShot->
                if (querySnapShot.isEmpty)
                {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    eraseUserData(querySnapShot.documents[0], dialog)
                }
            }
            .addOnFailureListener {
                sendErrorToastMessage(this)
            }
    }

    private fun eraseUserData(document: DocumentSnapshot, dialog: DialogInterface)
    {
        db.collection("user").document(document.id).delete()
            .addOnSuccessListener {
                deleteCurrentUserOfAuth(dialog)
            }
            .addOnFailureListener { e ->
                sendErrorToastMessage(this)
            }
    }

    private fun deleteCurrentUserOfAuth(dialog: DialogInterface)
    {
        auth.currentUser!!.delete().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                dialog.dismiss()
                returnToInitialActivity()

            } else
            {
                sendErrorToastMessage(this, "${task.exception?.message}")
            }
        }
    }
}