package com.example.najigithub.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.najigithub.data.remote.dto.SearchItem
import com.example.najigithub.databinding.ListUserItemLayoutBinding
import com.example.najigithub.domain.utils.loadImage


class SearchAdapter: RecyclerView.Adapter<SearchAdapter.ViewHolderSearch>() {

    inner class ViewHolderSearch(val binding: ListUserItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(searchItem: SearchItem) {
            with(binding) {
                ivAvatar.loadImage(searchItem.avatarUrl)
                tvUsername.text = searchItem.login
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<SearchItem>() {
        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSearch {
        return ViewHolderSearch(ListUserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderSearch, position: Int) {
        val item = differ.currentList[position]
        with(holder) {
            bind(item)
            itemView.setOnClickListener {
                onItemClickListener?.let { dataItem ->
                    dataItem(item)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((SearchItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (SearchItem) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        fun instance() = SearchAdapter()
    }
}