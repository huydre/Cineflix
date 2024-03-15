package com.example.cineflix.Model.local.searchHistory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchHistory::class], version = 3)
abstract class SearchDatabase : RoomDatabase(){

    abstract fun searchDao() : SearchHistoryDao

    companion object{

        @Volatile
        private var INSTANCE: SearchDatabase? = null

        fun getInstance(context : Context) : SearchDatabase{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SearchDatabase::class.java,
                        "search_history_database"
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}