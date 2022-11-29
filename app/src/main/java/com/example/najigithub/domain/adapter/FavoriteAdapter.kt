package com.example.najigithub.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.najigithub.databinding.ListUserItemLayoutBinding
import com.example.najigithub.domain.utils.loadImage
import com.example.najigithub.domain.utils.showView
import favoritedatabase.favoritedb.FavoriteEntity

class FavoriteAdapter: RecyclerView.Adapter<FavoriteAdapter.ViewHolderFavorite>() {

    inner class ViewHolderFavorite(private val binding: ListUserItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(favoriteEntity: FavoriteEntity) {
            with(binding) {
                ivAvatar.loadImage(favoriteEntity.IMAGE)
                tvUsername.text = favoriteEntity.NAME
                if (ivDelete.visibility == View.GONE) {
                    ivDelete.showView()
                }
                ivDelete.setOnClickListener {
                    onDeleteItemClickListener?.let { dataItem ->
                        dataItem(favoriteEntity.id)
                    }
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<FavoriteEntity>() {
        override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.ViewHolderFavorite {
        return ViewHolderFavorite(ListUserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.ViewHolderFavorite, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { dataItem ->
                dataItem(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((FavoriteEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (FavoriteEntity) -> Unit) {
        onItemClickListener = listener
    }

    private var onDeleteItemClickListener: ((Long) -> Unit)? = null

    fun setOnDeleteItemListener(listener: (Long) -> Unit){
        onDeleteItemClickListener = listener
    }

    companion object {
        fun instance() = FavoriteAdapter()
    }
}