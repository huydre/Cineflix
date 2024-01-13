package com.example.cineflix.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.MovieListAdapter
import com.example.cineflix.Model.MovieModel
import com.example.cineflix.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lst = mutableListOf<MovieModel>()
        lst.add(MovieModel("Movie 1", "en"))
        lst.add(MovieModel("Movie 1", "en"))
        lst.add(MovieModel("Movie 1", "en"))

        val adapterView1 = MovieListAdapter(lst)
        val view1 = findViewById<RecyclerView>(R.id.view1)
        view1.adapter = adapterView1
        view1.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
    }
}