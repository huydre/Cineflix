package com.example.cineflix.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Model.local.searchHistory.SearchHistory
import com.example.cineflix.R
import com.google.android.material.button.MaterialButton

class SearchHistoryAdapter(
    var lst:List<SearchHistory>,
    var onclick: ((SearchHistory, Boolean) -> Unit)? = null)

    : RecyclerView.Adapter<SearchHistoryAdapter.MovieViewHolder>() {
    inner class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val query : TextView = itemView.findViewById(R.id.query)
        val deleteRecord : MaterialButton = itemView.findViewById(R.id.delete_query)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_history_item_container, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val queries = lst[position]
        holder.query.text = queries.query
        holder.query.setOnClickListener {
            onclick?.invoke(queries, true)
        }

        holder.deleteRecord.setOnClickListener {
            onclick?.invoke(queries, false)
        }
    }

    override fun getItemCount(): Int {
        return lst.size;
    }

    fun setMovies(newList: List<SearchHistory>) {
        lst = newList
        notifyDataSetChanged()
    }
}