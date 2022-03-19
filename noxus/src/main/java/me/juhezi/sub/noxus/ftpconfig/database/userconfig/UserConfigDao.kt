package me.juhezi.sub.noxus.ftpconfig.database.userconfig

import androidx.room.*
import io.reactivex.rxjava3.core.Observable
import java.util.*

/**
 * * 用户配置
 * * 所有配置
 * * 公共配置
 */
@Dao
interface UserConfigDao {

    /**
     * 获取所有的配置信息
     */
    @Query("SELECT * FROM user_configs")
    fun getAll(): List<UserConfigModel>

    /**
     * 获取配置记录
     */
    @Query("SELECT COUNT(*) FROM user_configs")
    fun getConfigCount(): Observable<Int>

    // 获取所有用户信息
//    @Query("SELECT user FROM user_configs WHERE user NOT NULL")
//    fun getAllUser(): List<String>

    // 获取公共的配置项ListenableFuture
//    fun getCommonConfigs(): List<UserConfigModel>

    // 获取单个用户信息
//    fun getConfigsByUser(username: String): List<UserConfigModel>

    /**
     * 更新配置项
     */
//    @Update
//    fun updateConfigs(configs: List<UserConfigModel>): Int

    // 删除用户信息
//    fun deleteUser(username: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(configs: List<UserConfigModel>)

}