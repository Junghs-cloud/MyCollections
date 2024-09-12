package com.example.mycollections

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object GlideUtilityFragment {
    fun setImageToImageView(context: Fragment, imageView: ImageView, filePath: String, documentID: String)
    {
        getImageAndSetImageView(context, imageView, filePath, documentID)
    }

    private fun getImageAndSetImageView(context: Fragment, imageView: ImageView, filePath: String, documentID: String)
    {
        if (filePath != "noImage")
        {
            try{
                getBitmap(context, imageView, filePath)
            }
            catch (exception: Exception) {
                getImageFromFirebaseStorage(context, imageView, documentID)
            }
        }
        else
        {
            imageView.setImageResource(R.drawable.image)
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