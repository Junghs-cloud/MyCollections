package com.example.mycollections

import android.graphics.Bitmap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Utility {
    val db = Firebase.firestore
    val auth = Firebase.auth
    fun cropToSquare(bitmap: Bitmap?): Bitmap {
        requireNotNull(bitmap) { "Bitmap cannot be null" }
        val width = bitmap.width
        val height = bitmap.height

        val newSize = width.coerceAtMost(height)

        val x = (width - newSize) / 2
        val y = (height - newSize) / 6

        return Bitmap.createBitmap(bitmap, x, y, newSize, newSize)
    }
}