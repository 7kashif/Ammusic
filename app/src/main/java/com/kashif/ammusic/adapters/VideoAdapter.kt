package com.kashif.ammusic.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.kashif.ammusic.database.VideoModel
import com.kashif.ammusic.databinding.VideoListItemBinding

class VideoAdapter : ListAdapter<VideoModel, VideoAdapter.VideoViewHolder>(diffCallBack) {

    inner class VideoViewHolder(val binding: VideoListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<VideoModel>() {
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem.videoPrimaryKey == newItem.videoPrimaryKey
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder(
            VideoListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun getVideoItem(position:Int):VideoModel {
        return this.currentList[position]
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            holder.itemView.apply {
                ivVideoThumbnail.load("https://i.ytimg.com/vi/${item.videoId}/0.jpg")
                tvTitle.text = item.videoTitle
                root.setOnClickListener{
                    videoClickListener?.let {
                        it(item)
                    }
                }
            }
        }
    }



    private var videoClickListener:((VideoModel)->Unit)?=null
    fun onVideoItemClickListener(listener:(VideoModel)->Unit) {
        videoClickListener = listener
    }
}