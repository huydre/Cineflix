package com.example.cineflix.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Model.network.Movie
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity

class NowPlayingListAdapter(var lst:List<Movie>): RecyclerView.Adapter<NowPlayingListAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.textView10)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NowPlayingListAdapter.MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container_big, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: NowPlayingListAdapter.MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.poster_path}")
        holder.tileTextView.text = ""

        holder.imageView.setOnClickListener{
            val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
            intent.putExtra("movie_id", movieResult.id)
            intent.putExtra("movie_title", movieResult.title)
            intent.putExtra("movie_year", movieResult.release_date)
            intent.putExtra("movie_overview", movieResult.overview)
            intent.putExtra("movie_backdropPath", movieResult.backdrop_path)
            intent.putExtra("poster_path", movieResult.poster_path)
            holder.itemView.context.startActivity(intent)
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_movieDetailsActivity)
        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<Movie>) {
        lst = newList
        notifyDataSetChanged()
    }

}