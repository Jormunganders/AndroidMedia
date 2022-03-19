package me.juhezi.sub.noxus.ftpconfig.database.userconfig

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_configs")
data class UserConfigModel(
    @PrimaryKey var key: String,
    var value: String,
    var desc: String,
    var type: String,    // 该配置类型 string/boolean/file
    var saveToFile: Boolean,    // 是否保存到文件中
    var user: String? = null,
) {
    constructor() : this("", "", "", "String", true, null)
}