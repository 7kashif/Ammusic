package com.kashif.ammusic.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kashif.ammusic.adapters.VideoAdapter
import com.kashif.ammusic.database.VideoDatabase
import com.kashif.ammusic.databinding.VideoFragmentBinding
import com.kashif.ammusic.viewModel.SharedViewModel
import com.kashif.ammusic.viewModel.SharedViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback

class VideoFragment : Fragment() {
    private lateinit var binding: VideoFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private val videoAdapter = VideoAdapter()
    private val args :VideoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = VideoFragmentBinding.inflate(inflater)
        initViewModel()
        loadVideo(args.video.videoId)
        setViews()
        addClickListeners()
        addObservers()

        return binding.root
    }

    private fun initViewModel() {
        val application = requireNotNull(this.activity).application
        val database = VideoDatabase.getInstance(application).videoDatabaseDao

        sharedViewModel =
            ViewModelProvider(
                this,
                SharedViewModelFactory(database, application)
            )[SharedViewModel::class.java]
    }

    private fun addObservers() {
        sharedViewModel.allVideos.observe(viewLifecycleOwner,{
            videoAdapter.submitList(it)
        })
    }

    private fun addClickListeners() {
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }
            ibPlayList.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        videoAdapter.onVideoItemClickListener {
            binding.tvTitle.text = it.videoTitle
            binding.youtubePlayer.getYouTubePlayerWhenReady(object : YouTubePlayerCallback{
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(it.videoId,0.0f)
                }
            })
        }
    }

    private fun setViews() {
        binding.apply {
            tvTitle.text = args.video.videoTitle
            youtubePlayer.enableBackgroundPlayback(true)
            rvVideos.apply {
                adapter = videoAdapter
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)
            }
        }
    }

    private fun loadVideo(videoId:String) {
        binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                youTubePlayer.loadVideo(videoId,0.0f)
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                super.onStateChange(youTubePlayer, state)
                if(state == PlayerConstants.PlayerState.ENDED)
                    youTubePlayer.loadVideo(videoId,0.0f)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayer.release()
    }

}