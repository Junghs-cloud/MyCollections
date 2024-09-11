package com.example.mycollections

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object Utility {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val storage = Firebase.storage
    fun cropToSquare(bitmap: Bitmap?): Bitmap {
        requireNotNull(bitmap) { "Bitmap cannot be null" }
        val width = bitmap.width
        val height = bitmap.height

        val newSize = width.coerceAtMost(height)

        val x = (width - newSize) / 2
        val y = (height - newSize) / 6

        return Bitmap.createBitmap(bitmap, x, y, newSize, newSize)
    }


    fun sendErrorToastMessage(context: Context, text: String = "오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}

object GlideUtility
{
    fun setImageToImageView(context: Fragment, imageView: ImageView, filePath: String, documentID: String)
    {
        getImageAndSetImageView(context, imageView, filePath, documentID)
    }
    private fun getImageAndSetImageView(context: Fragment, imageView: ImageView, filePath: String, documentID: String)
    {
        if (filePath != "noImage")
        {
            getBitmap(context, imageView, filePath)
        }
        else
        {
            getImageFromFirebaseStorage(context, imageView, documentID)
        }
    }

    private fun getImageFromFirebaseStorage(context: Fragment, imageView: ImageView, documentID: String)
    {
        val userID = CurrentUser.user!!.id
        val imgRef = Utility.storage.reference.child("${userID}/${documentID}.png")
        imgRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            getBitmap(context, imageView, imageUrl)
        }
    }

    private fun getBitmap(context: Fragment, imageView: ImageView, path: String)
    {
        Glide.with(context)
            .asBitmap()
            .load(path)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    val cropBitmap = Utility.cropToSquare(bitmap)
                    imageView.setImageBitmap(cropBitmap)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}