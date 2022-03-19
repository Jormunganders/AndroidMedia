package me.juhezi.sub.noxus.ftpconfig.model

import com.google.gson.annotations.SerializedName

class FtpConfigGroup {

    @SerializedName("user_config_properties")
    var userConfigProperties: List<FtpConfigProperty> = emptyList()

    @SerializedName("common_config_properties")
    var commonConfigProperties: List<FtpConfigProperty> = emptyList()

    override fun toString(): String {
        return "FtpConfigGroup(userConfigProperties=$userConfigProperties, globalConfigProperties=$commonConfigProperties)"
    }


}