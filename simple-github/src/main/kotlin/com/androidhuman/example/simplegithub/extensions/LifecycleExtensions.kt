package com.androidhuman.example.simplegithub.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver

/**
 * Created by ehlee on 2018. 4. 19..
 */
operator fun Lifecycle.plusAssign(observer: LifecycleObserver) = this.addObserver(observer)