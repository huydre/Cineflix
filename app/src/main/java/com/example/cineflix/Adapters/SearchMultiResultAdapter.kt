package com.example.cineflix.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.layout.Layout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Model.Movie
import com.example.cineflix.Model.SearchMulti
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity
import com.example.cineflix.View.Fragments.SearchFragment

class SearchMultiResultAdapter(var lst:List<SearchMulti>): RecyclerView.Adapter<SearchMultiResultAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMultiResultAdapter.MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_grid_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.poster_path}")

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java)
            if (movieResult.media_type == "movie") {
                intent.putExtra("movie_id", movieResult.id)
                intent.putExtra("movie_title", movieResult.title)
                intent.putExtra("movie_year", movieResult.release_date)
                intent.putExtra("movie_overview", movieResult.overview)
                holder.itemView.context.startActivity(intent)
                Navigation.createNavigateOnClickListener(R.id.action_searchFragment_to_movieDetailsActivity)
            }
            else {
                Toast.makeText(holder.itemView.context, "Phần này đang được phát triển", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<SearchMulti>) {
        lst = newList
        notifyDataSetChanged()
    }
}