package com.example.mycollections

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.example.mycollections.CurrentUser.user
import com.example.mycollections.Utility.db
import com.example.mycollections.Utility.isNetworkAvailable
import com.example.mycollections.Utility.sendErrorToastMessage

class DataViewModel: ViewModel()
{
    private var currentAlbum = mutableListOf<CollectionData>()
    private var albumLiveData = MutableLiveData(currentAlbum)
    val album: MutableLiveData<MutableList<CollectionData>> = albumLiveData
    private val userID = user!!.id
    init{
        db.collection("user").document(userID).collection("collection").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val collectionData = Utility.makeCollectionData(document)
                    currentAlbum.add(collectionData)
                }
                currentAlbum.sortBy { it.unixTime }
                album.value = currentAlbum
            }
            .addOnFailureListener {
            }
    }

    fun addData(collectionData: CollectionData)
    {
        currentAlbum.add(collectionData)
        album.value = currentAlbum
    }

    fun removeData(collectionData: CollectionData, context: Context)
    {
        if (isNetworkAvailable(context)) {
            currentAlbum.remove(collectionData)
            album.value = currentAlbum
            db.collection("user").document(userID).collection("collection").
            document(collectionData.documentID).delete()
                .addOnFailureListener {
                    sendErrorToastMessage(context)
                }
        }
        else {
            sendErrorToastMessage(context, "네트워크 연결을 확인해주세요.")
        }
    }

    fun replaceData(oldCollectionData: CollectionData, collectionData: CollectionData)
    {
        val indexOfOldData = currentAlbum.indexOf(oldCollectionData)
        currentAlbum[indexOfOldData] = collectionData
        album.value = currentAlbum
    }
}