package com.kashif.ammusic.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "videos_table")
@Parcelize
data class VideoModel (
    @PrimaryKey(autoGenerate = true)
    var videoPrimaryKey:Long=0L,

    @ColumnInfo(name="videoId")
    var videoId : String,

    @ColumnInfo(name = "title")
    var videoTitle:String
):Parcelable