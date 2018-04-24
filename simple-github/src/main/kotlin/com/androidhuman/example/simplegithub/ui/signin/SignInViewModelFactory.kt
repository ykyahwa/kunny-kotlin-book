package com.androidhuman.example.simplegithub.ui.signin

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.androidhuman.example.simplegithub.api.AuthApi
import com.androidhuman.example.simplegithub.data.AuthTokenProvider

/**
 * Created by eokhyunlee on 2018. 4. 24..
 */
class SignInViewModelFactory(val api:AuthApi, val authTokenProvider: AuthTokenProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelclass: Class<T>) : T {
        @Suppress("UNCHECKED_CAST")
        return SignInViewModel(api, authTokenProvider) as T
    }
}