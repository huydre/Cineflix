package com.example.cineflix.Model.local.searchHistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "search_history",indices = [Index(value = ["query"], unique = true)])
data class SearchHistory (
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0 ,
    @ColumnInfo(name = "query")
    val query:String = "",
)