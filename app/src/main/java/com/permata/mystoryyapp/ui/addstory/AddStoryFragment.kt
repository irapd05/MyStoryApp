package com.permata.mystoryyapp.ui.addstory

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.FragmentAddstoryBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddstoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddStoryViewModel by viewModels()

    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private val locationManager by lazy { requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), getString(R.string.permission_granted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), getString(R.string.permission_granted), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun allLocationPermissionsGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddstoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        if (!allLocationPermissionsGranted()) {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setupObservers()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            lifecycleScope.launch {
                uploadImage()
            }
        }

        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCurrentLocation()
                Toast.makeText(requireContext(), getString(R.string.location_enabled), Toast.LENGTH_SHORT).show()
            } else {
                currentLocation = null
                Toast.makeText(requireContext(), getString(R.string.location_disabled), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is UploadStatus.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    navigateToStoryList()
                }
                is UploadStatus.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    showToast(status.message)
                }
                is UploadStatus.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherCamera.launch(currentImageUri!!)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun getCurrentLocation() {
        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                currentLocation = location
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }


    private suspend fun uploadImage() {
        val description = binding.storyEditText.text.toString()

        if (description.isBlank()) {
            showToast(getString(R.string.description_required))
            return
        }

        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext())
            val compressedImageFile = imageFile.reduceFileImage()

            val latRequestBody = currentLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = currentLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())

            viewModel.uploadStory(compressedImageFile, description, getToken(), latRequestBody, lonRequestBody)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null)
    }

    private fun navigateToStoryList() {
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
        navController.navigate(
            R.id.navigation_liststory,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_addstory, true)
                .build()
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
