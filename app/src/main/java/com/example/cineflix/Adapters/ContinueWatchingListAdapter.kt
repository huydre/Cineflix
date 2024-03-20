package com.example.cineflix.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Model.local.watching.ContinueWatching
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieDetailsActivity
import com.example.cineflix.View.Activities.MoviePlayerActivity
import com.example.cineflix.View.Activities.TvDetailsActivity
import com.example.cineflix.ViewModel.ContinueWatchingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class ContinueWatchingListAdapter(
    var lst:List<ContinueWatching>,
    private val viewModel: ContinueWatchingViewModel
    ): RecyclerView.Adapter<ContinueWatchingListAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imagePoster)
        val tileTextView : TextView = itemView.findViewById(R.id.textviewCWatching)
        val moreBtn : ImageView = itemView.findViewById(R.id.more_btn)
        val infoBtn : ImageView = itemView.findViewById(R.id.info_btn)
        val processWatched : ProgressBar = itemView.findViewById(R.id.progressBar)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContinueWatchingListAdapter.MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slide_item_cw_contaner, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContinueWatchingListAdapter.MovieViewHolder, position: Int) {
        val movieResult = lst[position]
        holder.imageView.load("https://media.themoviedb.org/t/p/w780/${movieResult.posterPath}")

        holder.infoBtn.setOnClickListener{
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

        holder.moreBtn.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
            val bottomSheetView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.bottom_sheet_media_details, null)
            bottomSheetDialog.setContentView(bottomSheetView)

            val title = bottomSheetView.findViewById<TextView>(R.id.title_text)
            title.text = movieResult.title

            val closeIcon = bottomSheetView.findViewById<ImageView>(R.id.close_icon)
            closeIcon.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            val detailsBtn = bottomSheetView.findViewById<LinearLayout>(R.id.details_button)
            detailsBtn.setOnClickListener {
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
                bottomSheetDialog.dismiss()
            }

            val removeBtn = bottomSheetView.findViewById<LinearLayout>(R.id.remove_button)
            removeBtn.setOnClickListener {
                viewModel.deleteMovie(movieResult)

                // Remove the movie from the list and notify the adapter
                lst = lst.filter { it != movieResult }
                notifyDataSetChanged()

                // Dismiss the bottom sheet dialog
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }

        holder.processWatched.max = movieResult.duration.toInt()
        holder.processWatched.progress = movieResult.progress.toInt()

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
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<ContinueWatching>) {
        lst = newList
        notifyDataSetChanged()
    }

}