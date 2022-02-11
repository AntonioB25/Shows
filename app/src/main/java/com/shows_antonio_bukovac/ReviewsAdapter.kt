package com.shows_antonio_bukovac

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shows_antonio_bukovac.databinding.ViewReviewItemBinding
import com.shows_antonio_bukovac.model.Review


class ReviewsAdapter(
    private var reviews: List<Review>,
) : RecyclerView.Adapter<ReviewsAdapter.ShowReviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowReviewHolder {
        val binding = ViewReviewItemBinding.inflate(LayoutInflater.from(parent.context))

        return ShowReviewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowReviewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    //Add new rating
    fun addReview(review: Review) {
        reviews = reviews + review
        notifyItemInserted(reviews.lastIndex)
    }

    inner class ShowReviewHolder(private val binding: ViewReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.numericGrade.text = review.rating.toString()
            binding.reviewComment.text = review.comment
            binding.reviewerUsername.text = review.user.email
        }
    }
}