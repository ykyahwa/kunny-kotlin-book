package com.androidhuman.example.simplegithub.ui.repo

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.androidhuman.example.simplegithub.api.GithubApi

/**
 * Created by eokhyunlee on 2018. 4. 25..
 */
class RepositoryViewModelFactory(val api :GithubApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RepositoryViewModel(api) as T
    }

}