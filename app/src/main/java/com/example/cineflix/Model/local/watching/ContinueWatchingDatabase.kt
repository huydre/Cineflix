package com.example.cineflix.Model.local.watching

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cineflix.View.Fragments.HomeFragment


@Database(entities = [ContinueWatching::class], version = 1)
abstract class ContinueWatchingDatabase : RoomDatabase() {

    abstract fun watchDAO() : ContinueWatchingDAO

    companion object {
        @Volatile
        private var INSTANCE: ContinueWatchingDatabase?= null

        fun getInstance(context : Context) : ContinueWatchingDatabase{
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContinueWatchingDatabase::class.java,
                        "continue_watching_database"
                    ).build()
                    INSTANCE =  instance
                }
                return  instance
            }
        }
    }
}