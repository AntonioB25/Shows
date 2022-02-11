package com.shows_antonio_bukovac

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_antonio_bukovac.databinding.DialogAddReviewBinding
import com.shows_antonio_bukovac.databinding.FragmentShowDetailsBinding
import com.shows_antonio_bukovac.model.PrefsConstants
import com.shows_antonio_bukovac.model.Review
import com.shows_antonio_bukovac.model.ReviewRequest
import com.shows_antonio_bukovac.model.Show
import com.shows_antonio_bukovac.view_models.ReviewsViewModel
import com.shows_antonio_bukovac.view_models.ShowDetailsViewModel
import kotlin.properties.Delegates


class ShowDetailsFragment : Fragment() {

    private var show: Show? = null

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val showDetailsViewModel: ShowDetailsViewModel by viewModels()
    private val reviewsViewModel: ReviewsViewModel by viewModels()

    private var prefs: SharedPreferences? = null
    private var adapter: ReviewsAdapter? = null
    private var showId by Delegates.notNull<Int>()

    private val args: ShowDetailsFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showId = args.showId

        initDetails()
        initBackButton()
        initAddReviewButton()
    }

    private fun initDetails() {
        showDetailsViewModel.getShowDetailsLiveData(showId)
            .observe(this.viewLifecycleOwner) { showDetails ->
                show = showDetails

                reviewsViewModel.getReviewsLiveData(showId)
                    .observe(this.viewLifecycleOwner) { reviews ->
                        initRecyclerView(reviews)
                        showShowDetails(show, reviews)
                    }
            }
    }


    private fun initBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            ShowDetailsFragmentDirections.actionShowDetailsToShows()
                .also { findNavController().navigate(it) }
        }
    }

    private fun initAddReviewButton() {
        binding.detailsAddReviewButton.setOnClickListener {
            showBottomSheet()
        }
    }


    private fun showShowDetails(show: Show?, reviewsList: List<Review>) {

        if (show != null) {
            binding.apply {
                collapsingToolbarLayout.title = show.title
                showDescription.text = show.description
                toolbar.title = show.title
                (context?.let { Glide.with(it).load(show.imageUrl).into(showImage) })
            }

            if (reviewsList.isEmpty()) {
                binding.showDetailsRatingBar.visibility = View.INVISIBLE
            } else {
                binding.apply {
                    totalRatings.text =
                        getString(R.string.review_average_text).format(
                            show.noOfReviews,
                            show.averageRating
                        )
                    showDetailsRatingBar.visibility = View.VISIBLE
                    showDetailsRatingBar.rating = show.averageRating.toFloat()
                }
            }
        }
    }


    private fun initRecyclerView(reviewsList: List<Review>) {
        binding.reviewsRecycler.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)


        binding.reviewsRecycler.addItemDecoration(
            DividerItemDecoration(
                binding.reviewsRecycler.context,
                1
            )
        )
        adapter = ReviewsAdapter(reviewsList)
        binding.reviewsRecycler.adapter = adapter

    }

    private fun showBottomSheet() {
        var dialog = this.context?.let { BottomSheetDialog(it) }

        val dialogBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog?.setContentView(dialogBinding.root)

        dialogBinding.dialogRatingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (rating.toDouble() == 0.0) {
                dialogBinding.dialogSubmitButton.isEnabled = false
                dialogBinding.dialogSubmitButton.isClickable = false
                dialogBinding.dialogSubmitButton.setBackgroundColor(Color.parseColor("#876996"))
            } else {
                dialogBinding.dialogSubmitButton.isEnabled = true
                dialogBinding.dialogSubmitButton.isClickable = true
                dialogBinding.dialogSubmitButton.setBackgroundColor(Color.parseColor("#3D1D72"))
            }
        }

        dialogBinding?.dialogSubmitButton?.setOnClickListener {
            val reviewRequest = ReviewRequest(
                dialogBinding.dialogRatingBar.rating.toInt(),
                dialogBinding.dialogComment.text.toString(),
                showId
            )

            reviewsViewModel.addReview(reviewRequest)

            reviewsViewModel.getReviewLiveData().observe(this.viewLifecycleOwner) { review ->
                adapter?.addReview(review)
            }
            dialog?.dismiss()
        }
        dialog?.show()
    }

//    private fun refreshRating() {
//
//        showDetailsViewModel.getShowDetailsLiveData(showId)
//            .observe(this.viewLifecycleOwner) { showDetails ->
//                binding.totalRatings.text = getString(R.string.reviewAverageText).format(
//                    showDetails.noOfReviews,
//                    showDetails.averageRating
//                )
//            }
//    }

    private fun getUsername(): String? {
        return prefs?.getString(PrefsConstants.USER_EMAIL, PrefsConstants.NO_USER_EMAIL)?.substringBefore("@")
    }
}
