package com.androidhuman.example.simplegithub.rx

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by eokhyunlee on 2018. 4. 18..
 */
class AutoClearedDisposable(
        private val lifecycleOwner : AppCompatActivity,
        private val alwaysClearOnStop : Boolean = true,
        private val compositeDisposable: CompositeDisposable = CompositeDisposable())
    :LifecycleObserver {

    fun add(disposable: Disposable) {
        check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED))

        compositeDisposable.add(disposable)
    }
}