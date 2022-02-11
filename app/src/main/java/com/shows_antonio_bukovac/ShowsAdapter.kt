package com.shows_antonio_bukovac

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shows_antonio_bukovac.databinding.ViewShowItemBinding
import com.shows_antonio_bukovac.model.Show

class ShowsAdapter(
    private var items: List<Show>,
    private val context: Context,
    private val onClickCallback: (Int) -> Unit

) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ViewShowItemBinding.inflate(LayoutInflater.from(parent.context))

        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(shows: List<Show>) {
        items = shows
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ViewShowItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Show) {

            binding.apply {
                showName.text = item.title
                showDescription.text = item.description
                Glide.with(context).load(item.imageUrl).into(showImage)
                root.setOnClickListener {
                    onClickCallback(item.id)
                }
            }
        }
    }
}