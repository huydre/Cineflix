package com.example.cineflix.Model.local.watching

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "continue_watching")
data class ContinueWatching (
    val progress: Long,
    val posterPath: String,
    @PrimaryKey(autoGenerate = true)
    val tmdbID: Int? = null,
    val title: String,
    val episode: Int = 0,
    val season: Int = 0,
    val media_type: String,
    val year: String? = null,
    val lastUpdate: Long = System.currentTimeMillis()
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()?:"",
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString()?:"",
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()?:"",
            parcel.readString()?:"",
            parcel.readLong()
        ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(progress)
        parcel.writeString(posterPath)
        parcel.writeValue(tmdbID)
        parcel.writeString(title)
        parcel.writeInt(episode)
        parcel.writeInt(season)
        parcel.writeString(media_type)
        parcel.writeString(year)
        parcel.writeLong(lastUpdate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContinueWatching> {
        override fun createFromParcel(parcel: Parcel): ContinueWatching {
            return ContinueWatching(parcel)
        }

        override fun newArray(size: Int): Array<ContinueWatching?> {
            return arrayOfNulls(size)
        }
    }
}

data class VideoData(
    val progress: Int,
    val posterPath: String,
    @PrimaryKey
    val tmdbID: Int,
    val imdbID: String?= null,
    val title: String,
    val episode: Int = 0,
    val season : Int = 0,
    val media_type: String,
    val year: String? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(progress)
        parcel.writeString(posterPath)
        parcel.writeInt(tmdbID)
        parcel.writeString(imdbID)
        parcel.writeString(title)
        parcel.writeInt(episode)
        parcel.writeInt(season)
        parcel.writeString(media_type)
        parcel.writeString(year)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoData> {
        override fun createFromParcel(parcel: Parcel): VideoData {
            return VideoData(parcel)
        }

        override fun newArray(size: Int): Array<VideoData?> {
            return arrayOfNulls(size)
        }
    }

}