package com.dicoding.picodiploma.loginwithanimation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryRowBinding
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem

class StoryAdapter(
    private val stories: ArrayList<ListStoryItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        stories[position].let { holder.bind(it) }
    }

    fun updateList(newList: List<ListStoryItem>) {
        val diffCallback = StoryDiffCallback(stories, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stories.clear()
        stories.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(
        private val binding: ItemStoryRowBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                storyTitle.text = story.name
                storyDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .error(R.drawable.baseline_image_24)
                    .into(eventImage)
            }
            itemView.setOnClickListener {
                story.id?.let { onItemClick(it) }
            }
        }
    }

    private class StoryDiffCallback(
        private val oldList: List<ListStoryItem>,
        private val newList: List<ListStoryItem>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}