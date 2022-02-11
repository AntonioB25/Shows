package com.shows_antonio_bukovac

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.addListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shows_antonio_bukovac.databinding.FragmentSplashBinding
import com.shows_antonio_bukovac.model.PrefsConstants

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = activity?.getPreferences(Context.MODE_PRIVATE)

        binding.root.setOnClickListener {
            SplashFragmentDirections.splashToShows().also {
                findNavController().navigate(it)
            }
        }



        val animator = ObjectAnimator.ofFloat(binding.splashTriangle, "translationY", -500f, 0f)
        animator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                val isLoggedIn = prefs?.getBoolean(PrefsConstants.REMEMBER_ME, false)
                Log.v("CHECKED", isLoggedIn.toString())
                if(isLoggedIn == true){
                    SplashFragmentDirections.splashToShows().also {
                        findNavController().navigate(it)
                    }
                }else{
                    SplashFragmentDirections.splashToLogin().also {
                        findNavController().navigate(it)
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
        animator.apply {
            duration = 2000
            interpolator = BounceInterpolator()
            start()
        }

        val titleAnimator =
            ObjectAnimator.ofFloat(binding.splashShowsTitle, "translationX", -100f, 0f)
        titleAnimator.apply {
            duration = 2000
            interpolator = DecelerateInterpolator(3.0f)
            start()
        }
    }

}