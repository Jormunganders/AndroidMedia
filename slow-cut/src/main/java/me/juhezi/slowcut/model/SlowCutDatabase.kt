package me.juhezi.slowcut.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.juhezi.slowcut.model.gala.db.dao.RemoteKeysDao
import me.juhezi.slowcut.model.gala.db.dao.RepoDao
import me.juhezi.slowcut.model.gala.db.entry.RemoteKeys
import me.juhezi.slowcut.model.gala.db.entry.Repo
import me.juhezi.slowcut.model.life.db.dao.MomentDao
import me.juhezi.slowcut.model.life.db.entry.Moment

@Database(entities = [Moment::class, Repo::class, RemoteKeys::class], version = 1)
abstract class SlowCutDatabase : RoomDatabase() {

    abstract fun momentDao(): MomentDao
    abstract fun reposDao(): RepoDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: SlowCutDatabase? = null

        fun getInstance(context: Context): SlowCutDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SlowCutDatabase::class.java, "SlowCut.db"
            ).build()

    }

}