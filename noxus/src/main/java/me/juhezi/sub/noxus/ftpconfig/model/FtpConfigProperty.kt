package me.juhezi.sub.noxus.ftpconfig.model

import com.google.gson.annotations.SerializedName

/**
 * 以用户为单位的属性配置
 */
data class FtpConfigProperty constructor(
    @SerializedName("key") var key: String = "",
    @SerializedName("value") var value: String = "",
    @SerializedName("desc") var desc: String = "",
    @SerializedName("type") var type: String = "String",
)

// 数据库中的存储结构




