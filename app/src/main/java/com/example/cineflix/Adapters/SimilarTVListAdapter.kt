package com.example.cineflix.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Model.Movie
import com.example.cineflix.Model.TV
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity

class SimilarTVListAdapter(var lst:List<TV>): RecyclerView.Adapter<SimilarTVListAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarTVListAdapter.MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    override fun onBindViewHolder(holder: SimilarTVListAdapter.MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.poster_path}")

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
            intent.putExtra("tv_id", movieResult.id)
            intent.putExtra("tv_title", movieResult.name)
            intent.putExtra("tv_year", movieResult.first_air_date)
            intent.putExtra("tv_overview", movieResult.overview)
            holder.itemView.context.startActivity(intent)
            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_movieDetailsActivity)
        }
    }

    fun setMovies(newList: List<TV>) {
        lst = newList
        notifyDataSetChanged()
    }
}