package com.permata.mystoryyapp.ui.listaddstory.utamaliststory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import com.bumptech.glide.Glide
import com.permata.mystoryyapp.R
import com.permata.mystoryyapp.databinding.ItemListStoryBinding
import com.permata.mystoryyapp.network.response.ListStoryItem

class ListStoryAdapter :
    PagingDataAdapter<ListStoryItem, ListStoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListStoryBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val storyItem = getItem(position)
        storyItem?.let { holder.bind(it) }
    }

    class MyViewHolder(private val binding: ItemListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.tvName.text = item.name

            Glide.with(binding.root.context)
                .load(item.photoUrl)
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_broken_image)
                .into(binding.imgArticle)

            binding.root.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("EXTRA_ID", item.id)
                    putString("EXTRA_NAME", item.name)
                    putString("EXTRA_PHOTO_URL", item.photoUrl)
                    putString("EXTRA_DESCRIPTION", item.description)
                }
                it.findNavController().navigate(R.id.action_liststoryFragment_to_detailFragment, bundle)
            }
        }
    }

    companion object {
        @JvmStatic
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem == newItem
        }
    }
}
