package com.example.cineflix.Model.local.playlist

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class PlayList (
    val name: String,
    val posterPath: String,
    @PrimaryKey(autoGenerate = true)
    val tmdbID: Int,
    val media_type: String,
    val timeAdd: Long = System.currentTimeMillis(),
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readLong()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(posterPath)
        parcel.writeInt(tmdbID)
        parcel.writeString(media_type)
        parcel.writeLong(timeAdd)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PlayList> {
        override fun createFromParcel(parcel: Parcel): PlayList {
            return PlayList(parcel)
        }

        override fun newArray(size: Int): Array<PlayList?> {
            return arrayOfNulls(size)
        }
    }
}