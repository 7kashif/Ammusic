package com.kashif.ammusic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VideoModel::class],version = 2,exportSchema = false)
abstract class VideoDatabase:RoomDatabase() {
    abstract val videoDatabaseDao : VideoDatabaseDao

    companion object {
        private var INSTANCE:VideoDatabase?=null
        fun getInstance(context: Context):VideoDatabase {
            var instance = INSTANCE
            if(instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoDatabase::class.java,
                    "videos_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
            }
            return  instance
        }
    }

}