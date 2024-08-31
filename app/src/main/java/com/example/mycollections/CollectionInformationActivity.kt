package com.example.mycollections

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mycollections.databinding.ActivityCollectionInformationBinding
import com.example.mycollections.databinding.DialogCollectionCostModifyBinding
import com.example.mycollections.databinding.DialogCollectionNameModifyBinding
import com.example.mycollections.databinding.DialogReleaseDateModifyBinding

class CollectionInformationActivity : AppCompatActivity() {
    private val releaseDateRegex = Regex("^\\d{4}년 ([1-9]|1[0-2])월(?: ([1-9]|[1-2][0-9]|3[0-1])일)?|-")
    private val collectionCostRegex = Regex("^[1-9][0-9]*원\$|-")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCollectionInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListenersToTextViews(binding)
    }

    private fun setListenersToTextViews(binding: ActivityCollectionInformationBinding)
    {
        binding.collectionNameTextView.setOnClickListener{
            val dialogBinding = DialogCollectionNameModifyBinding.inflate(layoutInflater)
            val builder = makeBuilder(dialogBinding)
            val dialog = builder.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    binding.collectionNameTextView.text = dialogBinding.valueEditText.editableText.toString()
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
    }

    private fun makeBuilder(binding: androidx.viewbinding.ViewBinding):  AlertDialog.Builder
    {
        val builder = AlertDialog.Builder(this)
        builder.setView(binding.root)
        builder.setNegativeButton("취소", null)
        builder.setPositiveButton("확인", null)
        return builder
    }

}