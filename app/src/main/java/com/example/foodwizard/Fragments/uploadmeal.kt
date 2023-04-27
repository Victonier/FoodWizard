package com.example.foodwizard.Fragments

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.foodwizard.DB.Diet
import com.example.foodwizard.Diet.DietRecognition
import com.example.foodwizard.R
import com.example.foodwizard.Util.Constants.TEMP_IMAGE_URL
import com.example.foodwizard.Util.PictureUtils.Companion.getScaledBitmap
import com.example.foodwizard.databinding.FragmentRecordBinding
import com.example.foodwizard.databinding.FragmentUploadmealBinding
import com.example.foodwizard.viewModel.UsersViewModel
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class uploadmeal : DialogFragment() {
    private var _binding: FragmentUploadmealBinding? = null
    private var photoName:String? = null
    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
            // Handle the result
        if (didTakePhoto && photoName != null) {
            //add meal's photoname to dataset
            Log.d("photo", photoName!!)
            val photoURL = TEMP_IMAGE_URL
            photoURL?.let { recognizeDiet(it) }
            updatePhoto(photoName)


        }
    }
    private val binding
       get() = checkNotNull(_binding) {
           "Cannot access binding because it is null. Is the view visible?"
       }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        _binding = FragmentUploadmealBinding.inflate(inflater, container, false)
        binding.upload.setOnClickListener()
        {
            photoName = "IMG_${Date()}.JPG"
            val photoFile = File(requireContext().applicationContext.filesDir,
                photoName)
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.foodwizard.fileprovider",
                photoFile
            )
            takePhoto.launch(photoUri)
        }
        /*
        binding.apply{
            val captureImageIntent = takePhoto.contract.createIntent(
                requireContext(),
                null
            )
            upload.isEnabled = canResolveIntent(captureImageIntent)
        }
        */
        return binding.root

    }
    private fun canResolveIntent(intent: Intent): Boolean {
        intent.addCategory(Intent.CATEGORY_HOME)
        val packageManager: PackageManager = requireActivity().packageManager
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.food.tag!=photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.food.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.food.setImageBitmap(scaledBitmap)
                    binding.food.tag = photoFileName
                }
            } else {
                Log.d("da","none exist")
                binding.food.setImageBitmap(null)
                binding.food.tag = null
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun recognizeDiet(photoURL : String) {
        GlobalScope.launch(Dispatchers.IO) {
            val usersViewModel: UsersViewModel by viewModels()
            DietRecognition(usersViewModel).recognizeDiet(photoURL)
            withContext(Dispatchers.Main) {

            }
        }
    }
}