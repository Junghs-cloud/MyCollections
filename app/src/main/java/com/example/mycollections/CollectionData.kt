package com.example.mycollections

import android.os.Parcel
import android.os.Parcelable

data class CollectionData(
    val collectionCategory: String,
    val cost: String,
    val documentID: String,
    val filePath: String,
    val memo: String,
    val name: String,
    val ownCategory: String,
    val releaseDate: String,
    val unixTime: Long
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(collectionCategory)
        parcel.writeString(cost)
        parcel.writeString(documentID)
        parcel.writeString(filePath)
        parcel.writeString(memo)
        parcel.writeString(name)
        parcel.writeString(ownCategory)
        parcel.writeString(releaseDate)
        parcel.writeLong(unixTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollectionData> {
        override fun createFromParcel(parcel: Parcel): CollectionData {
            return CollectionData(parcel)
        }

        override fun newArray(size: Int): Array<CollectionData?> {
            return arrayOfNulls(size)
        }
    }
}