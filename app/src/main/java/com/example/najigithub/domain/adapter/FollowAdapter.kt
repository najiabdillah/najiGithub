package com.example.najigithub.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.najigithub.data.remote.dto.FollowResponse
import com.example.najigithub.databinding.ListUserItemLayoutBinding
import com.example.najigithub.domain.utils.loadImage

class FollowAdapter: RecyclerView.Adapter<FollowAdapter.ViewHolderFollowing>() {

    inner class ViewHolderFollowing(private val binding: ListUserItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(followItem: FollowResponse) {
            with(binding) {
                ivAvatar.loadImage(followItem.avatarUrl)
                tvUsername.text = followItem.login
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<FollowResponse>() {
        override fun areItemsTheSame(oldItem: FollowResponse, newItem: FollowResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FollowResponse, newItem: FollowResponse): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderFollowing {
        return ViewHolderFollowing(ListUserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderFollowing, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    companion object {
        fun instance() = FollowAdapter()
    }
}