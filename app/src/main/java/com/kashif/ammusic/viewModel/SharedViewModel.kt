package com.kashif.ammusic.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kashif.ammusic.api.RetrofitInstance
import com.kashif.ammusic.api.VideoResponse
import com.kashif.ammusic.database.VideoDatabaseDao
import com.kashif.ammusic.database.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class SharedViewModel(private val database:VideoDatabaseDao, application:Application):AndroidViewModel(application) {
    val allVideos = database.getAllVideos()
    private val _videoItem = MutableLiveData<Response<VideoResponse>>()
    val videoItem : LiveData<Response<VideoResponse>> get() = _videoItem

    fun insertVideo(video:VideoModel) {
        CoroutineScope(Dispatchers.IO).launch {
            database.addVideo(video)
        }
    }

    fun deleteVideo(video: VideoModel) {
        CoroutineScope(Dispatchers.IO).launch {
            database.deleterSingleVideo(video.videoPrimaryKey)
        }
    }

    suspend fun checkVideoInDatabase(videoId:String):VideoModel? {
        return withContext(Dispatchers.IO) {
            database.getVideoWithVideoId(videoId)
        }
    }

    fun getVideo(url:String) {
        viewModelScope.launch {
            try {
                _videoItem.value= RetrofitInstance.api.getVideoDetails(url = url)
            } catch (e:Exception) {
                Log.e("viewModel",e.message.toString())
            }
        }
    }

}