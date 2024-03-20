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
import com.example.cineflix.Model.network.TV
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity
import com.example.cineflix.View.Activities.TvDetailsActivity

class TVAdapter (var lst:List<TV>): RecyclerView.Adapter<TVAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.poster_path}")
        holder.tileTextView.text = movieResult.name

        holder.imageView.setOnClickListener{
            val intent = Intent(holder.itemView.context, TvDetailsActivity::class.java)
            intent.putExtra("movie_id", movieResult.id)
            intent.putExtra("tv_title", movieResult.name)
            intent.putExtra("tv_year", movieResult.first_air_date)
            intent.putExtra("tv_overview", movieResult.overview)
            intent.putExtra("tv_backdrop", movieResult.backdrop_path)
            intent.putExtra("poster_path", movieResult.poster_path)
            holder.itemView.context.startActivity(intent)
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_tvDetailsActivity)
        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<TV>) {
        lst += newList
        notifyDataSetChanged()
    }
}