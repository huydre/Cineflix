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
import com.example.cineflix.Model.local.playlist.PlayList
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity
import com.example.cineflix.View.Activities.TvDetailsActivity

class PlayListAdapter (var lst:List<PlayList>): RecyclerView.Adapter<PlayListAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.backdropPL)
        val tileTextView : TextView = itemView.findViewById(R.id.titlePL)
        val playBtn : ImageView = itemView.findViewById(R.id.playPL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.play_list_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.posterPath}")
        holder.tileTextView.text = movieResult.name

        holder.itemView.setOnClickListener {
            if (movieResult.media_type == "movie") {
                val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
                intent.putExtra("movie_id", movieResult.tmdbID)
                holder.itemView.context.startActivity(intent)
            }
            else {
                val intent = Intent(holder.itemView.context, TvDetailsActivity::class.java)
                intent.putExtra("movie_id", movieResult.tmdbID)
                holder.itemView.context.startActivity(intent)
                Navigation.createNavigateOnClickListener(R.id.action_searchFragment_to_tvDetailsActivity)
            }

        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<PlayList>) {
        lst = newList
        notifyDataSetChanged()
    }
}