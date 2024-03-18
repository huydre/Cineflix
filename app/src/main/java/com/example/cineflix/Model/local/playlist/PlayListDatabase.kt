package com.example.cineflix.Model.local.playlist

import androidx.room.Database
import androidx.room.Room
import android.content.Context
import androidx.room.RoomDatabase


@Database(entities = [PlayList::class], version = 1)
abstract class PlayListDatabase : RoomDatabase(){
    abstract fun playListDao() : PlayListDao

    companion object{

        @Volatile
        private var INSTANCE: PlayListDatabase? = null

        fun getInstance(context: Context) : PlayListDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlayListDatabase::class.java,
                        "play_list_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }


}