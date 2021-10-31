package com.kashif.ammusic.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kashif.ammusic.R
import com.kashif.ammusic.adapters.VideoAdapter
import com.kashif.ammusic.database.VideoDatabase
import com.kashif.ammusic.database.VideoModel
import com.kashif.ammusic.databinding.AddVideoDialogBinding
import com.kashif.ammusic.databinding.HomeFragmentBinding
import com.kashif.ammusic.viewModel.SharedViewModel
import com.kashif.ammusic.viewModel.SharedViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding
    private lateinit var sharedViewModel: SharedViewModel
    private var videoId: String? = null
    private val videosAdapter = VideoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater)
        initViewModel()
        setUpRecyclerView()
        getVideos()
        addClickListeners()
        addSwipeListener()

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

    private fun addClickListeners() {
        binding.apply {
            fabAddVideo.setOnClickListener {
                addVideo()
            }
        }

        videosAdapter.onVideoItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("video", it)
            }
            findNavController().navigate(R.id.action_homeFragment_to_videoFragment, bundle)
        }
    }

    private fun setUpRecyclerView() = binding.videosRv.apply {
        adapter = videosAdapter
        layoutManager = LinearLayoutManager(activity)
        setHasFixedSize(true)
    }

    private fun getVideos() {
        sharedViewModel.allVideos.observe(viewLifecycleOwner, {
            videosAdapter.submitList(it)
            binding.tvDescription.isVisible = it.isEmpty()
        })
        sharedViewModel.videoItem.observe(viewLifecycleOwner, { response ->
            val item = response.body()
            item?.let {
                videoId?.let { id ->
                    val video = VideoModel(
                        videoId = id,
                        videoTitle = it.title
                    )
                    sharedViewModel.insertVideo(video)
                    videoId = null
                }
            }
        })
    }

    private fun addVideo() {
        val dialogLayout = AddVideoDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireActivity())
        dialog.apply {
            setContentView(dialogLayout.root)
            setCanceledOnTouchOutside(true)
            setCancelable(true)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }

        dialogLayout.btnAddVideo.setOnClickListener {
            if (dialogLayout.etVideoUrl.text.isEmpty())
                Toast.makeText(activity, "Enter a url", Toast.LENGTH_SHORT).show()
            else {
                val url = dialogLayout.etVideoUrl.text.toString().trim()
                videoId = url.substring(url.lastIndexOf('/') + 1, url.length)
                lifecycleScope.launchWhenCreated {
                    if(sharedViewModel.checkVideoInDatabase(videoId!!)==null) {
                        sharedViewModel.getVideo(url)
                        dialog.dismiss()
                    } else {
                        Toast.makeText(activity,"Video already exists.",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun addSwipeListener() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                sharedViewModel.deleteVideo(videosAdapter.getVideoItem(position))
                Toast.makeText(activity,"Item Deleted.",Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(binding.videosRv)

    }

}