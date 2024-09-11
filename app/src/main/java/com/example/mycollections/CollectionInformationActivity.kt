package com.example.mycollections

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.sendErrorToastMessage
import com.example.mycollections.Utility.storage
import com.example.mycollections.databinding.ActivityCollectionInformationBinding
import com.example.mycollections.databinding.DialogCollectionCostModifyBinding
import com.example.mycollections.databinding.DialogCollectionMemoBinding
import com.example.mycollections.databinding.DialogCollectionNameModifyBinding
import com.example.mycollections.databinding.DialogReleaseDateModifyBinding
import java.io.File

class CollectionInformationActivity : AppCompatActivity() {
    private val releaseDateRegex = Regex("^\\d{4}년 ([1-9]|1[0-2])월(?: ([1-9]|[1-2][0-9]|3[0-1])일)?|-")
    private val collectionCostRegex = Regex("^[1-9][0-9]*원\$|-")
    private val binding: ActivityCollectionInformationBinding by lazy {
        ActivityCollectionInformationBinding.inflate(layoutInflater)
    }
    private var isModified = true
    private var filePath = "noImage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListenersToTextViews()
        addListenerToCollectionImage()
        addListenerToSpinners()
    }

    private fun setListenersToTextViews()
    {
        binding.collectionNameTextView.setOnClickListener{
            val dialogBinding = DialogCollectionNameModifyBinding.inflate(layoutInflater)
            val builder = makeBuilder(dialogBinding)
            val dialog = builder.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    binding.collectionNameTextView.text = dialogBinding.valueEditText.editableText.toString()
                    isModified=true
                    dialog.dismiss()
                }
            }
            dialog.show()
        }

        binding.releaseDateTextView.setOnClickListener{
            val dialogBinding = DialogReleaseDateModifyBinding.inflate(layoutInflater)
            val builder = makeBuilder(dialogBinding)
            val dialog = builder.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (releaseDateRegex.matches(dialogBinding.valueEditText.editableText.toString()))
                    {
                        binding.releaseDateTextView.text = dialogBinding.valueEditText.editableText.toString()
                        isModified=true
                        dialog.dismiss()
                    }
                    else
                    {
                        dialogBinding.warningTextView.visibility = View.VISIBLE
                    }
                }
            }
            dialog.show()
        }

        binding.collectionCostTextView.setOnClickListener {
            val dialogBinding = DialogCollectionCostModifyBinding.inflate(layoutInflater)
            val builder = makeBuilder(dialogBinding)
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    if (collectionCostRegex.matches(dialogBinding.valueEditText.editableText.toString()))
                    {
                        binding.collectionCostTextView.text = dialogBinding.valueEditText.editableText.toString()
                        isModified=true
                        dialog.dismiss()
                    }
                    else
                    {
                        dialogBinding.warningTextView.visibility = View.VISIBLE
                    }
                }
            }
            dialog.show()
        }

        binding.memoTextView.setOnClickListener{
            val dialogBinding = DialogCollectionMemoBinding.inflate(layoutInflater)
            dialogBinding.valueEditText.setText(binding.memoTextView.text.toString())
            val builder = makeBuilder(dialogBinding)
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    binding.memoTextView.text = dialogBinding.valueEditText.text.toString()
                    isModified=true
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
    }

    private fun makeBuilder(binding: androidx.viewbinding.ViewBinding):  AlertDialog.Builder
    {
        val builder = AlertDialog.Builder(this)
        builder.setView(binding.root)
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인", null)
        return builder
    }

    private fun addListenerToCollectionImage()
    {
        val requestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode === android.app.Activity.RESULT_OK) {
                Glide.with(applicationContext).load(it.data?.data).into(binding.collectionImage)

                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null
                )
                cursor?.moveToFirst().let {
                    filePath = cursor?.getString(0) as String
                }
                isModified = true
            }
        }

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
        {isGranted->
            if (isGranted)
            {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                requestLauncher.launch(intent)
            }
            else
            {
                Toast.makeText(this, "사진 및 동영상 권한을 설정해야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.collectionImage.setOnClickListener{
            requestPermissionLauncher.launch("android.permission.READ_MEDIA_IMAGES")
        }
    }

    private fun addListenerToSpinners()
    {
        val spinnerSelectListener = object : AdapterView.OnItemSelectedListener {
            private var previousPosition = AdapterView.INVALID_POSITION
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                if (position != previousPosition)
                {
                    isModified = true
                    previousPosition = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.ownCategorySpinner.onItemSelectedListener = spinnerSelectListener
        binding.collectionCategorySpinner.onItemSelectedListener = spinnerSelectListener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (isModified) {
                    AlertDialog.Builder(this).run {
                        setTitle("알림")
                        setMessage("\n내용이 저장되지 않았습니다.\n이대로 종료하시겠습니까?\n")
                        setNegativeButton("아니오", null)
                        setPositiveButton("네") { DialogInterface, Int -> finish() }
                        show()
                    }
                } else {
                    saveCollectionData()
                    finish()
                }
                return true
            }

            R.id.save -> {
                isModified = false
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveCollectionData()
    {
        val unixTime = System.currentTimeMillis() / 1000L
        val ownCategory = binding.ownCategorySpinner.selectedItem.toString()
        val collectionCategory = binding.collectionCategorySpinner.selectedItem.toString()
        val name = binding.collectionNameTextView.text.toString()
        val releaseDate = binding.releaseDateTextView.text.toString()
        val cost = binding.collectionCostTextView.text.toString()
        val memo = binding.memoTextView.text.toString()
        val newCollection = hashMapOf(
            "unixTime" to unixTime,
            "ownCategory" to ownCategory,
            "collectionCategory" to collectionCategory,
            "name" to name,
            "releaseDate" to releaseDate,
            "cost" to cost,
            "memo" to memo,
            "filePath" to filePath
        )

        val id = CurrentUser.user!!.id

        db.collection("user").document(id).collection("collection").add(newCollection)
            .addOnSuccessListener {
                if (filePath != "noImage")
                {
                    val imageName = it.id
                    val storageRef = storage.reference
                    val imgRef = storageRef.child("${id}/${imageName}.png")
                    val file = Uri.fromFile(File(filePath))
                    imgRef.putFile(file)
                }

        }
            .addOnFailureListener {
                sendErrorToastMessage(this, it.toString())
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_collection, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
