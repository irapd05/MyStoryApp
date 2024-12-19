package com.permata.mystoryyapp.ui.listaddstory.utamaliststory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.adapter.LoadingStateAdapter
import com.permata.mystoryyapp.databinding.FragmentListstoryBinding

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListstoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListStoryAdapter

    private val viewModel: ListViewModel by viewModels {
        ListViewModelFactory(requireContext())
    }

    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListstoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListStoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        binding.recyclerView.setHasFixedSize(true)

        observeViewModel()

        val token = getLoginSession()
        if (token != null) {
            viewModel.fetchStories(token)
        } else {
            Toast.makeText(context, getString(R.string.notlogin), Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.story.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.errorStatus.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it) {
                    is Resource.Success -> {
                        if (isFirstLoad) {
                            Toast.makeText(context, getString(R.string.data_loaded_success), Toast.LENGTH_SHORT).show()
                            isFirstLoad = false
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun getLoginSession(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TOKEN", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
