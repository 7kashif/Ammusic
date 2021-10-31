package com.kashif.ammusic.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoDatabaseDao {
    @Insert
    fun addVideo(video: VideoModel)

    @Query("SELECT * FROM videos_table ORDER BY videoPrimaryKey DESC")
    fun getAllVideos(): LiveData<List<VideoModel>>

    @Query("DELETE FROM videos_table")
    fun deleteAllVideos()

    @Query("DELETE FROM videos_table WHERE videoPrimaryKey=:key")
    fun deleterSingleVideo(key: Long)

    @Query("SELECT * FROM videos_table WHERE videoId=:id")
    fun getVideoWithVideoId(id: String):VideoModel?
}