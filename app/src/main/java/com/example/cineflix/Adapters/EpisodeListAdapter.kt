package com.example.cineflix.Adapters

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Model.EpisodeX
import com.example.cineflix.Model.TV
import com.example.cineflix.R
import com.example.cineflix.View.Activities.TvDetailsActivity

class EpisodeListAdapter (var lst:List<EpisodeX>): RecyclerView.Adapter<EpisodeListAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.name)
        val durationTV: TextView = itemView.findViewById(R.id.duration)
        val overviewTV: TextView = itemView.findViewById(R.id.overview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.episode_item_container, parent, false)
        return MovieViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.still_path}")
        holder.tileTextView.text = "${movieResult.episode_number}.  ${movieResult.name}"
        holder.durationTV.text = "${movieResult.runtime.toString()}m"
        holder.overviewTV.text = movieResult.overview


        holder.itemView.setOnClickListener{
//            val intent = Intent(holder.itemView.context, TvDetailsActivity::class.java)
//            intent.putExtra("tv_id", movieResult.id)
//            intent.putExtra("tv_title", movieResult.name)
//            intent.putExtra("tv_year", movieResult.first_air_date)
//            intent.putExtra("tv_overview", movieResult.overview)
//            intent.putExtra("tv_backdrop", movieResult.backdrop_path)
//            holder.itemView.context.startActivity(intent)
//            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_tvDetailsActivity)
        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<EpisodeX>) {
        lst = newList
        notifyDataSetChanged()
    }
}