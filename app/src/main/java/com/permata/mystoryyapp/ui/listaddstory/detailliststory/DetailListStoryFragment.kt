package com.permata.mystoryyapp.ui.listaddstory.detailliststory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.FragmentDetailListStoryBinding

class DetailListStoryFragment : Fragment() {

    private var _binding: FragmentDetailListStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailListStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = DetailListStoryViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[DetailListStoryViewModel::class.java]

        observeViewModel()

        val storyId = arguments?.getString(EXTRA_ID) ?: ""
        if (storyId.isNotEmpty()) {
            val token = getTokenFromSharedPreferences()
            fetchStoryDetails(token, storyId)
        } else {
            Toast.makeText(context, "Story ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchStoryDetails(token: String, storyId: String) {
        displayArgumentsData()

        viewModel.getStoryDetail(token, storyId)
    }

    private fun displayArgumentsData() {
        val storyName = arguments?.getString(EXTRA_NAME)
        val storyDescription = arguments?.getString(EXTRA_DESCRIPTION)
        val storyPhotoUrl = arguments?.getString(EXTRA_PHOTO_URL)

        binding.namaUser.text = storyName
        binding.Deskripsi.text = storyDescription
        if (!storyPhotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(storyPhotoUrl)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.baseline_error_24)
                .into(binding.imgdetailstorylist)
        }
    }

    private fun observeViewModel() {
        viewModel.story.observe(viewLifecycleOwner) { story ->
            story?.let {
                binding.namaUser.text = it.name
                binding.Deskripsi.text = it.description
                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.baseline_error_24)
                    .into(binding.imgdetailstorylist)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return "Bearer ${sharedPreferences.getString("TOKEN", "")}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_NAME = "EXTRA_NAME"
        const val EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION"
        const val EXTRA_PHOTO_URL = "EXTRA_PHOTO_URL"
    }
}
