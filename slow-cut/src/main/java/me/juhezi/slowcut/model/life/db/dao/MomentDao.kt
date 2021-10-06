package me.juhezi.slowcut.model.life.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Flowable
import me.juhezi.slowcut.model.life.db.entry.Moment
import javax.sql.DataSource

@Dao
interface MomentDao {

    // TODO: 支持分页加载逻辑
    @Query("SELECT * FROM moments")
    fun getAll(): Flowable<List<Moment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(moments: List<Moment>)

    @Query("SELECT * FROM moments WHERE title LIKE :title")
    fun pagingSource(title: String): PagingSource<Int, Moment>

    @Query("DELETE FROM moments")
    suspend fun clearAll()

}