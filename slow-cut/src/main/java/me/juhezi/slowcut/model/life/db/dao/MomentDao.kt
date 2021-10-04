package me.juhezi.slowcut.model.life.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import me.juhezi.slowcut.model.life.db.entry.Moment

@Dao
interface MomentDao {

    // TODO: 支持分页加载逻辑
    @Query("SELECT * FROM moment")
    fun getAll(): Flowable<List<Moment>>

}