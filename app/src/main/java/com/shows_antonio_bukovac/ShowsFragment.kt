package com.shows_antonio_bukovac

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shows_antonio_bukovac.databinding.DialogProfileDetailsBinding
import com.shows_antonio_bukovac.databinding.FragmentShowsBinding
import com.shows_antonio_bukovac.model.PrefsConstants
import com.shows_antonio_bukovac.model.Show
import com.shows_antonio_bukovac.view_models.ShowsViewModel
import com.shows_antonio_bukovac.view_models.UserViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ShowsFragment : Fragment() {

    private val showsViewModel: ShowsViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private var _binding: FragmentShowsBinding? = null
    private val binding get() = _binding!!
    private var dialog: BottomSheetDialog? = null
    private lateinit var dialogBinding: DialogProfileDetailsBinding

    private var prefs: SharedPreferences? = null

    private var imageFile: File? = null
    private var avatarUri: Uri? = null
    private var takeImageResult: ActivityResultLauncher<Uri>? = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            setPicture()
        }
    }

    private val cameraPermissionForTakingPictures =
        preparePermissionsContract(onPermissionsGranted = {
            takePicture()
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog =
                ?.let { BottomSheetDialog(it) }
        dialogBinding = DialogProfileDetailsBinding.inflate(layoutInflater)
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = activity?.getPreferences(Context.MODE_PRIVATE)

        showsViewModel.getShowsLiveData().observe(this.viewLifecycleOwner, { shows ->
            if (shows.isEmpty()) {
                binding.noShowsLayout.isVisible = true
                binding.showsRecycler.isVisible = false
            } else {
                binding.noShowsLayout.isVisible = false
                binding.showsRecycler.isVisible = true
                context?.let { initRecyclerView(shows, it) }
            }

        })

        initProfile()
        initProfileButton()
    }

    /**
     * Check if there is already file for profile picture, if there is, set it
     * If there isn't, do nothing
     */
    private fun initProfile() {
        dialogBinding.userMail.text = getUserMail()

        var imageFile = FileUtil.getImageFile(context)

        if (imageFile != null) {
            avatarUri = context?.let { context ->
                FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".fileprovider",
                    imageFile
                )
            }

            context?.let { Glide.with(it) }?.load(avatarUri)
                ?.diskCacheStrategy(DiskCacheStrategy.NONE)
                ?.skipMemoryCache(true)?.into(binding.profileImage)
            context?.let { Glide.with(it) }?.load(avatarUri)
                ?.diskCacheStrategy(DiskCacheStrategy.NONE)
                ?.skipMemoryCache(true)?.into(dialogBinding.profileImage)
        }
    }

    private fun initRecyclerView(shows: List<Show>, context: Context) {
        binding.showsRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.showsRecycler.adapter = ShowsAdapter(shows, context) {
            ShowsFragmentDirections.actionShowsToShowDetails(it).also { action ->
                findNavController().navigate(action)
            }
        }
    }


    private fun initProfileButton() {
        binding.profileImage.setOnClickListener {
            showProfileDialog()
        }
    }


    private fun showProfileDialog() {
        initLogoutButton()
        initChangePictureButton()

        dialog?.setContentView(dialogBinding.root)
        dialog?.show()
    }

    private fun initLogoutButton() {
        dialogBinding.logoutButton.setOnClickListener {
            // TODO: Clean tokens from shared prefs
            prefs?.edit()?.apply {
                remove(PrefsConstants.HEADER_UID)
                remove(PrefsConstants.HEADER_ACCESS_TOKEN)
                remove(PrefsConstants.HEADER_CLIENT)
            }?.apply()
            initAlertMessage()
            dialog?.dismiss()
        }
    }


    private fun initAlertMessage() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout!")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Logout") { _: DialogInterface, _: Int ->
            logout()
        }
        builder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
            dialog?.dismiss()
        }
        builder.show()
    }

    private fun logout() {
        prefs?.edit()?.putBoolean(PrefsConstants.REMEMBER_ME, false)?.apply()
        ShowsFragmentDirections.actionShowsToLogin()
            .also { action -> findNavController().navigate(action) }
    }

    private fun initChangePictureButton() {
        dialogBinding.changeProfilePhotoButton.setOnClickListener {
            cameraPermissionForTakingPictures.launch(arrayOf(Manifest.permission.CAMERA))
            dialog?.dismiss()
        }
    }


    private fun takePicture() {
        imageFile = FileUtil.getImageFile(context)
        if (imageFile == null) {
            imageFile = context?.let { FileUtil.createImageFile(it) }
        }

        if (imageFile != null) {
            avatarUri = context?.let { context ->
                FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".fileprovider",
                    imageFile!!
                )
            }
        }
        takeImageResult?.launch(avatarUri)
    }


    private fun setPicture() {

        //send to server

        val file = FileUtil.getImageFile(context)

        val requestFile =
            FileUtil.getImageFile(context)?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = requestFile?.let {
            MultipartBody.Part.createFormData(
                "image_url", file?.name,
                it
            )
        }

        if (body != null) {
            userViewModel.addImage(body)
        }


        context?.let { Glide.with(it) }?.load(avatarUri)?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.skipMemoryCache(true)?.into(binding.profileImage)
        context?.let { Glide.with(it) }?.load(avatarUri)?.diskCacheStrategy(DiskCacheStrategy.NONE)
            ?.skipMemoryCache(true)?.into(dialogBinding.profileImage)
    }

    private fun getUserMail(): String {
        return prefs?.getString(PrefsConstants.USER_EMAIL, PrefsConstants.NO_USER_EMAIL).toString()
    }
}