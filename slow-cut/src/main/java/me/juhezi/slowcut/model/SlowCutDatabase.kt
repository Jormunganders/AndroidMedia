package me.juhezi.slowcut.model

import androidx.room.Database
import androidx.room.RoomDatabase
import me.juhezi.slowcut.model.life.db.dao.MomentDao
import me.juhezi.slowcut.model.life.db.entry.Moment

@Database(entities = [Moment::class], version = 1)
abstract class SlowCutDatabase : RoomDatabase() {

    abstract fun momentDao(): MomentDao

}