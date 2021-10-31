package com.kashif.ammusic.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kashif.ammusic.database.VideoDatabaseDao
import java.lang.IllegalArgumentException

class SharedViewModelFactory(
    private val databaseDao: VideoDatabaseDao,
    private val application: Application
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SharedViewModel(databaseDao,application)::class.java))
            return SharedViewModel(databaseDao,application) as T

        throw IllegalArgumentException("unknown viewModel class")
    }
}