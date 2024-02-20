package com.example.cineflix.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.R
import coil.load
import com.example.cineflix.Model.Movie

class PopularListAdapter(var lst:List<Movie>): RecyclerView.Adapter<PopularListAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.textView10)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        Log.d(TAG, "onBindViewHolder: " + lst)
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w500/${movieResult.poster_path}")
        holder.tileTextView.text = ""
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<Movie>) {
        lst = newList
        notifyDataSetChanged()
    }
}