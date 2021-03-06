package com.androidhuman.example.simplegithub.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import io.reactivex.Flowable

/**
 * Created by eokhyunlee on 2018. 4. 20..
 */
@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(repo:GithubRepo)

    @Query("SELECT * FROM REPOSITORIES")
    fun getHistory(): Flowable<List<GithubRepo>>

    @Query("DELETE FROM repositories")
    fun clearAll()
}