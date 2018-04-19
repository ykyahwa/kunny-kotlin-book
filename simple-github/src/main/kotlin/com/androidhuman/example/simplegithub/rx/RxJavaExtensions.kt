package com.androidhuman.example.simplegithub.rx

import io.reactivex.disposables.Disposable

/**
 * Created by ehlee on 2018. 4. 19..
 */
operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) = this.add(disposable)