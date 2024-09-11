package com.example.mycollections

import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.mycollections.Utility.auth
import com.example.mycollections.Utility.db
import com.example.mycollections.databinding.ActivityEditUserInfoBinding
import com.example.mycollections.databinding.DialogDeleteAccountBinding
import com.example.mycollections.databinding.DialogEditNameBinding
import com.example.mycollections.databinding.DialogUpdatePasswordBinding
import org.checkerframework.checker.units.qual.Current

class EditUserInfoActivity : AppCompatActivity() {
    private val binding: ActivityEditUserInfoBinding by lazy {
        ActivityEditUserInfoBinding.inflate(layoutInflater)
    }
    private var currentPassword = CurrentUser.user!!.password
    private val id = CurrentUser.user!!.id
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loadUserInfo()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.passwordLayout.setOnClickListener{
            makePasswordDialog()
        }

        binding.nameLayout.setOnClickListener {
            makeNameDialog()
        }

    }

    private fun makePasswordDialog()
    {
        val dialogBinding = DialogUpdatePasswordBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialogBinding.confirmButton.setOnClickListener {
            val newPassword = dialogBinding.newPasswordEditText.text.toString()
            val editUserInfoConditionChecker = EditUserInfoConditionChecker(dialogBinding, currentPassword)
            if (editUserInfoConditionChecker.checkAllConditions())
            {
                updatePassword(dialog, newPassword)
            }
        }
        dialog.show()
    }

    private fun updatePassword(dialog: AlertDialog, newPassword: String)
    {
        auth.currentUser!!.updatePassword(newPassword)
            .addOnSuccessListener{
                updateDB(dialog, "password", newPassword)
            }
            .addOnFailureListener {
                Utility.sendErrorToastMessage(this, it.toString())
            }
    }

    private fun updateDB(dialog: AlertDialog, field: String, newValue: String)
    {
        db.collection("user").document(id).update(field, newValue)
            .addOnSuccessListener {
                if (field == "password")
                {
                    CurrentUser.user!!.password = newValue
                    currentPassword = newValue
                }
                dialog.dismiss()
            }
            .addOnFailureListener {
                Utility.sendErrorToastMessage(this, it.toString())
            }
    }

    private fun makeNameDialog()
    {
        val dialogBinding = DialogEditNameBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialogBinding.confirmButton.setOnClickListener {
            val password = dialogBinding.currentPasswordEditText.text.toString()
            if (currentPassword == password)
            {
                val newName = dialogBinding.nameEditText.text.toString()
                updateDB(dialog, "name", newName)
                binding.nameTextView.text = newName
                CurrentUser.user!!.name = newName
            }
            else
            {
                dialogBinding.currentPasswordWarningTextView.visibility= View.VISIBLE
            }
        }
        dialog.show()
    }

    private fun loadUserInfo()
    {
        val name = CurrentUser.user!!.name

        binding.idTextView.text = id
        binding.nameTextView.text = name
    }

}