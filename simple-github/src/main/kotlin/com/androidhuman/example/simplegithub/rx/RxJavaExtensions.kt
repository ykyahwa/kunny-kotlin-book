package com.androidhuman.example.simplegithub.rx

import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by ehlee on 2018. 4. 19..
 */
operator fun AutoClearedDisposable.plusAssign(disposable: Disposable) = this.add(disposable)

fun runOnIoScheduler(func: () -> Unit) : Disposable = Completable.fromAction(func)
        .subscribeOn(Schedulers.io())
        .subscribe()