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
import com.example.cineflix.Model.Movie
import com.example.cineflix.Model.local.watching.ContinueWatching
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity
import com.example.cineflix.View.Activities.MoviePlayerActivity

class ContinueWatchingListAdapter(var lst:List<ContinueWatching>): RecyclerView.Adapter<ContinueWatchingListAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.textviewCWatching)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContinueWatchingListAdapter.MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_item_cw_contaner, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContinueWatchingListAdapter.MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.posterPath}")

        if (movieResult.media_type == "movie") {
            holder.tileTextView.text = movieResult.title
            holder.imageView.setOnClickListener {
                val intent = Intent(holder.itemView.context, MoviePlayerActivity::class.java)
                intent.putExtra("movie_id", movieResult.tmdbID)
                intent.putExtra("media_type", "movie")
                intent.putExtra("title", movieResult.title)
                intent.putExtra("poster_path", movieResult.posterPath)
                intent.putExtra("progress", movieResult.progress)
                holder.itemView.context.startActivity(intent)
            }
        }
        else {
            holder.tileTextView.text = "M${movieResult.season}:T${movieResult.episode} ${movieResult.title}"
            holder.imageView.setOnClickListener {
                val intent = Intent(holder.itemView.context, MoviePlayerActivity::class.java)
                intent.putExtra("movie_id", movieResult.tmdbID)
                intent.putExtra("media_type", "tv")
                intent.putExtra("title", movieResult.title)
                intent.putExtra("poster_path", movieResult.posterPath)
                intent.putExtra("progress", movieResult.progress)
                intent.putExtra("season", movieResult.season)
                intent.putExtra("episode", movieResult.episode)
                holder.itemView.context.startActivity(intent)
            }
        }

//        holder.itemView.setOnClickListener{
//            val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
//            intent.putExtra("movie_id", movieResult.id)
//            intent.putExtra("movie_title", movieResult.title)
//            intent.putExtra("movie_year", movieResult.release_date)
//            intent.putExtra("movie_overview", movieResult.overview)
//            intent.putExtra("movie_backdropPath", movieResult.backdrop_path)
//            holder.itemView.context.startActivity(intent)
//            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_movieDetailsActivity)
//        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<ContinueWatching>) {
        lst = newList
        notifyDataSetChanged()
    }

}