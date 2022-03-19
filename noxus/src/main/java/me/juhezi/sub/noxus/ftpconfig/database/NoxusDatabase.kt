package me.juhezi.sub.noxus.ftpconfig.database

import androidx.room.Database
import androidx.room.RoomDatabase
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigDao
import me.juhezi.sub.noxus.ftpconfig.database.userconfig.UserConfigModel

@Database(entities = [UserConfigModel::class], version = 1)
abstract class NoxusDatabase : RoomDatabase() {

    abstract fun userConfigDao(): UserConfigDao

}