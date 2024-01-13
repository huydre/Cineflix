package com.example.cineflix.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Model.MovieModel
import com.example.cineflix.R
import coil.load

class MovieListAdapter(var lst:List<MovieModel>): RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.textView10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.imageView.load("https://media.themoviedb.org/t/p/w220_and_h330_face/46sp1Z9b2PPTgCMyA87g9aTLUXi.jpg")
        holder.tileTextView.text = "Movie 1"
    }

    override fun getItemCount(): Int {
        return lst.size;
    }
}